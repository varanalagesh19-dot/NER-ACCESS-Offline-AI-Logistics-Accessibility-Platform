package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.NerAccessTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
            val isOfflineDemoMode by viewModel.isOfflineDemoMode.collectAsStateWithLifecycle()
            val offlineStatusMode by viewModel.offlineStatusMode.collectAsStateWithLifecycle()

            val nodes by viewModel.nodes.collectAsStateWithLifecycle()
            val edges = viewModel.edges

            val startNodeId by viewModel.startNodeId.collectAsStateWithLifecycle()
            val destNodeId by viewModel.destNodeId.collectAsStateWithLifecycle()
            val routePreference by viewModel.routePreference.collectAsStateWithLifecycle()
            val accessibilityReqs by viewModel.accessibilityReqs.collectAsStateWithLifecycle()
            val routeOptions by viewModel.routeOptions.collectAsStateWithLifecycle()
            val selectedRouteOption by viewModel.selectedRouteOption.collectAsStateWithLifecycle()
            val isCalculatingRoute by viewModel.isCalculatingRoute.collectAsStateWithLifecycle()

            val logisticsStops by viewModel.logisticsStops.collectAsStateWithLifecycle()
            val logisticsPlan by viewModel.logisticsPlan.collectAsStateWithLifecycle()
            val isOptimizingLogistics by viewModel.isOptimizingLogistics.collectAsStateWithLifecycle()

            val routeHistory by viewModel.routeHistory.collectAsStateWithLifecycle()
            val emergencyContacts by viewModel.emergencyContacts.collectAsStateWithLifecycle()
            val sosQueue by viewModel.sosQueue.collectAsStateWithLifecycle()
            val customNodes by viewModel.customNodes.collectAsStateWithLifecycle()

            NerAccessTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopHeaderBar(
                            isOfflineDemoMode = isOfflineDemoMode,
                            onToggleDemoMode = { viewModel.toggleOfflineDemoMode() },
                            statusMode = offlineStatusMode,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { viewModel.toggleDarkTheme() },
                            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentDestination = currentDestination,
                            onSelectDestination = { viewModel.setDestination(it) },
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentDestination) {
                            AppDestination.DASHBOARD -> {
                                DashboardScreen(
                                    nodes = nodes,
                                    edges = edges,
                                    onNavigate = { viewModel.setDestination(it) },
                                    onQuickStartRoute = { start, dest ->
                                        viewModel.quickStartRoute(start, dest)
                                    }
                                )
                            }
                            AppDestination.PLAN_ROUTE -> {
                                RoutePlannerScreen(
                                    nodes = nodes,
                                    edges = edges,
                                    startNodeId = startNodeId,
                                    destNodeId = destNodeId,
                                    onStartNodeChange = { viewModel.setStartNode(it) },
                                    onDestNodeChange = { viewModel.setDestNode(it) },
                                    preference = routePreference,
                                    onPreferenceChange = { viewModel.setRoutePreference(it) },
                                    accessibilityReqs = accessibilityReqs,
                                    onReqsChange = { viewModel.setAccessibilityReqs(it) },
                                    routeOptions = routeOptions,
                                    selectedRouteOption = selectedRouteOption,
                                    onSelectRouteOption = { viewModel.selectRouteOption(it) },
                                    isCalculating = isCalculatingRoute,
                                    onCalculateRoute = { viewModel.calculateRoute() },
                                    onSaveRouteToHistory = { viewModel.saveRouteToHistory(it) }
                                )
                            }
                            AppDestination.ACCESSIBILITY -> {
                                AccessibilityScreen(
                                    currentRoute = selectedRouteOption,
                                    allNodes = nodes,
                                    allEdges = edges
                                )
                            }
                            AppDestination.LOGISTICS -> {
                                LogisticsScreen(
                                    stops = logisticsStops,
                                    logisticsPlan = logisticsPlan,
                                    onAddStop = { viewModel.addLogisticsStop(it) },
                                    onRemoveStop = { viewModel.removeLogisticsStop(it) },
                                    onToggleStopComplete = { viewModel.toggleLogisticsStopComplete(it) },
                                    onOptimizeLogistics = { viewModel.optimizeLogistics() },
                                    isOptimizing = isOptimizingLogistics
                                )
                            }
                            AppDestination.SOS -> {
                                SosScreen(
                                    contacts = emergencyContacts,
                                    sosQueue = sosQueue,
                                    onTriggerSos = { code, lat, lon, alt, msg, target ->
                                        viewModel.triggerSos(code, lat, lon, alt, msg, target)
                                    },
                                    onAddContact = { name, agency, phone, primary, notes ->
                                        viewModel.addEmergencyContact(name, agency, phone, primary, notes)
                                    },
                                    onDeleteContact = { viewModel.deleteEmergencyContact(it) },
                                    onUpdateSosStatus = { id, status ->
                                        viewModel.updateSosStatus(id, status)
                                    }
                                )
                            }
                            AppDestination.OFFLINE_DATA -> {
                                OfflineDataScreen(
                                    nodes = nodes,
                                    edges = edges,
                                    customNodes = customNodes,
                                    onImportCustomNodes = { viewModel.importCustomNodes(it) },
                                    onClearCustomData = { viewModel.clearCustomData() },
                                    onClearCache = { viewModel.clearCache() }
                                )
                            }
                            AppDestination.HISTORY -> {
                                HistoryScreen(
                                    historyList = routeHistory,
                                    onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                                    onClearAllHistory = { viewModel.clearAllHistory() }
                                )
                            }
                            AppDestination.SETTINGS -> {
                                SettingsScreen(
                                    isOfflineDemoMode = isOfflineDemoMode,
                                    onToggleOfflineDemoMode = { viewModel.toggleOfflineDemoMode() },
                                    isDarkTheme = isDarkTheme,
                                    onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                                    onClearAllData = {
                                        viewModel.clearAllHistory()
                                        viewModel.clearCustomData()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
