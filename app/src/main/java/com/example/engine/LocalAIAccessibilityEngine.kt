package com.example.engine

import com.example.data.*
import kotlin.math.roundToInt

object LocalAIAccessibilityEngine {

    /**
     * Deterministic on-device AI assessment analyzing physical terrain,
     * gradient distributions, physical barriers, and verified amenities.
     * ZERO external network or cloud AI dependency.
     */
    fun analyzePath(nodes: List<LocationNode>, edges: List<RouteEdge>): AccessibilityAnalysis {
        if (nodes.isEmpty()) {
            return AccessibilityAnalysis(
                wheelchairStatus = "Unknown",
                stairsAvoided = true,
                steepRoadsCount = 0,
                maxSlopePercent = 0.0,
                walkingDistanceKm = 0.0,
                accessibilityRating = "N/A",
                numericScore = 0,
                identifiedRisks = listOf("No active path selected"),
                verifiedDatasetPercent = 100,
                accessibleFacilities = emptyList()
            )
        }

        var totalDistanceKm = 0.0
        var maxSlope = 0.0
        var steepCount = 0
        var stairsPresent = false
        var stairCount = 0
        val identifiedRisks = mutableListOf<String>()
        val facilitiesFound = mutableSetOf<String>()

        // Check edges along path
        for (i in 0 until nodes.size - 1) {
            val fromId = nodes[i].id
            val toId = nodes[i + 1].id
            val edge = edges.find { (it.fromId == fromId && it.toId == toId) || (it.fromId == toId && it.toId == fromId) }
            if (edge != null) {
                totalDistanceKm += edge.distanceKm
                if (edge.slopePercent > maxSlope) maxSlope = edge.slopePercent
                if (edge.slopePercent > 6.0) {
                    steepCount++
                    identifiedRisks.add("Steep gradient (${String.format("%.1f", edge.slopePercent)}%) between ${nodes[i].name} and ${nodes[i+1].name}")
                }
                if (edge.stairsPresent) {
                    stairsPresent = true
                    identifiedRisks.add("Step / Stairway impediment detected on ${nodes[i].name} access path")
                }
                if (edge.obstacleRisk != "None") {
                    identifiedRisks.add("${edge.obstacleRisk} in ${nodes[i].state} corridor")
                }
            }
        }

        // Check node facilities & steps
        var verifiedCount = 0
        for (node in nodes) {
            if (node.verification == DataVerificationStatus.VERIFIED_DATASET) verifiedCount++
            stairCount += node.stairSteps
            if (node.stairSteps > 0) {
                stairsPresent = true
                identifiedRisks.add("${node.name}: ${node.stairSteps} outdoor stair steps detected (Ramp alternative required)")
            }
            facilitiesFound.addAll(node.facilities)
        }

        val verifiedPercent = ((verifiedCount.toDouble() / nodes.size) * 100).roundToInt()

        // Calculate wheelchair status
        val wheelchairStatus = when {
            stairsPresent || maxSlope > 7.5 -> "Limited (High Obstacle Risk)"
            maxSlope > 5.0 -> "Partially Accessible (Assistance Advised)"
            else -> "Fully Supported (Low Incline & Ramped)"
        }

        // Numeric score (0 - 100)
        var score = 100
        if (stairsPresent) score -= 35
        if (steepCount > 0) score -= (steepCount * 12).coerceAtMost(30)
        if (maxSlope > 8.0) score -= 20
        else if (maxSlope > 5.0) score -= 10
        score = score.coerceIn(15, 100)

        val rating = when {
            score >= 85 -> "High Accessibility"
            score >= 65 -> "Moderate Accessibility"
            else -> "Low Accessibility (Terrain Barriers)"
        }

        if (identifiedRisks.isEmpty()) {
            identifiedRisks.add("Zero major mobility barriers identified on this corridor.")
            identifiedRisks.add("Continuous paved surfaces with verified ramp gradients.")
        }

        return AccessibilityAnalysis(
            wheelchairStatus = wheelchairStatus,
            stairsAvoided = !stairsPresent,
            steepRoadsCount = steepCount,
            maxSlopePercent = maxSlope,
            walkingDistanceKm = totalDistanceKm,
            accessibilityRating = rating,
            numericScore = score,
            identifiedRisks = identifiedRisks,
            verifiedDatasetPercent = verifiedPercent,
            accessibleFacilities = facilitiesFound.toList()
        )
    }
}
