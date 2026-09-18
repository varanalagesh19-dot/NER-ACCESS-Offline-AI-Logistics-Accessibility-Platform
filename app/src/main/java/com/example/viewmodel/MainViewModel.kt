package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.entity.CustomDatasetEntity
import com.example.data.entity.EmergencyContactEntity
import com.example.data.entity.RouteHistoryEntity
import com.example.data.entity.SosQueueEntity
import com.example.ui.components.AppDestination
import com.example.ui.components.OfflineStatusMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NerAccessRepository(application.applicationContext)

    private val _currentDestination = MutableStateFlow(AppDestination.DASHBOARD)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _isOfflineDemoMode = MutableStateFlow(true)
    val isOfflineDemoMode: StateFlow<Boolean> = _isOfflineDemoMode.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _nodes = MutableStateFlow<List<LocationNode>>(NerDemoData.nodes)
    val nodes: StateFlow<List<LocationNode>> = _nodes.asStateFlow()

    val edges: List<RouteEdge> = repository.getAllEdges()

    // Route Planner State
    private val _startNodeId = MutableStateFlow("GHY_01") // Guwahati Railway Central
    val startNodeId: StateFlow<String> = _startNodeId.asStateFlow()

    private val _destNodeId = MutableStateFlow("SHL_08") // NEIGRIHMS Super Speciality Shillong
    val destNodeId: StateFlow<String> = _destNodeId.asStateFlow()

    private val _routePreference = MutableStateFlow(RoutePreference.ACCESSIBILITY_FRIENDLY)
    val routePreference: StateFlow<RoutePreference> = _routePreference.asStateFlow()

    private val _accessibilityReqs = MutableStateFlow(AccessibilityRequirements(wheelchairAccessible = true, avoidStairs = true))
    val accessibilityReqs: StateFlow<AccessibilityRequirements> = _accessibilityReqs.asStateFlow()

    private val _routeOptions = MutableStateFlow<List<RouteOption>>(emptyList())
    val routeOptions: StateFlow<List<RouteOption>> = _routeOptions.asStateFlow()

    private val _selectedRouteOption = MutableStateFlow<RouteOption?>(null)
    val selectedRouteOption: StateFlow<RouteOption?> = _selectedRouteOption.asStateFlow()

    private val _isCalculatingRoute = MutableStateFlow(false)
    val isCalculatingRoute: StateFlow<Boolean> = _isCalculatingRoute.asStateFlow()

    // Logistics State
    private val _logisticsStops = MutableStateFlow<List<LogisticsStop>>(NerDemoData.sampleLogisticsStops)
    val logisticsStops: StateFlow<List<LogisticsStop>> = _logisticsStops.asStateFlow()

    private val _logisticsPlan = MutableStateFlow<LogisticsPlan?>(null)
    val logisticsPlan: StateFlow<LogisticsPlan?> = _logisticsPlan.asStateFlow()

    private val _isOptimizingLogistics = MutableStateFlow(false)
    val isOptimizingLogistics: StateFlow<Boolean> = _isOptimizingLogistics.asStateFlow()

    // Room Database Flows
    val routeHistory: StateFlow<List<RouteHistoryEntity>> = repository.routeHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emergencyContacts: StateFlow<List<EmergencyContactEntity>> = repository.emergencyContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sosQueue: StateFlow<List<SosQueueEntity>> = repository.sosQueue
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customNodes: StateFlow<List<CustomDatasetEntity>> = repository.customNodes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val offlineStatusMode: StateFlow<OfflineStatusMode> = _isOfflineDemoMode.map { offline ->
        if (offline) OfflineStatusMode.OFFLINE_READY else OfflineStatusMode.ONLINE_ENHANCEMENT
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OfflineStatusMode.OFFLINE_READY)

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            refreshNodes()
            // Run initial route calculation for demo convenience
            calculateRoute()
            optimizeLogistics()
        }
    }

    private suspend fun refreshNodes() {
        _nodes.value = repository.getAllNodes()
    }

    fun setDestination(destination: AppDestination) {
        _currentDestination.value = destination
    }

    fun toggleOfflineDemoMode() {
        _isOfflineDemoMode.value = !_isOfflineDemoMode.value
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setStartNode(id: String) {
        _startNodeId.value = id
    }

    fun setDestNode(id: String) {
        _destNodeId.value = id
    }

    fun setRoutePreference(preference: RoutePreference) {
        _routePreference.value = preference
    }

    fun setAccessibilityReqs(reqs: AccessibilityRequirements) {
        _accessibilityReqs.value = reqs
    }

    fun selectRouteOption(option: RouteOption) {
        _selectedRouteOption.value = option
    }

    fun calculateRoute() {
        viewModelScope.launch {
            _isCalculatingRoute.value = true
            val options = repository.computeRoutes(
                startId = _startNodeId.value,
                destId = _destNodeId.value,
                preference = _routePreference.value,
                reqs = _accessibilityReqs.value
            )
            _routeOptions.value = options
            _selectedRouteOption.value = options.firstOrNull()
            _isCalculatingRoute.value = false
        }
    }

    fun quickStartRoute(startId: String, destId: String) {
        _startNodeId.value = startId
        _destNodeId.value = destId
        _currentDestination.value = AppDestination.PLAN_ROUTE
        calculateRoute()
    }

    fun saveRouteToHistory(route: RouteOption) {
        viewModelScope.launch {
            val startName = _nodes.value.find { it.id == _startNodeId.value }?.name ?: _startNodeId.value
            val destName = _nodes.value.find { it.id == _destNodeId.value }?.name ?: _destNodeId.value
            repository.saveCalculatedRoute(route, startName, destName, _routePreference.value.label)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    // Logistics Methods
    fun addLogisticsStop(stop: LogisticsStop) {
        val updated = _logisticsStops.value + stop
        _logisticsStops.value = updated
        optimizeLogistics()
    }

    fun removeLogisticsStop(stopId: String) {
        val updated = _logisticsStops.value.filter { it.id != stopId }
        _logisticsStops.value = updated
        optimizeLogistics()
    }

    fun toggleLogisticsStopComplete(stopId: String) {
        val updated = _logisticsStops.value.map {
            if (it.id == stopId) it.copy(completed = !it.completed) else it
        }
        _logisticsStops.value = updated
    }

    fun optimizeLogistics() {
        viewModelScope.launch {
            _isOptimizingLogistics.value = true
            val plan = repository.optimizeLogistics(_logisticsStops.value)
            _logisticsPlan.value = plan
            _isOptimizingLogistics.value = false
        }
    }

    // Emergency SOS Methods
    fun triggerSos(alertCode: String, lat: Double, lon: Double, altM: Int, message: String, target: String) {
        viewModelScope.launch {
            repository.queueOfflineSos(alertCode, lat, lon, altM, message, target)
        }
    }

    fun addEmergencyContact(name: String, agency: String, phone: String, isPrimary: Boolean, notes: String) {
        viewModelScope.launch {
            repository.addEmergencyContact(name, agency, phone, isPrimary, notes)
        }
    }

    fun deleteEmergencyContact(id: Long) {
        viewModelScope.launch {
            repository.deleteEmergencyContact(id)
        }
    }

    fun updateSosStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateSosStatus(id, status)
        }
    }

    // Offline Data Methods
    fun importCustomNodes(nodes: List<CustomDatasetEntity>) {
        viewModelScope.launch {
            repository.importCustomNodes(nodes)
            refreshNodes()
        }
    }

    fun clearCustomData() {
        viewModelScope.launch {
            repository.clearCustomDataset()
            refreshNodes()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }
}
