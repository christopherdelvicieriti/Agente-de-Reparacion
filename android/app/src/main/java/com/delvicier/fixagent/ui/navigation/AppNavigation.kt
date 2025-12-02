package com.delvicier.fixagent.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.delvicier.fixagent.ui.ViewModelFactory
import com.delvicier.fixagent.ui.screens.clients.ClientsAddScreen
import com.delvicier.fixagent.ui.screens.clients.ClientsEditScreen
import com.delvicier.fixagent.ui.screens.login.LoginScreen
import com.delvicier.fixagent.ui.screens.machines.MachinesAddScreen
import com.delvicier.fixagent.ui.screens.main.MainContainerScreen
import com.delvicier.fixagent.ui.screens.orders.OrdersAddScreen
import com.delvicier.fixagent.ui.screens.orders.OrdersEditScreen
import com.delvicier.fixagent.ui.screens.setup.SetupAccountScreen
import com.delvicier.fixagent.ui.screens.spaces.SpaceAddScreen
import com.delvicier.fixagent.ui.screens.spaces.SpaceEditScreen
import com.delvicier.fixagent.ui.screens.splash.SplashScreen
import com.delvicier.fixagent.ui.screens.welcome.WelcomeScreen
import com.delvicier.fixagent.ui.screens.welcome.WelcomeViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SetupAccount : Screen("setup_account")
    object Main : Screen("main_container")

    object Orders : Screen("orders")
    object Clients : Screen("clients")
    object Spaces : Screen("spaces")

    object SpaceAdd : Screen("space_add")
    object SpaceEdit : Screen("space_edit/{spaceId}") {
        fun createRoute(id: Int) = "space_edit/$id"
    }
    object ClientAdd : Screen("client_add")

    object ClientEdit : Screen("client_edit/{clientId}") {
        fun createRoute(id: Int) = "client_edit/$id"
    }

    object OrderEdit : Screen("order_edit/{orderId}") {
        fun createRoute(id: Int) = "order_edit/$id"
    }

    object OrderAdd : Screen("order_add")

    object MachineAdd : Screen("machine_add/{orderId}") {
        fun createRoute(id: Int) = "machine_add/$id"
    }

    object MachineEdit : Screen("machine_edit/{machineId}") {
        fun createRoute(id: Int) = "machine_edit/$id"
    }

    object Profile : Screen("profile")

    object Recover : Screen("recover")

    object ConfigNetwork : Screen("config_network")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val factory = ViewModelFactory(context)

    fun performLogout() {
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                viewModel = viewModel(factory = factory),
                onNavigateToWelcome = { navController.navigate(Screen.Welcome.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToSetup = { navController.navigate(Screen.SetupAccount.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToMain = { navController.navigate(Screen.Main.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }
            )
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                viewModel = viewModel(factory = factory),
                onNavigateToLogin = { navController.navigate(Screen.Splash.route) { popUpTo(Screen.Welcome.route) { inclusive = true } } }
            )
        }

        composable(Screen.SetupAccount.route) {
            SetupAccountScreen(
                viewModel = viewModel(factory = factory),
                onNavigateToHome = { navController.navigate(Screen.Login.route) { popUpTo(Screen.SetupAccount.route) { inclusive = true } } }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel(factory = factory),
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRecover = {
                    navController.navigate(Screen.Recover.route)
                }
            )
        }

        composable(Screen.Main.route) {
            MainContainerScreen(
                factory = factory,
                rootNavController = navController,
                onLogout = { performLogout() }
            )
        }

        composable(Screen.SpaceAdd.route) {
            SpaceAddScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.SpaceEdit.route,
            arguments = listOf(navArgument("spaceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("spaceId") ?: 0
            SpaceEditScreen(spaceId = id, factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Screen.ClientAdd.route) {
            ClientsAddScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.ClientEdit.route,
            arguments = listOf(navArgument("clientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("clientId") ?: 0
            ClientsEditScreen(
                clientId = id,
                factory = factory,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.OrderAdd.route) {
            OrdersAddScreen(
                factory = factory,
                onBack = { navController.popBackStack() },
                onNavigateToCreateClient = {
                    navController.navigate(Screen.ClientAdd.route)
                }
            )
        }

        composable(
            route = Screen.OrderEdit.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("orderId") ?: 0

            OrdersEditScreen(
                orderId = id,
                factory = factory,
                onBack = { navController.popBackStack() },

                onNavigateToAddMachine = { orderId ->
                    navController.navigate(Screen.MachineAdd.createRoute(orderId))
                },

                onNavigateToEditMachine = { machineId ->
                    navController.navigate(Screen.MachineEdit.createRoute(machineId))
                }
            )
        }

        composable(
            route = Screen.MachineAdd.route,
            arguments = listOf(androidx.navigation.navArgument("orderId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            MachinesAddScreen(
                orderId = orderId,
                factory = factory,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.MachineEdit.route,
            arguments = listOf(navArgument("machineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val machineId = backStackEntry.arguments?.getInt("machineId") ?: 0

            com.delvicier.fixagent.ui.screens.machines.MachinesEditScreen(
                machineId = machineId,
                factory = factory,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            com.delvicier.fixagent.ui.screens.profile.ProfileScreen(
                factory = factory,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Recover.route) {
            com.delvicier.fixagent.ui.screens.recover.RecoverScreen(
                factory = factory,
                onBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Recover.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ConfigNetwork.route) {
            val viewModel: WelcomeViewModel = viewModel(factory = factory)

            com.delvicier.fixagent.ui.screens.config.ConfigNetworkScreen(
                viewModel = viewModel,
                onConfigComplete = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}