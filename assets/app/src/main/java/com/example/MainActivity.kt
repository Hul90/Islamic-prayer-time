package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.model.AppLanguage
import com.example.model.AppThemeMode
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.screens.tools.*
import com.example.ui.theme.IslamicPrayerTheme
import com.example.ui.viewmodel.CalendarViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.SultanToolsViewModel

data class BottomNavItem(
    val route: String,
    val titleEn: String,
    val titleBn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val calendarViewModel: CalendarViewModel = viewModel()
            val toolsViewModel: SultanToolsViewModel = viewModel()

            val mainUiState by mainViewModel.uiState.collectAsState()
            val calendarUiState by calendarViewModel.uiState.collectAsState()

            val isDarkTheme = when (mainUiState.settings.themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
            }

            IslamicPrayerTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isBangla = mainUiState.settings.language == AppLanguage.BANGLA
                    val isOnboardingCompleted = mainUiState.settings.isOnboardingCompleted

                    var showOnboarding by remember(isOnboardingCompleted) {
                        mutableStateOf(!isOnboardingCompleted)
                    }

                    if (showOnboarding) {
                        OnboardingScreen(
                            viewModel = mainViewModel,
                            isBangla = isBangla,
                            onFinish = { showOnboarding = false }
                        )
                    } else {
                        MainAppContent(
                            mainViewModel = mainViewModel,
                            calendarViewModel = calendarViewModel,
                            toolsViewModel = toolsViewModel,
                            mainUiState = mainUiState,
                            calendarUiState = calendarUiState,
                            isBangla = isBangla
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    mainViewModel: MainViewModel,
    calendarViewModel: CalendarViewModel,
    toolsViewModel: SultanToolsViewModel,
    mainUiState: com.example.ui.viewmodel.MainUiState,
    calendarUiState: com.example.ui.viewmodel.CalendarUiState,
    isBangla: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(
            route = Screen.Home.route,
            titleEn = "Home",
            titleBn = "হোম",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        BottomNavItem(
            route = Screen.Prayer.route,
            titleEn = "Prayer",
            titleBn = "নামাজ",
            selectedIcon = Icons.Filled.AccessTimeFilled,
            unselectedIcon = Icons.Outlined.AccessTime,
            testTag = "nav_prayer"
        ),
        BottomNavItem(
            route = Screen.Calendar.route,
            titleEn = "Calendar",
            titleBn = "ক্যালেন্ডার",
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth,
            testTag = "nav_calendar"
        ),
        BottomNavItem(
            route = Screen.SultanTools.route,
            titleEn = "Tools",
            titleBn = "টুলস",
            selectedIcon = Icons.Filled.Widgets,
            unselectedIcon = Icons.Outlined.Widgets,
            testTag = "nav_tools"
        ),
        BottomNavItem(
            route = Screen.Settings.route,
            titleEn = "Settings",
            titleBn = "সেটিংস",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            testTag = "nav_settings"
        )
    )

    val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = NavigationBarDefaults.Elevation
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.titleEn
                                )
                            },
                            label = {
                                Text(
                                    text = if (isBangla) item.titleBn else item.titleEn,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = mainViewModel,
                    uiState = mainUiState,
                    onNavigateTo = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.Prayer.route) {
                PrayerScreen(
                    viewModel = mainViewModel,
                    uiState = mainUiState,
                    onNavigateTo = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.Calendar.route) {
                CalendarScreen(
                    viewModel = calendarViewModel,
                    uiState = calendarUiState,
                    isBangla = isBangla,
                    onNavigateTo = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.SultanTools.route) {
                SultanToolsScreen(
                    isBangla = isBangla,
                    onNavigateTo = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = mainViewModel,
                    uiState = mainUiState,
                    onNavigateTo = { route -> navController.navigate(route) }
                )
            }

            // Sub-destinations
            composable(Screen.Qibla.route) {
                QiblaScreen(
                    viewModel = toolsViewModel,
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Tasbih.route) {
                TasbihScreen(
                    viewModel = toolsViewModel,
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.PrayerTracker.route) {
                PrayerTrackerScreen(
                    viewModel = toolsViewModel,
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.DateConverter.route) {
                DateConverterScreen(
                    viewModel = toolsViewModel,
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ZakatCalculator.route) {
                ZakatCalculatorScreen(
                    viewModel = toolsViewModel,
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.DhikrTimer.route) {
                DhikrTimerScreen(
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.NearbyMosques.route) {
                NearbyMosquesScreen(
                    location = mainUiState.location,
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.RamadanMode.route) {
                RamadanModeScreen(
                    viewModel = mainViewModel,
                    uiState = mainUiState,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.About.route) {
                AboutScreen(
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.DistrictPicker.route) {
                DistrictPickerScreen(
                    viewModel = mainViewModel,
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.PrayerCalculationDetails.route) {
                PrayerCalculationDetailsScreen(
                    uiState = mainUiState,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.NamazLearning.route) {
                NamazLearningScreen(
                    isBangla = isBangla,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Duas.route) {
                IslamicWebViewScreen(
                    title = if (isBangla) "সকল দোয়া ও যিকির — সূচিপত্র" else "All Duas & Zikr — Contents",
                    url = if (isBangla) "https://dua.gtaf.org/" else "https://dua.gtaf.org/en/",
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Quran.route) {
                IslamicWebViewScreen(
                    title = if (isBangla) "আল কুরআন" else "AL QURAN",
                    url = "https://www.hadithbd.com/quran/",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
