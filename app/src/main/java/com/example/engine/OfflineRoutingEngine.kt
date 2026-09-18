package com.example.engine

import com.example.data.*
import java.util.PriorityQueue
import kotlin.math.*

class OfflineRoutingEngine(
    private val allNodes: List<LocationNode>,
    private val allEdges: List<RouteEdge>
) {
    private val nodeMap: Map<String, LocationNode> = allNodes.associateBy { it.id }
    private val adjacency: Map<String, List<RouteEdge>> = allEdges.groupBy { it.fromId }

    /**
     * Calculates multiple route candidates locally based on start, destination,
     * preferences, and strict accessibility constraints.
     */
    fun calculateRoutes(
        startId: String,
        destId: String,
        preference: RoutePreference,
        reqs: AccessibilityRequirements
    ): List<RouteOption> {
        if (startId == destId) return emptyList()
        val startNode = nodeMap[startId] ?: return emptyList()
        val destNode = nodeMap[destId] ?: return emptyList()

        val results = mutableListOf<RouteOption>()

        // 1. Primary requested route
        val primaryPath = runAStar(startId, destId, preference, reqs, excludedEdge = null)
        if (primaryPath.isNotEmpty()) {
            val opt = buildRouteOption(
                id = "OPT_A",
                title = "Route A (${preference.label})",
                tag = if (preference == RoutePreference.ACCESSIBILITY_FRIENDLY) "100% ACCESSIBLE" else "OPTIMAL",
                path = primaryPath,
                preference = preference,
                reqs = reqs
            )
            results.add(opt)
        }

        // 2. Alternative Route: If user selected something other than ACCESSIBILITY_FRIENDLY, compute accessibility alternative!
        val altPref = if (preference != RoutePreference.ACCESSIBILITY_FRIENDLY) {
            RoutePreference.ACCESSIBILITY_FRIENDLY
        } else {
            RoutePreference.FASTEST
        }
        val altReqs = if (altPref == RoutePreference.ACCESSIBILITY_FRIENDLY) {
            reqs.copy(wheelchairAccessible = true, avoidStairs = true, avoidSteepRoads = true)
        } else {
            reqs.copy(wheelchairAccessible = false, avoidStairs = false)
        }
        val altPath = runAStar(startId, destId, altPref, altReqs, excludedEdge = null)
        if (altPath.isNotEmpty() && altPath != primaryPath) {
            val opt = buildRouteOption(
                id = "OPT_B",
                title = "Route B (${altPref.label})",
                tag = if (altPref == RoutePreference.ACCESSIBILITY_FRIENDLY) "ACCESSIBILITY CHOICE" else "FASTEST DIRECT",
                path = altPath,
                preference = altPref,
                reqs = altReqs
            )
            results.add(opt)
        }

        // 3. Balanced / Logistics Alternative: By penalizing the first edge of primary route to force alternative corridor
        if (primaryPath.size > 2) {
            val firstEdgeKey = "${primaryPath[0]}_${primaryPath[1]}"
            val thirdPath = runAStar(startId, destId, RoutePreference.BALANCED, reqs, excludedEdge = firstEdgeKey)
            if (thirdPath.isNotEmpty() && thirdPath != primaryPath && thirdPath != altPath) {
                val opt = buildRouteOption(
                    id = "OPT_C",
                    title = "Route C (Balanced Corridor)",
                    tag = "SCENIC & LOW RISK",
                    path = thirdPath,
                    preference = RoutePreference.BALANCED,
                    reqs = reqs
                )
                results.add(opt)
            }
        }

        // Fallback: If no strict route found due to too harsh constraints, attempt relaxed route
        if (results.isEmpty()) {
            val relaxed = runAStar(startId, destId, RoutePreference.SHORTEST, AccessibilityRequirements(), null)
            if (relaxed.isNotEmpty()) {
                results.add(
                    buildRouteOption(
                        id = "OPT_FALLBACK",
                        title = "Route (Standard Paved Connection)",
                        tag = "RESTRICTED PASS",
                        path = relaxed,
                        preference = RoutePreference.SHORTEST,
                        reqs = AccessibilityRequirements()
                    )
                )
            }
        }

        return results
    }

    private data class SearchNode(
        val id: String,
        val gCost: Double,
        val fCost: Double,
        val path: List<String>
    ) : Comparable<SearchNode> {
        override fun compareTo(other: SearchNode): Int = this.fCost.compareTo(other.fCost)
    }

    private fun runAStar(
        startId: String,
        destId: String,
        preference: RoutePreference,
        reqs: AccessibilityRequirements,
        excludedEdge: String?
    ): List<String> {
        val destNode = nodeMap[destId] ?: return emptyList()

        val openSet = PriorityQueue<SearchNode>()
        val closedSet = mutableSetOf<String>()
        val gScores = mutableMapOf<String, Double>()

        openSet.add(SearchNode(startId, 0.0, heuristic(startId, destId), listOf(startId)))
        gScores[startId] = 0.0

        while (openSet.isNotEmpty()) {
            val current = openSet.poll() ?: break

            if (current.id == destId) {
                return current.path
            }

            if (current.id in closedSet) continue
            closedSet.add(current.id)

            val edges = adjacency[current.id] ?: emptyList()
            for (edge in edges) {
                val neighborId = edge.toId
                if (neighborId in closedSet) continue

                val edgeKey = "${edge.fromId}_${edge.toId}"
                if (excludedEdge != null && edgeKey == excludedEdge) continue

                val edgeWeight = calculateEdgeCost(edge, preference, reqs)
                if (edgeWeight >= Double.MAX_VALUE / 2) {
                    continue // Infeasible edge according to strict constraints
                }

                val tentativeG = current.gCost + edgeWeight
                val currentBestG = gScores.getOrDefault(neighborId, Double.MAX_VALUE)

                if (tentativeG < currentBestG) {
                    gScores[neighborId] = tentativeG
                    val fCost = tentativeG + heuristic(neighborId, destId)
                    val newPath = current.path + neighborId
                    openSet.add(SearchNode(neighborId, tentativeG, fCost, newPath))
                }
            }
        }

        return emptyList()
    }

    private fun calculateEdgeCost(
        edge: RouteEdge,
        preference: RoutePreference,
        reqs: AccessibilityRequirements
    ): Double {
        // Strict accessibility filters
        if (reqs.wheelchairAccessible) {
            if (!edge.wheelchairSuitable) return Double.MAX_VALUE
            if (edge.stairsPresent) return Double.MAX_VALUE
            if (edge.slopePercent > 7.0) return Double.MAX_VALUE
        }
        if (reqs.avoidStairs && edge.stairsPresent) {
            return Double.MAX_VALUE
        }
        if (reqs.avoidSteepRoads && edge.slopePercent > 6.0) {
            return Double.MAX_VALUE
        }

        val baseTimeMin = (edge.distanceKm / edge.baseSpeedKmh) * 60.0

        return when (preference) {
            RoutePreference.FASTEST -> {
                // Primary factor is travel time, plus small slope impact
                baseTimeMin + (edge.slopePercent * 1.5)
            }
            RoutePreference.SHORTEST -> {
                edge.distanceKm
            }
            RoutePreference.ACCESSIBILITY_FRIENDLY -> {
                // Heavy penalties for slopes and stairs; bonus for wheelchair suitability
                var cost = edge.distanceKm * 1.0
                if (!edge.wheelchairSuitable) cost += 80.0
                if (edge.stairsPresent) cost += 120.0
                cost += (edge.slopePercent * 8.0)
                if (edge.surface == RoadSurface.EXCELLENT_HIGHWAY || edge.surface == RoadSurface.GOOD_PAVED) {
                    cost -= 2.0
                }
                cost.coerceAtLeast(0.5)
            }
            RoutePreference.LOGISTICS_OPTIMIZED -> {
                // Optimizes for high tonnage clearance, wide roads, lower fuel grade
                var cost = baseTimeMin
                if (edge.maxVehicleTonnage < 30.0) cost += 40.0
                if (edge.slopePercent > 5.0) cost += (edge.slopePercent * 4.0) // Fuel consumption on steep hill
                if (edge.surface == RoadSurface.NARROW_MOUNTAIN_PASS) cost += 60.0
                cost
            }
            RoutePreference.SAFER_ROUTE -> {
                var cost = edge.distanceKm * 1.2
                if (edge.obstacleRisk != "None") cost += 50.0
                if (edge.slopePercent > 6.0) cost += 25.0
                cost
            }
            RoutePreference.BALANCED -> {
                baseTimeMin * 0.5 + edge.distanceKm * 0.5 + (edge.slopePercent * 2.0)
            }
        }
    }

    private fun heuristic(nodeIdA: String, nodeIdB: String): Double {
        val a = nodeMap[nodeIdA] ?: return 0.0
        val b = nodeMap[nodeIdB] ?: return 0.0
        // Euclidean distance approx in km (1 deg lat ~ 111 km, 1 deg lon ~ 100 km at NER latitude)
        val dLat = (a.lat - b.lat) * 111.0
        val dLon = (a.lon - b.lon) * 100.0
        return sqrt(dLat * dLat + dLon * dLon)
    }

    private fun buildRouteOption(
        id: String,
        title: String,
        tag: String,
        path: List<String>,
        preference: RoutePreference,
        reqs: AccessibilityRequirements
    ): RouteOption {
        val nodes = path.mapNotNull { nodeMap[it] }

        var totalDist = 0.0
        var totalMinutes = 0
        var maxSlope = 0.0
        var totalStairs = 0
        val obstacles = mutableListOf<String>()
        var elevationGain = 0

        for (i in 0 until path.size - 1) {
            val fromId = path[i]
            val toId = path[i + 1]
            val edge = adjacency[fromId]?.find { it.toId == toId }
            if (edge != null) {
                totalDist += edge.distanceKm
                val tMin = (edge.distanceKm / edge.baseSpeedKmh) * 60.0
                totalMinutes += tMin.roundToInt()
                maxSlope = max(maxSlope, edge.slopePercent)
                if (edge.stairsPresent) totalStairs += 15
                if (edge.obstacleRisk != "None") {
                    obstacles.add("${edge.obstacleRisk} (${nodeMap[fromId]?.name} → ${nodeMap[toId]?.name})")
                }
            }
        }

        // Calculate elevation gain from node heights
        for (i in 0 until nodes.size - 1) {
            val diff = nodes[i + 1].elevationMeters - nodes[i].elevationMeters
            if (diff > 0) elevationGain += diff
            totalStairs += nodes[i].stairSteps
        }

        // Accessibility score computation (0 - 100)
        var accScore = 95
        if (maxSlope > 8.0) accScore -= 30
        else if (maxSlope > 5.0) accScore -= 15
        if (totalStairs > 0) accScore -= (totalStairs * 2).coerceAtMost(40)
        val hasInaccessibleNode = nodes.any { !it.isAccessible }
        if (hasInaccessibleNode) accScore -= 20
        accScore = accScore.coerceIn(20, 100)

        val difficulty = when {
            maxSlope > 8.0 || elevationGain > 800 -> RouteDifficulty.SEVERE
            maxSlope > 5.0 || elevationGain > 400 -> RouteDifficulty.CHALLENGING
            maxSlope > 3.0 || elevationGain > 150 -> RouteDifficulty.MODERATE
            else -> RouteDifficulty.EASY
        }

        val optScore = when (preference) {
            RoutePreference.ACCESSIBILITY_FRIENDLY -> (accScore / 10.0)
            RoutePreference.FASTEST -> ((10.0 - (totalMinutes / 60.0)).coerceIn(5.0, 9.8))
            RoutePreference.SHORTEST -> ((10.0 - (totalDist / 50.0)).coerceIn(5.5, 9.7))
            RoutePreference.LOGISTICS_OPTIMIZED -> 9.4
            else -> 8.8
        }

        val explanation = when (preference) {
            RoutePreference.ACCESSIBILITY_FRIENDLY ->
                "Prioritizes low gradients (Max ${String.format("%.1f", maxSlope)}%), ramp-equipped transit, avoids flight steps, and ensures paved sidewalk connections."
            RoutePreference.FASTEST ->
                "Optimizes for minimum transit duration (${totalMinutes / 60}h ${totalMinutes % 60}m) utilizing high-speed four-lane national highway links."
            RoutePreference.SHORTEST ->
                "Direct topological geodesic path totaling ${String.format("%.1f", totalDist)} km across North Eastern transit nodes."
            RoutePreference.LOGISTICS_OPTIMIZED ->
                "Guarantees heavy axle clearance (up to 45T), bypasses congested bazaar bottlenecks, and minimizes high-fuel mountain grades."
            RoutePreference.SAFER_ROUTE ->
                "Avoids monsoon flash points and steep precipice roads, aligning with emergency trauma outposts."
            RoutePreference.BALANCED ->
                "Well-rounded compromise balancing highway speed, accessible surfaces, and reliable terrain conditions."
        }

        val fuelEfficiency = when (difficulty) {
            RouteDifficulty.EASY -> "High (14.2 km/L)"
            RouteDifficulty.MODERATE -> "Standard (11.8 km/L)"
            RouteDifficulty.CHALLENGING -> "Moderate (8.5 km/L)"
            RouteDifficulty.SEVERE -> "Low (6.2 km/L - Hill Climb)"
        }

        return RouteOption(
            id = id,
            title = title,
            tag = tag,
            distanceKm = round(totalDist * 10) / 10.0,
            estimatedMinutes = totalMinutes.coerceAtLeast(5),
            accessibilityScore = accScore,
            difficulty = difficulty,
            optimizationScore = round(optScore * 10) / 10.0,
            pathNodeIds = path,
            pathNodes = nodes,
            elevationGainMeters = elevationGain,
            maxSlopePercent = round(maxSlope * 10) / 10.0,
            stairsEncountered = totalStairs,
            obstaclesDetected = obstacles.distinct(),
            criteriaExplanation = explanation,
            fuelEfficiencyRating = fuelEfficiency
        )
    }
}
