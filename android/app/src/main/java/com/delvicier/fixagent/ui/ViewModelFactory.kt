package com.delvicier.fixagent.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delvicier.fixagent.data.local.PreferencesRepository
import com.delvicier.fixagent.data.network.RetrofitClient
import com.delvicier.fixagent.ui.screens.clients.ClientsAddViewModel
import com.delvicier.fixagent.ui.screens.clients.ClientsEditViewModel
import com.delvicier.fixagent.ui.screens.clients.ClientsViewModel
import com.delvicier.fixagent.ui.screens.login.LoginViewModel
import com.delvicier.fixagent.ui.screens.machines.MachinesAddViewModel
import com.delvicier.fixagent.ui.screens.machines.MachinesEditViewModel
import com.delvicier.fixagent.ui.screens.machines.MachinesViewModel
import com.delvicier.fixagent.ui.screens.orders.OrdersAddViewModel
import com.delvicier.fixagent.ui.screens.orders.OrdersEditViewModel
import com.delvicier.fixagent.ui.screens.orders.OrdersViewModel
import com.delvicier.fixagent.ui.screens.profile.ProfileViewModel
import com.delvicier.fixagent.ui.screens.recover.RecoverViewModel
import com.delvicier.fixagent.ui.screens.setup.SetupAccountViewModel
import com.delvicier.fixagent.ui.screens.spaces.SpaceAddViewModel
import com.delvicier.fixagent.ui.screens.spaces.SpaceEditViewModel
import com.delvicier.fixagent.ui.screens.spaces.SpacesViewModel
import com.delvicier.fixagent.ui.screens.splash.SplashViewModel
import com.delvicier.fixagent.ui.screens.welcome.WelcomeViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val preferencesRepository by lazy {
        PreferencesRepository(context.applicationContext)
    }

    private val apiService by lazy {
        RetrofitClient.getClient(preferencesRepository)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> {
                WelcomeViewModel(preferencesRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(SetupAccountViewModel::class.java) -> {
                SetupAccountViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(SpacesViewModel::class.java) -> {
                SpacesViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(SpaceAddViewModel::class.java) -> {
                SpaceAddViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(ClientsViewModel::class.java) -> {
                ClientsViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(SpaceEditViewModel::class.java) -> {
                SpaceEditViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(ClientsAddViewModel::class.java) -> {
                ClientsAddViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(ClientsEditViewModel::class.java) -> {
                ClientsEditViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(OrdersViewModel::class.java) -> {
                OrdersViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(OrdersAddViewModel::class.java) -> {
                OrdersAddViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(OrdersEditViewModel::class.java) -> {
                OrdersEditViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(MachinesViewModel::class.java) -> {
                MachinesViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(MachinesAddViewModel::class.java) -> {
                MachinesAddViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(MachinesEditViewModel::class.java) -> {
                MachinesEditViewModel(preferencesRepository, apiService) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(RecoverViewModel::class.java) -> {
                RecoverViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(com.delvicier.fixagent.ui.screens.main.MainViewModel::class.java) -> {
                com.delvicier.fixagent.ui.screens.main.MainViewModel(preferencesRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}