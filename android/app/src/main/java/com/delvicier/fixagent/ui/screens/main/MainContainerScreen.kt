package com.delvicier.fixagent.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bottomBorder
import com.delvicier.fixagent.ui.ViewModelFactory
import com.delvicier.fixagent.ui.navigation.Screen
import com.delvicier.fixagent.ui.screens.clients.ClientsScreen
import com.delvicier.fixagent.ui.screens.clients.ClientsViewModel
import com.delvicier.fixagent.ui.screens.orders.OrdersScreen
import com.delvicier.fixagent.ui.screens.orders.OrdersViewModel
import com.delvicier.fixagent.ui.screens.spaces.SpacesScreen
import com.delvicier.fixagent.ui.screens.spaces.SpacesViewModel
import kotlinx.coroutines.launch
import com.delvicier.fixagent.R
import com.delvicier.fixagent.ui.theme.MyColors.BrandBlue
import com.delvicier.fixagent.ui.theme.MyColors.BrandPurple
sealed class DrawerNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Profile : DrawerNavItem("profile", "Mi Perfil", Icons.Default.Person)
    object Logout : DrawerNavItem("logout", "Cerrar Sesión", Icons.AutoMirrored.Filled.Logout)
}

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Orders : BottomNavItem(Screen.Orders.route, "Órdenes", Icons.Default.ListAlt)
    object Clients : BottomNavItem(Screen.Clients.route, "Clientes", Icons.Default.People)
    object Spaces : BottomNavItem(Screen.Spaces.route, "Espacios", Icons.Default.Apartment)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainerScreen(
    factory: ViewModelFactory,
    rootNavController: androidx.navigation.NavController,
    onLogout: () -> Unit
) {

    val dashboardNavController = rememberNavController()


    val mainViewModel: MainViewModel = viewModel(factory = factory)


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    val navBackStackEntry by dashboardNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    val topLevelRoutes = listOf(
        Screen.Orders.route,
        Screen.Clients.route,
        Screen.Spaces.route
    )
    val isTopLevelScreen = currentDestination?.route in topLevelRoutes

    val gradientColors = listOf(BrandBlue, BrandPurple)
    val buttonBrush = Brush.horizontalGradient(gradientColors)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(260.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(buttonBrush)
                        .padding(24.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.95f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logotipo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Digital Home Computers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Ingreso de equipos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val drawerItems = listOf(DrawerNavItem.Profile, DrawerNavItem.Logout)

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }

                            if (item.route == "logout") {

                                mainViewModel.logout {
                                    onLogout()
                                }
                            } else if (item == DrawerNavItem.Profile) {
                                rootNavController.navigate(Screen.Profile.route)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                val topBarShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)

                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = {
                        Text(
                            text = findScreenTitle(currentDestination?.route),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        if (isTopLevelScreen) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                            }
                        } else {
                            IconButton(onClick = { dashboardNavController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                            }
                        }
                    },
                    actions = {
                        val currentRoute = currentDestination?.route
                        val action = mainViewModel.topBarActions[currentRoute]

                        if (action != null) {
                            IconButton(onClick = action.onClick) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = action.description
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(48.dp))
                        }
                    },
                    modifier = Modifier
                        .shadow(elevation = 2.dp, shape = topBarShape)
                        .clip(topBarShape)
                        .background(MaterialTheme.colorScheme.background)
                        .bottomBorder(
                            strokeWidth = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                )
            },
            bottomBar = {
                val navShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.inverseSurface,
                        MaterialTheme.colorScheme.inverseOnSurface
                    )
                )

                NavigationBar(
                    modifier = Modifier
                        .shadow(elevation = 8.dp, shape = navShape)
                        .clip(navShape)
                        .background(gradient),

                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    val navItemsBottom = listOf(BottomNavItem.Orders, BottomNavItem.Clients, BottomNavItem.Spaces)

                    navItemsBottom.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                dashboardNavController.navigate(item.route) {
                                    popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                                unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->

            NavHost(
                navController = dashboardNavController,
                startDestination = Screen.Orders.route,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(Screen.Orders.route) {
                    val ordersViewModel: OrdersViewModel = viewModel(factory = factory)

                    OrdersScreen(
                        mainViewModel = mainViewModel,
                        viewModel = ordersViewModel,
                        onNavigateToAdd = {
                            rootNavController.navigate(Screen.OrderAdd.route)
                        },
                        onNavigateToEdit = { orderId ->
                            rootNavController.navigate(Screen.OrderEdit.createRoute(orderId))
                        },
                        onNavigateToConfig = {
                            rootNavController.navigate(Screen.ConfigNetwork.route)
                        }
                    )
                }

                composable(Screen.Clients.route) {
                    val clientsViewModel: ClientsViewModel = viewModel(factory = factory)

                    ClientsScreen(
                        mainViewModel = mainViewModel,
                        viewModel = clientsViewModel,
                        onNavigateToAdd = {
                            rootNavController.navigate(Screen.ClientAdd.route)
                        },
                        onNavigateToEdit = { clientId ->
                            rootNavController.navigate(Screen.ClientEdit.createRoute(clientId))
                        },
                        onNavigateToConfig = {
                            rootNavController.navigate(Screen.ConfigNetwork.route)
                        }
                    )
                }

                composable(Screen.Spaces.route) {
                    val spacesViewModel: SpacesViewModel = viewModel(factory = factory)

                    SpacesScreen(
                        mainViewModel = mainViewModel,
                        viewModel = spacesViewModel,
                        onNavigateToAdd = {
                            rootNavController.navigate(Screen.SpaceAdd.route)
                        },
                        onNavigateToEdit = { id ->
                            rootNavController.navigate(Screen.SpaceEdit.createRoute(id))
                        },
                        onNavigateToConfig = {
                            rootNavController.navigate(Screen.ConfigNetwork.route)
                        }
                    )
                }
            }
        }
    }
}

private fun findScreenTitle(route: String?): String {
    return when (route) {
        Screen.Orders.route -> "Gestión de Órdenes"
        Screen.Clients.route -> "Cartera de Clientes"
        Screen.Spaces.route -> "Mis Espacios"
        else -> "Fix Agent"
    }
}