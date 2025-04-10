package com.basecampers.basecamp.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel


@Composable
fun ConfirmPasswordTextField(password : String, onValueChange : (String) -> Unit, label : String, modifier : Modifier, authViewModel : AuthViewModel) {
	val state = remember { TextFieldState() }
	var showPassword by remember { mutableStateOf(false) }
	val errorMessage by authViewModel.registerErrorMessage.collectAsState()
	val isConfirmPasswordValid by authViewModel.confirmPasswordValid.collectAsState()
	
	val hasConfirmPasswordError = errorMessage.any { it in listOf(
		AuthViewModel.RegisterErrors.CONFIRM_PASSWORD_EMPTY,
		AuthViewModel.RegisterErrors.CONFIRM_PASSWORD_MISMATCH
	) }
	
	BasicSecureTextField(
		state = state,
		textObfuscationMode =
		if (showPassword) {
			TextObfuscationMode.Visible
		} else {
			TextObfuscationMode.RevealLastTyped
		},
		modifier = Modifier
			.width(360.dp)
			.padding(6.dp)
			.border(
				1.dp,
				when{
					isConfirmPasswordValid -> Color.Green
					hasConfirmPasswordError -> Color.Red
					else -> Color.LightGray
				}
			)
			.padding(6.dp),
		decorator = { innerTextField ->
			Box(modifier = Modifier.fillMaxWidth()) {
				Box(
					modifier = Modifier
						.align(alignment = Alignment.CenterStart)
						.padding(start = 16.dp, end = 48.dp)
				) {
					if (state.text.isEmpty()) {
						Text(
							text = label,
							color = Color.Gray,
							modifier = Modifier.align(alignment = Alignment.CenterStart)
						)
					}
					innerTextField()
				}
				Icon(
					if (showPassword) {
						Icons.Filled.Visibility
					} else {
						Icons.Filled.VisibilityOff
					},
					contentDescription = "Toggle password visibility",
					modifier = Modifier
						.align(alignment = Alignment.CenterEnd)
						.requiredSize(48.dp).padding(16.dp)
						.clickable { showPassword = !showPassword }
				)
			}
		}
	)
	
	LaunchedEffect(state.text, password) {
		onValueChange(state.text.toString())
		authViewModel.validateConfirmPasswordLive(password, state.text.toString())
		if(hasConfirmPasswordError && state.text.toString() != password) {
			authViewModel.clearConfirmPasswordErrors()
		}
	}
}

@Preview(showBackground = true)
@Composable
fun ConfirmPasswordTextFieldPreview() {
	ConfirmPasswordTextField(password = "", onValueChange = {}, label = "", modifier = Modifier, authViewModel = viewModel())
}