package com.zenith26.fest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zenith26.fest.ui.screens.AdminDashboardScreen
import com.zenith26.fest.ui.screens.StudentPortalScreen
import com.zenith26.fest.ui.theme.FestBg
import com.zenith26.fest.ui.theme.ZenithTheme
import com.zenith26.fest.ui.viewmodel.AdminViewModel
import com.zenith26.fest.ui.viewmodel.StudentViewModel

enum class ScreenState {
    STUDENT_PORTAL,
    ADMIN_DASHBOARD
}

class MainActivity : ComponentActivity() {

    private val studentViewModel: StudentViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZenithTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FestBg
                ) {
                    ZenithApp(
                        studentViewModel = studentViewModel,
                        adminViewModel = adminViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ZenithApp(
    studentViewModel: StudentViewModel,
    adminViewModel: AdminViewModel
) {
    var currentScreen by remember { mutableStateOf(ScreenState.STUDENT_PORTAL) }

    // Handle system back button: if in admin screen, return to student portal
    BackHandler(enabled = currentScreen == ScreenState.ADMIN_DASHBOARD) {
        currentScreen = ScreenState.STUDENT_PORTAL
    }

    when (currentScreen) {
        ScreenState.STUDENT_PORTAL -> {
            StudentPortalScreen(
                viewModel = studentViewModel,
                onNavigateToAdmin = {
                    adminViewModel.loadDashboardData()
                    currentScreen = ScreenState.ADMIN_DASHBOARD
                }
            )
        }

        ScreenState.ADMIN_DASHBOARD -> {
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onNavigateToStudent = {
                    studentViewModel.loadEvents()
                    currentScreen = ScreenState.STUDENT_PORTAL
                }
            )
        }
    }
}
