package com.example.data

import android.content.Context
import com.example.data.entity.CustomDatasetEntity
import com.example.data.entity.EmergencyContactEntity
import com.example.data.entity.RouteHistoryEntity
import com.example.data.entity.SosQueueEntity
import com.example.engine.LocalAIAccessibilityEngine
import com.example.engine.LogisticsOptimizer
import com.example.engine.OfflineRoutingEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class NerAccessRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val routeHistoryDao = db.routeHistoryDao()
    private val emergencyContactDao = db.emergencyContactDao()
    private val sosQueueDao = db.sosQueueDao()
    private val customDatasetDao = db.customDatasetDao()

    val routeHistory: Flow<List<RouteHistoryEntity>> = routeHistoryDao.getAllHistory()
    val emergencyContacts: Flow<List<EmergencyContactEntity>> = emergencyContactDao.getAllContacts()
    val sosQueue: Flow<List<SosQueueEntity>> = sosQueueDao.getAllQueue()
    val customNodes: Flow<List<CustomDatasetEntity>> = customDatasetDao.getAllCustomNodes()

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        val contactCount = emergencyContactDao.count()
        if (contactCount == 0) {
            emergencyContactDao.insertAll(NerDemoData.defaultEmergencyContacts)
        }
    }

    suspend fun saveCalculatedRoute(route: RouteOption, originName: String, destName: String, preference: String) =
        withContext(Dispatchers.IO) {
            val entity = RouteHistoryEntity(
                originName = originName,
                destinationName = destName,
                routeTitle = route.title,
                distanceKm = route.distanceKm,
                durationMinutes = route.estimatedMinutes,
                accessibilityScore = route.accessibilityScore,
                difficulty = route.difficulty.label,
                preference = preference,
                pathSummary = route.pathNodes.joinToString(" → ") { it.name.substringBefore("(") }
            )
            routeHistoryDao.insert(entity)
        }

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        routeHistoryDao.deleteById(id)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        routeHistoryDao.clearAll()
    }

    suspend fun addEmergencyContact(name: String, agency: String, phone: String, isPrimary: Boolean, notes: String) =
        withContext(Dispatchers.IO) {
            emergencyContactDao.insert(
                EmergencyContactEntity(
                    name = name,
                    relationOrAgency = agency,
                    phoneNumber = phone,
                    isPrimary = isPrimary,
                    notes = notes
                )
            )
        }

    suspend fun deleteEmergencyContact(id: Long) = withContext(Dispatchers.IO) {
        emergencyContactDao.deleteById(id)
    }

    suspend fun queueOfflineSos(alertCode: String, lat: Double, lon: Double, altM: Int, message: String, target: String) =
        withContext(Dispatchers.IO) {
            sosQueueDao.insert(
                SosQueueEntity(
                    alertCode = alertCode,
                    latitude = lat,
                    longitude = lon,
                    altitudeMeters = altM,
                    messageContent = message,
                    targetRecipient = target,
                    status = "PREPARED_OFFLINE"
                )
            )
        }

    suspend fun updateSosStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        sosQueueDao.updateStatus(id, status)
    }

    suspend fun importCustomNodes(nodes: List<CustomDatasetEntity>) = withContext(Dispatchers.IO) {
        customDatasetDao.insertAll(nodes)
    }

    suspend fun clearCustomDataset() = withContext(Dispatchers.IO) {
        customDatasetDao.clearCustomNodes()
    }

    /**
     * Combines preloaded NER nodes with any locally imported nodes.
     */
    suspend fun getAllNodes(): List<LocationNode> = withContext(Dispatchers.IO) {
        val customEntities = customDatasetDao.getAllCustomNodes().first()
        val customMapped = customEntities.map { entity ->
            LocationNode(
                id = entity.nodeId,
                name = entity.name,
                state = entity.state,
                lat = entity.lat,
                lon = entity.lon,
                elevationMeters = entity.elevationM,
                category = NodeCategory.ACCESSIBLE_TRANSIT,
                isAccessible = entity.isAccessible,
                hasRamp = entity.isAccessible,
                hasElevator = false,
                stairSteps = 0,
                facilities = listOf("Custom Imported Facility"),
                verification = DataVerificationStatus.USER_REPORTED
            )
        }
        NerDemoData.nodes + customMapped
    }

    fun getAllEdges(): List<RouteEdge> = NerDemoData.edges

    suspend fun computeRoutes(
        startId: String,
        destId: String,
        preference: RoutePreference,
        reqs: AccessibilityRequirements
    ): List<RouteOption> = withContext(Dispatchers.Default) {
        val nodes = getAllNodes()
        val engine = OfflineRoutingEngine(nodes, getAllEdges())
        engine.calculateRoutes(startId, destId, preference, reqs)
    }

    suspend fun evaluateAccessibility(nodes: List<LocationNode>): AccessibilityAnalysis =
        withContext(Dispatchers.Default) {
            LocalAIAccessibilityEngine.analyzePath(nodes, getAllEdges())
        }

    suspend fun optimizeLogistics(stops: List<LogisticsStop>): LogisticsPlan =
        withContext(Dispatchers.Default) {
            val nodes = getAllNodes()
            LogisticsOptimizer.optimizeStops(stops, nodes)
        }
}
