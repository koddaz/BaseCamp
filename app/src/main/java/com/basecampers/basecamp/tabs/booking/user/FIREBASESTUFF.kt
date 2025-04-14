package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.navigation.models.AuthViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItems

@Composable
fun FIREBASETESTSTUFF(
    authViewModel: AuthViewModel = viewModel()
) {
    val userEmail = "user@example.com"
    val userPassword = "Admin123!"

    val superUserEmail = "super@example.com"
    val superUserPassword = "Admin123!"

    val adminEmail = "admin@example.com"
    val adminPassword = "Admin123!"

    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {


        CustomButton(
            text = "USER",
            onClick = {
               authViewModel.login(userEmail, userPassword)
            }
        )

        CustomButton(
            text = "SUPER",
            onClick = {
                authViewModel.login(superUserEmail, superUserPassword)
            }

        )

        CustomButton(
            text = "ADMIN",
            onClick = {
               authViewModel.login(adminEmail, adminPassword)
            }

        )
        CustomButton(text = "SIGN", onClick = { authViewModel.logout() })
    }

    /*
    CustomButton(text = "CREATE ADMIN ACCOUNT", onClick = {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                // Create company document
                val companyName = "Actual Company Name"  // Replace with actual company name or ID
                val companyData =
                    CompanyModel(
                        bio = "No bio yet",
                        imageUrl = null,
                        companyName = "A Company Inc",
                        ownerUID = userId
                    )


                // Create the company in Firestore
                db.collection("companies")
                    .document(companyName)
                    .set(companyData)
                    .addOnSuccessListener {
                        // Create admin user document within the company
                        val userData =
                            UserModel(
                                email = email,
                                name = "Regular User",
                                imageUrl = null,
                                bio = "Company User",
                                status = UserStatus.USER,
                                id = userId,
                                companyName = companyName
                            )


                        // Add the user to the company's users collection
                        db.collection("companies")
                            .document(companyName)
                            .collection("users")
                            .document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d("AdminCreation", "Admin user created successfully")
                                // You could show a success message or navigate to a different screen here
                            }
                            .addOnFailureListener { e ->
                                Log.e("AdminCreation", "Error creating admin user", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("AdminCreation", "Error creating company", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("AdminCreation", "Error creating auth user", e)
            }
    })

     */
}


@Composable
fun ItemCard(
    item: BookingItems,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                colorScheme.primaryContainer
            else
                colorScheme.surface )
    ) {
        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Text(text = item.name,
                color = if (isSelected)
                colorScheme.onPrimaryContainer
            else
                colorScheme.onSurface)
            Text(text = item.info,
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface)
            Text(text = "$${item.price}",
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface)

        }
    }
}


@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: BookingCategories,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    colorScheme.primaryContainer
                else
                    colorScheme.surface )
    ) {
        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Text(
                text = category.name,
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface
            )
        }
    }
}