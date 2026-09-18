package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import com.example.data.entity.EmergencyContactEntity
import com.example.data.entity.RouteHistoryEntity
import com.example.data.entity.SosQueueEntity
import com.example.engine.LocalAIAccessibilityEngine
import com.example.engine.LogisticsOptimizer
import com.example.engine.OfflineRoutingEngine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NER ACCESS", appName)
    }

    @Test
    fun `offline routing engine calculates multiple routes between Guwahati and Shillong`() {
        val engine = OfflineRoutingEngine(NerDemoData.nodes, NerDemoData.edges)
        val routes = engine.calculateRoutes(
            startId = "GHY_01",
            destId = "SHL_08",
            preference = RoutePreference.FASTEST,
            reqs = AccessibilityRequirements()
        )

        assertTrue("Should produce at least one route", routes.isNotEmpty())
        val primary = routes.first()
        assertTrue("Distance should be greater than 50km", primary.distanceKm > 50.0)
        assertTrue("Duration should be greater than 60 min", primary.estimatedMinutes > 60)
        assertEquals("GHY_01", primary.pathNodes.first().id)
        assertEquals("SHL_08", primary.pathNodes.last().id)
    }

    @Test
    fun `accessibility engine computes gradient risks and scores properly`() {
        val pathNodes = NerDemoData.nodes.filter { it.id in listOf("GHY_01", "GHY_03", "NGB_05", "SHL_06", "SHL_08") }
        val analysis = LocalAIAccessibilityEngine.analyzePath(pathNodes, NerDemoData.edges)

        assertNotNull(analysis)
        assertTrue("Should have numeric accessibility score", analysis.numericScore in 1..100)
        assertTrue("Should have identified risks or confirmation", analysis.identifiedRisks.isNotEmpty())
        assertEquals(100, analysis.verifiedDatasetPercent)
    }

    @Test
    fun `logistics optimizer prioritizes critical medical consignments`() {
        val stops = NerDemoData.sampleLogisticsStops
        val plan = LogisticsOptimizer.optimizeStops(stops, NerDemoData.nodes)

        assertNotNull(plan)
        assertTrue("Should have stops in sequence", plan.orderedStops.isNotEmpty())
        assertTrue("Warehouse should be first stop", plan.orderedStops.first().type == StopType.WAREHOUSE_HUB)
        assertTrue("Critical medical items should be prioritized", plan.priorityDeliveriesCount > 0)
        assertTrue("Efficiency score should be high", plan.efficiencyScore > 70)
    }

    @Test
    fun `room database offline persistence works properly`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)

        // Test Route History DAO
        val historyDao = db.routeHistoryDao()
        val historyItem = RouteHistoryEntity(
            originName = "Guwahati Central",
            destinationName = "NEIGRIHMS Shillong",
            routeTitle = "Route A (Fastest Route)",
            distanceKm = 98.4,
            durationMinutes = 142,
            accessibilityScore = 88,
            difficulty = "Moderate Incline",
            preference = "Fastest Route",
            pathSummary = "Guwahati → Khanapara → Nongpoh → NEIGRIHMS"
        )
        val historyId = historyDao.insert(historyItem)
        assertTrue(historyId > 0)

        val retrievedList = historyDao.getAllHistory().first()
        assertTrue(retrievedList.any { it.originName == "Guwahati Central" })

        // Test SOS Queue DAO
        val sosDao = db.sosQueueDao()
        val sosItem = SosQueueEntity(
            alertCode = "NER-SOS-99123",
            latitude = 26.1833,
            longitude = 91.7450,
            altitudeMeters = 55,
            messageContent = "Emergency SOS Test message",
            targetRecipient = "1070",
            status = "PREPARED_OFFLINE"
        )
        val sosId = sosDao.insert(sosItem)
        assertTrue(sosId > 0)

        val queuedList = sosDao.getAllQueue().first()
        assertTrue(queuedList.any { it.alertCode == "NER-SOS-99123" })
    }
}
