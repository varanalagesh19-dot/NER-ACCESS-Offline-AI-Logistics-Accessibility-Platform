package com.example.data

import com.example.data.entity.EmergencyContactEntity

object NerDemoData {

    val nodes: List<LocationNode> = listOf(
        LocationNode(
            id = "GHY_01",
            name = "Guwahati Central (Paltan Bazar)",
            state = "Assam",
            lat = 26.1833,
            lon = 91.7450,
            elevationMeters = 55,
            category = NodeCategory.CITY,
            isAccessible = true,
            hasRamp = true,
            hasElevator = true,
            stairSteps = 0,
            facilities = listOf("Wheelchair Ramp", "Elevator", "Tactile Paving", "Accessible Washroom", "Emergency Ambulance"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "DIS_02",
            name = "Dispur Capital Secretariat",
            state = "Assam",
            lat = 26.1420,
            lon = 91.7890,
            elevationMeters = 62,
            category = NodeCategory.CITY,
            isAccessible = true,
            hasRamp = true,
            hasElevator = true,
            stairSteps = 0,
            facilities = listOf("Universal Accessibility Pass", "Braille Signage", "Accessible Parking", "EV Logistics Charging"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "JHB_03",
            name = "Jalukbari NH-27 Logistics Interchange",
            state = "Assam",
            lat = 26.1550,
            lon = 91.6620,
            elevationMeters = 52,
            category = NodeCategory.LOGISTICS_HUB,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 0,
            facilities = listOf("Heavy Freight Weighbridge", "Truck Parking", "24/7 Fuel", "Rest Area"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "GMCH_04",
            name = "GMCH Apex Emergency Center",
            state = "Assam",
            lat = 26.1600,
            lon = 91.7700,
            elevationMeters = 70,
            category = NodeCategory.EMERGENCY_HOSPITAL,
            isAccessible = true,
            hasRamp = true,
            hasElevator = true,
            stairSteps = 0,
            facilities = listOf("Level-1 Trauma Center", "Automated Ramps", "Helipad", "Oxygen Bank", "Blood Bank"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "NGL_05",
            name = "Nongpoh Hill Transit Depot",
            state = "Meghalaya",
            lat = 25.9030,
            lon = 91.8810,
            elevationMeters = 485,
            category = NodeCategory.REST_POINT,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 3,
            facilities = listOf("Accessible Restrooms", "Highway First Aid", "Cold-Storage Logistics", "Cafeteria"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "UMI_06",
            name = "Umiam Lake Barrier Corridor",
            state = "Meghalaya",
            lat = 25.6600,
            lon = 91.9050,
            elevationMeters = 980,
            category = NodeCategory.REST_POINT,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 0,
            facilities = listOf("Scenic Walkway (Ramp Grade < 4%)", "Solar Rest Sheds", "Emergency Call Pillar"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "SHL_07",
            name = "Shillong Police Bazar Center",
            state = "Meghalaya",
            lat = 25.5788,
            lon = 91.8833,
            elevationMeters = 1525,
            category = NodeCategory.CITY,
            isAccessible = false,
            hasRamp = false,
            hasElevator = false,
            stairSteps = 24,
            facilities = listOf("Pedestrian Market", "High Street Shop Deliveries"),
            verification = DataVerificationStatus.USER_REPORTED
        ),
        LocationNode(
            id = "SHL_08",
            name = "NEIGRIHMS Super-Specialty Hospital",
            state = "Meghalaya",
            lat = 25.5920,
            lon = 91.9380,
            elevationMeters = 1540,
            category = NodeCategory.EMERGENCY_HOSPITAL,
            isAccessible = true,
            hasRamp = true,
            hasElevator = true,
            stairSteps = 0,
            facilities = listOf("Regional Trauma Unit", "Universal Access Ramps", "Cardiac Care", "High Altitude Recovery"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "SHL_09",
            name = "Mawlai Accessible Ridge Pass",
            state = "Meghalaya",
            lat = 25.6010,
            lon = 91.8710,
            elevationMeters = 1490,
            category = NodeCategory.ACCESSIBLE_TRANSIT,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 0,
            facilities = listOf("Gentle Slope Bypass (< 5%)", "Paved Sidewalks", "Low-Floor Bus Stops"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "CHR_10",
            name = "Sohra (Cherrapunji) Plateau",
            state = "Meghalaya",
            lat = 25.2986,
            lon = 91.7322,
            elevationMeters = 1430,
            category = NodeCategory.HILL_PASS,
            isAccessible = false,
            hasRamp = false,
            hasElevator = false,
            stairSteps = 45,
            facilities = listOf("Mountain Viewpoint", "High-Rainfall Warning Outpost"),
            verification = DataVerificationStatus.USER_REPORTED
        ),
        LocationNode(
            id = "DWK_11",
            name = "Dawki Border Freight Terminal",
            state = "Meghalaya",
            lat = 25.1840,
            lon = 92.0190,
            elevationMeters = 85,
            category = NodeCategory.LOGISTICS_HUB,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 2,
            facilities = listOf("Customs Cargo Bay", "Weight Scales", "Cross-Border Logistics", "Driver Dormitory"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "TEZ_12",
            name = "Tezpur Northern Freight Node",
            state = "Assam",
            lat = 26.6528,
            lon = 92.7926,
            elevationMeters = 48,
            category = NodeCategory.LOGISTICS_HUB,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 0,
            facilities = listOf("Kaliabhomora Heavy Corridor", "Brahmaputra Port", "Fuel Depot", "Accessible Transit"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "NAG_13",
            name = "Nagaon Agri-Logistics Junction",
            state = "Assam",
            lat = 26.3460,
            lon = 92.6840,
            elevationMeters = 60,
            category = NodeCategory.LOGISTICS_HUB,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 0,
            facilities = listOf("Expressway Crossway", "Cold Grain Storage", "Universal Rest Bay"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "KAZ_14",
            name = "Kaziranga Eco-Corridor Transit",
            state = "Assam",
            lat = 26.5775,
            lon = 93.1711,
            elevationMeters = 65,
            category = NodeCategory.REST_POINT,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 0,
            facilities = listOf("Eco Elevated Corridor", "Accessible Viewing Bays", "Forest Ranger Emergency Unit"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "JOR_15",
            name = "Jorhat Commercial & Agro Center",
            state = "Assam",
            lat = 26.7509,
            lon = 94.2037,
            elevationMeters = 87,
            category = NodeCategory.CITY,
            isAccessible = true,
            hasRamp = true,
            hasElevator = true,
            stairSteps = 0,
            facilities = listOf("Medical College Hospital", "Braille Map Terminal", "Tea Logistics Warehouse"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        ),
        LocationNode(
            id = "SIL_16",
            name = "Silchar Barak Valley Logistics Depot",
            state = "Assam",
            lat = 24.8333,
            lon = 92.7789,
            elevationMeters = 25,
            category = NodeCategory.LOGISTICS_HUB,
            isAccessible = true,
            hasRamp = true,
            hasElevator = false,
            stairSteps = 0,
            facilities = listOf("Barak River Barge Terminal", "Heavy Freight Staging", "District Disaster Unit"),
            verification = DataVerificationStatus.VERIFIED_DATASET
        )
    )

    val edges: List<RouteEdge> = listOf(
        // Guwahati city center <-> Dispur Capital (Highway vs Avenue)
        RouteEdge("GHY_01", "DIS_02", 7.2, 45.0, 1.2, RoadSurface.EXCELLENT_HIGHWAY, true, false, 40.0, 4.5, "None"),
        RouteEdge("DIS_02", "GHY_01", 7.2, 45.0, 1.2, RoadSurface.EXCELLENT_HIGHWAY, true, false, 40.0, 4.5, "None"),

        // Guwahati Central <-> Jalukbari Logistics
        RouteEdge("GHY_01", "JHB_03", 9.5, 50.0, 0.8, RoadSurface.EXCELLENT_HIGHWAY, true, false, 45.0, 4.8, "None"),
        RouteEdge("JHB_03", "GHY_01", 9.5, 50.0, 0.8, RoadSurface.EXCELLENT_HIGHWAY, true, false, 45.0, 4.8, "None"),

        // Dispur <-> GMCH Emergency Hospital
        RouteEdge("DIS_02", "GMCH_04", 3.8, 35.0, 2.1, RoadSurface.GOOD_PAVED, true, false, 25.0, 4.0, "None"),
        RouteEdge("GMCH_04", "DIS_02", 3.8, 35.0, 2.1, RoadSurface.GOOD_PAVED, true, false, 25.0, 4.0, "None"),
        RouteEdge("GHY_01", "GMCH_04", 4.5, 30.0, 3.4, RoadSurface.GOOD_PAVED, true, false, 20.0, 3.8, "Moderate Peak Traffic"),
        RouteEdge("GMCH_04", "GHY_01", 4.5, 30.0, 3.4, RoadSurface.GOOD_PAVED, true, false, 20.0, 3.8, "Moderate Peak Traffic"),

        // Guwahati / Dispur -> Nongpoh Hill Transit (NH-6 Ascending into Meghalaya)
        RouteEdge("DIS_02", "NGL_05", 46.0, 55.0, 4.8, RoadSurface.EXCELLENT_HIGHWAY, true, false, 40.0, 4.5, "Gentle Hill Incline"),
        RouteEdge("NGL_05", "DIS_02", 46.0, 55.0, 4.8, RoadSurface.EXCELLENT_HIGHWAY, true, false, 40.0, 4.5, "Gentle Hill Decline"),
        RouteEdge("JHB_03", "NGL_05", 51.0, 60.0, 4.5, RoadSurface.EXCELLENT_HIGHWAY, true, false, 45.0, 4.8, "NH Bypass Arterial"),
        RouteEdge("NGL_05", "JHB_03", 51.0, 60.0, 4.5, RoadSurface.EXCELLENT_HIGHWAY, true, false, 45.0, 4.8, "NH Bypass Arterial"),

        // Nongpoh -> Umiam Lake (NH-6)
        RouteEdge("NGL_05", "UMI_06", 35.0, 50.0, 5.2, RoadSurface.GOOD_PAVED, true, false, 35.0, 4.2, "Scenic Curves"),
        RouteEdge("UMI_06", "NGL_05", 35.0, 50.0, 5.2, RoadSurface.GOOD_PAVED, true, false, 35.0, 4.2, "Scenic Curves"),

        // Umiam -> Shillong Mawlai Accessible Ridge Pass (ACCESSIBILITY OPTION: Gentle grade, ramps, avoids stairs)
        RouteEdge("UMI_06", "SHL_09", 14.0, 42.0, 4.0, RoadSurface.GOOD_PAVED, true, false, 30.0, 4.0, "Gentle Slope Paved Sidewalk"),
        RouteEdge("SHL_09", "UMI_06", 14.0, 42.0, 4.0, RoadSurface.GOOD_PAVED, true, false, 30.0, 4.0, "Gentle Slope Paved Sidewalk"),

        // Umiam -> Shillong Police Bazar (DIRECT / FASTEST OPTION: Steeper, pedestrian stairs at terminal)
        RouteEdge("UMI_06", "SHL_07", 16.5, 40.0, 8.5, RoadSurface.MODERATE_HILL_ROAD, false, true, 20.0, 3.8, "Steep 8.5% Incline & Market Stairs"),
        RouteEdge("SHL_07", "UMI_06", 16.5, 40.0, 8.5, RoadSurface.MODERATE_HILL_ROAD, false, true, 20.0, 3.8, "Steep 8.5% Incline & Market Stairs"),

        // Mawlai Accessible Pass -> Shillong Police Bazar
        RouteEdge("SHL_09", "SHL_07", 4.2, 28.0, 6.2, RoadSurface.GOOD_PAVED, false, true, 16.0, 3.5, "Urban Stair Corridor"),
        RouteEdge("SHL_07", "SHL_09", 4.2, 28.0, 6.2, RoadSurface.GOOD_PAVED, false, true, 16.0, 3.5, "Urban Stair Corridor"),

        // Mawlai Accessible Pass -> NEIGRIHMS Super Specialty
        RouteEdge("SHL_09", "SHL_08", 8.1, 40.0, 3.8, RoadSurface.GOOD_PAVED, true, false, 28.0, 4.2, "Accessible Emergency Corridor"),
        RouteEdge("SHL_08", "SHL_09", 8.1, 40.0, 3.8, RoadSurface.GOOD_PAVED, true, false, 28.0, 4.2, "Accessible Emergency Corridor"),

        // Shillong Center -> NEIGRIHMS
        RouteEdge("SHL_07", "SHL_08", 6.8, 30.0, 5.0, RoadSurface.GOOD_PAVED, true, false, 22.0, 3.8, "City Traffic Link"),
        RouteEdge("SHL_08", "SHL_07", 6.8, 30.0, 5.0, RoadSurface.GOOD_PAVED, true, false, 22.0, 3.8, "City Traffic Link"),

        // Shillong -> Sohra Cherrapunji (Hill road with high gradient)
        RouteEdge("SHL_07", "CHR_10", 52.0, 38.0, 9.8, RoadSurface.MODERATE_HILL_ROAD, false, true, 18.0, 3.5, "Landslide Watch & Steep Hairpin Bends"),
        RouteEdge("CHR_10", "SHL_07", 52.0, 38.0, 9.8, RoadSurface.MODERATE_HILL_ROAD, false, true, 18.0, 3.5, "Landslide Watch & Steep Hairpin Bends"),

        // Shillong Mawlai -> Dawki Freight Terminal
        RouteEdge("SHL_09", "DWK_11", 82.0, 45.0, 6.5, RoadSurface.MODERATE_HILL_ROAD, true, false, 32.0, 4.0, "Descent into Border Valley"),
        RouteEdge("DWK_11", "SHL_09", 82.0, 45.0, 6.5, RoadSurface.MODERATE_HILL_ROAD, true, false, 32.0, 4.0, "Descent into Border Valley"),

        // Dispur / Jalukbari -> Nagaon Agri-Logistics (NH-27 Eastward Trunk)
        RouteEdge("DIS_02", "NAG_13", 112.0, 75.0, 0.5, RoadSurface.EXCELLENT_HIGHWAY, true, false, 50.0, 5.0, "Four-Lane National Expressway"),
        RouteEdge("NAG_13", "DIS_02", 112.0, 75.0, 0.5, RoadSurface.EXCELLENT_HIGHWAY, true, false, 50.0, 5.0, "Four-Lane National Expressway"),

        // Nagaon -> Tezpur (Via Kaliabhomora Bridge)
        RouteEdge("NAG_13", "TEZ_12", 48.0, 65.0, 0.8, RoadSurface.EXCELLENT_HIGHWAY, true, false, 45.0, 4.8, "Major Brahmaputra River Bridge"),
        RouteEdge("TEZ_12", "NAG_13", 48.0, 65.0, 0.8, RoadSurface.EXCELLENT_HIGHWAY, true, false, 45.0, 4.8, "Major Brahmaputra River Bridge"),

        // Nagaon -> Kaziranga Eco Transit
        RouteEdge("NAG_13", "KAZ_14", 76.0, 60.0, 0.4, RoadSurface.EXCELLENT_HIGHWAY, true, false, 40.0, 4.5, "Animal Crossing Speed Regulation"),
        RouteEdge("KAZ_14", "NAG_13", 76.0, 60.0, 0.4, RoadSurface.EXCELLENT_HIGHWAY, true, false, 40.0, 4.5, "Animal Crossing Speed Regulation"),

        // Kaziranga -> Jorhat
        RouteEdge("KAZ_14", "JOR_15", 88.0, 68.0, 0.6, RoadSurface.EXCELLENT_HIGHWAY, true, false, 45.0, 4.8, "Paved Highway Arterial"),
        RouteEdge("JOR_15", "KAZ_14", 88.0, 68.0, 0.6, RoadSurface.EXCELLENT_HIGHWAY, true, false, 45.0, 4.8, "Paved Highway Arterial"),

        // Shillong -> Silchar (NH-6 Hill link into Barak Valley)
        RouteEdge("SHL_08", "SIL_16", 215.0, 36.0, 7.5, RoadSurface.MODERATE_HILL_ROAD, false, false, 25.0, 3.8, "Challenging Hill Highway & Fog Pass"),
        RouteEdge("SIL_16", "SHL_08", 215.0, 36.0, 7.5, RoadSurface.MODERATE_HILL_ROAD, false, false, 25.0, 3.8, "Challenging Hill Highway & Fog Pass"),

        // Dawki -> Silchar
        RouteEdge("DWK_11", "SIL_16", 145.0, 42.0, 5.0, RoadSurface.GOOD_PAVED, true, false, 28.0, 4.0, "Southern Foothill Highway"),
        RouteEdge("SIL_16", "DWK_11", 145.0, 42.0, 5.0, RoadSurface.GOOD_PAVED, true, false, 28.0, 4.0, "Southern Foothill Highway")
    )

    val defaultEmergencyContacts: List<EmergencyContactEntity> = listOf(
        EmergencyContactEntity(
            id = 1,
            name = "NER Emergency Disaster Operations Control",
            relationOrAgency = "Disaster Management Cell",
            phoneNumber = "1070",
            isPrimary = true,
            notes = "Toll-free emergency nodal desk for North Eastern Region"
        ),
        EmergencyContactEntity(
            id = 2,
            name = "Assam State Disaster Management Authority (ASDMA)",
            relationOrAgency = "State Agency",
            phoneNumber = "1077",
            isPrimary = true,
            notes = "24x7 Flood, Landslide & Logistics Emergency Center"
        ),
        EmergencyContactEntity(
            id = 3,
            name = "Meghalaya State Emergency Helpline",
            relationOrAgency = "State Agency",
            phoneNumber = "1070",
            isPrimary = false,
            notes = "Hill route evacuation & highway rescue support"
        ),
        EmergencyContactEntity(
            id = 4,
            name = "National Highway Ambulance Response (NH-27 / NH-6)",
            relationOrAgency = "Highway Medical",
            phoneNumber = "1033",
            isPrimary = false,
            notes = "Rapid paramedical response for mountain transit routes"
        ),
        EmergencyContactEntity(
            id = 5,
            name = "Regional Logistics & Fleet Safety Hub",
            relationOrAgency = "Fleet Coordinator",
            phoneNumber = "+91 361 2459900",
            isPrimary = false,
            notes = "Driver SOS & consignment breakdown recovery"
        )
    )

    val preloadedDatasets: List<DatasetInfo> = listOf(
        DatasetInfo(
            id = "ds_ner_road_graph",
            name = "NER High-Resolution Road Graph",
            category = "Road & Topology",
            status = "Preloaded Core",
            sizeMb = 18.4,
            recordCount = 12480,
            lastUpdated = "2026-08-15 (v3.2)",
            description = "Topology network covering Assam, Meghalaya, and transit corridors with elevation and slope models."
        ),
        DatasetInfo(
            id = "ds_ner_accessibility",
            name = "NER Urban Accessibility & Tactile Survey",
            category = "Accessibility Layer",
            status = "Preloaded Core",
            sizeMb = 8.6,
            recordCount = 4320,
            lastUpdated = "2026-09-01 (SIH26 Verified)",
            description = "Audited wheelchair ramps, stairs, tactile paving, accessible restrooms, and low-gradient pathways."
        ),
        DatasetInfo(
            id = "ds_ner_logistics_weight",
            name = "NER Freight Corridors & Axle-Load Limits",
            category = "Logistics Intelligence",
            status = "Preloaded Core",
            sizeMb = 6.2,
            recordCount = 2190,
            lastUpdated = "2026-07-28 (v2.1)",
            description = "Bridge load thresholds, hairpin turning radiuses, height clearances, and truck parking bays."
        ),
        DatasetInfo(
            id = "ds_ner_emergency_outposts",
            name = "NER Emergency Medical & Refuge Outposts",
            category = "Emergency SOS",
            status = "Preloaded Core",
            sizeMb = 4.1,
            recordCount = 1150,
            lastUpdated = "2026-09-10 (Verified)",
            description = "SDMA shelters, Level-1 trauma hospitals, disaster recovery points, and oxygen depots."
        ),
        DatasetInfo(
            id = "ds_ner_local_neural_heuristics",
            name = "Local Neural Heuristics Model (v2.4)",
            category = "Local AI / Heuristic",
            status = "Preloaded Core",
            sizeMb = 3.8,
            recordCount = 512,
            lastUpdated = "2026-09-15 (Deterministic Weights)",
            description = "Lightweight on-device classifier predicting route bottleneck risk and accessibility compliance."
        )
    )

    val sampleLogisticsStops: List<LogisticsStop> = listOf(
        LogisticsStop(
            id = "LS_01",
            name = "Jalukbari Central Warehouse",
            stateLocation = "Guwahati, Assam",
            type = StopType.WAREHOUSE_HUB,
            priority = StopPriority.HIGH,
            timeWindow = "07:00 - 08:30",
            weightKg = 1250.0,
            accessibilityRequirement = "Forklift Dock & Level Ramp",
            completed = true
        ),
        LogisticsStop(
            id = "LS_02",
            name = "GMCH Emergency Trauma Center",
            stateLocation = "Guwahati, Assam",
            type = StopType.DELIVERY,
            priority = StopPriority.CRITICAL_MEDICAL,
            timeWindow = "08:45 - 09:30",
            weightKg = 180.0,
            accessibilityRequirement = "Universal Medical Ramp Required",
            completed = false
        ),
        LogisticsStop(
            id = "LS_03",
            name = "Nongpoh Cold Storage Hub",
            stateLocation = "Nongpoh, Meghalaya",
            type = StopType.DELIVERY,
            priority = StopPriority.HIGH,
            timeWindow = "10:30 - 11:30",
            weightKg = 420.0,
            accessibilityRequirement = "Ground Floor Loading Bay",
            completed = false
        ),
        LogisticsStop(
            id = "LS_04",
            name = "NEIGRIHMS Super-Specialty Medical",
            stateLocation = "Shillong, Meghalaya",
            type = StopType.DELIVERY,
            priority = StopPriority.CRITICAL_MEDICAL,
            timeWindow = "12:00 - 13:00",
            weightKg = 310.0,
            accessibilityRequirement = "Wheelchair & Gurney Accessible Entry",
            completed = false
        ),
        LogisticsStop(
            id = "LS_05",
            name = "Mawlai Accessible Freight Depot",
            stateLocation = "Shillong, Meghalaya",
            type = StopType.DELIVERY,
            priority = StopPriority.MEDIUM,
            timeWindow = "14:00 - 16:00",
            weightKg = 650.0,
            accessibilityRequirement = "Standard Paved Access",
            completed = false
        )
    )
}
