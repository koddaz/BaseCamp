package com.basecampers.basecamp.tabs.booking.adminstuff

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterAdmin(adminViewModel: AdminViewModel = viewModel()) {
    val email = "admin@admin.com"
    val password = "test1234"

    Column(
    ) {
        Button(onClick = { adminViewModel.createAdminAccount(email, password) }) {
            Text(text = "Create Admin Account")
        }
    }
}