package com.example.data

enum class NodeCategory {
    CITY,
    LOGISTICS_HUB,
    EMERGENCY_HOSPITAL,
    HILL_PASS,
    REST_POINT,
    ACCESSIBLE_TRANSIT
}

enum class DataVerificationStatus {
    VERIFIED_DATASET,
    USER_REPORTED,
    UNKNOWN
}

data class LocationNode(
    val id: String,
    val name: String,
    val state: String,
    val lat: Double,
    val lon: Double,
    val elevationMeters: Int,
    val category: NodeCategory,
    val isAccessible: Boolean,
    val hasRamp: Boolean,
    val hasElevator: Boolean,
    val stairSteps: Int,
    val facilities: List<String>,
    val verification: DataVerificationStatus
)

enum class RoadSurface {
    EXCELLENT_HIGHWAY,
    GOOD_PAVED,
    MODERATE_HILL_ROAD,
    ROUGH_TERRAIN,
    NARROW_MOUNTAIN_PASS
}

data class RouteEdge(
    val fromId: String,
    val toId: String,
    val distanceKm: Double,
    val baseSpeedKmh: Double,
    val slopePercent: Double,
    val surface: RoadSurface,
    val wheelchairSuitable: Boolean,
    val stairsPresent: Boolean,
    val maxVehicleTonnage: Double,
    val maxVehicleHeightMeters: Double,
    val obstacleRisk: String
)

enum class RoutePreference(val label: String) {
    FASTEST("Fastest Route"),
    SHORTEST("Shortest Route"),
    ACCESSIBILITY_FRIENDLY("Accessibility-Friendly"),
    SAFER_ROUTE("Safer Route"),
    LOGISTICS_OPTIMIZED("Logistics-Optimized"),
    BALANCED("Balanced Route")
}

data class AccessibilityRequirements(
    val wheelchairAccessible: Boolean = false,
    val avoidStairs: Boolean = false,
    val avoidSteepRoads: Boolean = false,
    val lowWalkingDistance: Boolean = false,
    val accessibleEntry: Boolean = false,
    val accessibleRestStops: Boolean = false
)

enum class RouteDifficulty(val label: String) {
    EASY("Easy (Paved / Flat)"),
    MODERATE("Moderate (Gentle Slope)"),
    CHALLENGING("Challenging (Steep Incline)"),
    SEVERE("Severe (Mountain Pass)")
}

data class RouteOption(
    val id: String,
    val title: String,
    val tag: String,
    val distanceKm: Double,
    val estimatedMinutes: Int,
    val accessibilityScore: Int,
    val difficulty: RouteDifficulty,
    val optimizationScore: Double,
    val pathNodeIds: List<String>,
    val pathNodes: List<LocationNode>,
    val elevationGainMeters: Int,
    val maxSlopePercent: Double,
    val stairsEncountered: Int,
    val obstaclesDetected: List<String>,
    val criteriaExplanation: String,
    val fuelEfficiencyRating: String
)

enum class StopType(val label: String) {
    WAREHOUSE_HUB("Warehouse Origin"),
    PICKUP("Cargo Pickup"),
    DELIVERY("Consignment Delivery")
}

enum class StopPriority(val label: String, val level: Int) {
    CRITICAL_MEDICAL("Critical Medical", 4),
    HIGH("High Priority", 3),
    MEDIUM("Standard Medium", 2),
    LOW("Flexible Low", 1)
}

data class LogisticsStop(
    val id: String,
    val name: String,
    val stateLocation: String,
    val type: StopType,
    val priority: StopPriority,
    val timeWindow: String,
    val weightKg: Double,
    val accessibilityRequirement: String,
    val completed: Boolean = false
)

data class LogisticsPlan(
    val orderedStops: List<LogisticsStop>,
    val totalDistanceKm: Double,
    val estimatedHours: Double,
    val totalStops: Int,
    val priorityDeliveriesCount: Int,
    val efficiencyScore: Int,
    val freightClearanceStatus: String,
    val summary: String
)

data class AccessibilityAnalysis(
    val wheelchairStatus: String,
    val stairsAvoided: Boolean,
    val steepRoadsCount: Int,
    val maxSlopePercent: Double,
    val walkingDistanceKm: Double,
    val accessibilityRating: String,
    val numericScore: Int,
    val identifiedRisks: List<String>,
    val verifiedDatasetPercent: Int,
    val accessibleFacilities: List<String>
)

data class DatasetInfo(
    val id: String,
    val name: String,
    val category: String,
    val status: String,
    val sizeMb: Double,
    val recordCount: Int,
    val lastUpdated: String,
    val description: String
)

data class EmergencyOutpost(
    val id: String,
    val name: String,
    val city: String,
    val type: String,
    val phone: String,
    val lat: Double,
    val lon: Double,
    val altitudeM: Int
)
