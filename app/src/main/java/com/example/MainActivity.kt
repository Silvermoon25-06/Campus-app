package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.CampusRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val repository = remember { CampusRepository.getInstance(context) }
                val currentUser by repository.currentUser.collectAsState()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentUser == null) {
                        LoginScreen(repository = repository) { loggedInUser ->
                            // session managed by repository flow
                        }
                    } else {
                        MainAppShell(repository = repository)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(repository: CampusRepository) {
    val currentUser by repository.currentUser.collectAsState()
    var currentScreen by remember { mutableStateOf("home") }

    val isAdmin = currentUser?.role == "admin"

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 640.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Adaptive Side Navigation Rail for Large Displays / Tablets
            if (isWideScreen) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    header = {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "UniCampus",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .size(28.dp)
                        )
                    },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
                ) {
                    NavigationRailItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp) }
                    )
                    NavigationRailItem(
                        selected = currentScreen == "schedule",
                        onClick = { currentScreen = "schedule" },
                        icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Schedule") },
                        label = { Text("Classes", fontSize = 10.sp) }
                    )
                    NavigationRailItem(
                        selected = currentScreen == "grades",
                        onClick = { currentScreen = "grades" },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Grades") },
                        label = { Text("Grades", fontSize = 10.sp) }
                    )
                    NavigationRailItem(
                        selected = currentScreen == "map",
                        onClick = { currentScreen = "map" },
                        icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                        label = { Text("Map", fontSize = 10.sp) }
                    )
                    NavigationRailItem(
                        selected = currentScreen == "partners",
                        onClick = { currentScreen = "partners" },
                        icon = { Icon(Icons.Default.Groups, contentDescription = "Study Partners") },
                        label = { Text("Partners", fontSize = 10.sp) }
                    )
                    NavigationRailItem(
                        selected = currentScreen == "messaging",
                        onClick = { currentScreen = "messaging" },
                        icon = { Icon(Icons.Default.Forum, contentDescription = "Messaging") },
                        label = { Text("Clubs", fontSize = 10.sp) }
                    )
                    NavigationRailItem(
                        selected = currentScreen == "assistant",
                        onClick = { currentScreen = "assistant" },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Hub") },
                        label = { Text("AI Hub", fontSize = 10.sp) }
                    )
                    NavigationRailItem(
                        selected = currentScreen == "recreation",
                        onClick = { currentScreen = "recreation" },
                        icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Life") },
                        label = { Text("Life", fontSize = 10.sp) }
                    )
                    if (isAdmin) {
                        NavigationRailItem(
                            selected = currentScreen == "admin",
                            onClick = { currentScreen = "admin" },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Desk") },
                            label = { Text("Admin", fontSize = 10.sp) }
                        )
                    }
                }
                VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            }

            // Main Scaffold content block
            Scaffold(
                modifier = Modifier.weight(1f),
                bottomBar = {
                    // Standard Bottom Navigation Bar for Mobile displays
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == "home",
                                onClick = { currentScreen = "home" },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 9.sp) }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "schedule",
                                onClick = { currentScreen = "schedule" },
                                icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Classes") },
                                label = { Text("Classes", fontSize = 9.sp) }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "map",
                                onClick = { currentScreen = "map" },
                                icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                                label = { Text("Map", fontSize = 9.sp) }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "partners",
                                onClick = { currentScreen = "partners" },
                                icon = { Icon(Icons.Default.Groups, contentDescription = "Study Partners") },
                                label = { Text("Partners", fontSize = 9.sp) }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "messaging",
                                onClick = { currentScreen = "messaging" },
                                icon = { Icon(Icons.Default.Forum, contentDescription = "Messaging") },
                                label = { Text("Clubs", fontSize = 9.sp) }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "assistant",
                                onClick = { currentScreen = "assistant" },
                                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Hub") },
                                label = { Text("AI Hub", fontSize = 9.sp) }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "recreation",
                                onClick = { currentScreen = "recreation" },
                                icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Life") },
                                label = { Text("Life", fontSize = 9.sp) }
                            )
                            if (isAdmin) {
                                NavigationBarItem(
                                    selected = currentScreen == "admin",
                                    onClick = { currentScreen = "admin" },
                                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Desk") },
                                    label = { Text("Admin", fontSize = 9.sp) }
                                )
                            }
                        }
                    }
                },
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            repository = repository,
                            onNavigateToSchedule = { currentScreen = "schedule" },
                            onNavigateToGrades = { currentScreen = "grades" },
                            onNavigateToMap = { currentScreen = "map" },
                            onSignOut = { repository.signOut() }
                        )
                        "schedule" -> ScheduleScreen(repository = repository)
                        "grades" -> GradesScreen(repository = repository)
                        "map" -> CampusMapScreen(repository = repository)
                        "partners" -> StudyPartnerScreen(repository = repository)
                        "messaging" -> MessagingScreen(repository = repository)
                        "assistant" -> GeminiAssistantScreen()
                        "recreation" -> RecreationScreen()
                        "admin" -> AdminDashboardScreen(repository = repository)
                    }
                }
            }
        }
    }
}
