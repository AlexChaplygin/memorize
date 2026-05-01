package com.alex.che.memorize.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alex.che.memorize.viewmodel.CreateDictionaryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateDictionaryScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateDictionaryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val gradientBrush = Brush.linearGradient(
        0.0f to Color(0xFF2196F3),
        1.0f to Color(0xFFF22828)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "New dictionary",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Dictionary Name TextField
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Name:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(100.dp),
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = state.dictionaryName,
                    onValueChange = { viewModel.onDictionaryNameChange(it) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (state.dictionaryName.isNotBlank()) {
                                viewModel.createDictionary()
                            }
                        }
                    ),
                    enabled = !state.isLoading,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.DarkGray,
                        disabledIndicatorColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.Gray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons Row
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Button(
                    onClick = {
                        if (state.dictionaryName.isNotBlank()) {
                            viewModel.createDictionary()
                        }
                    },
                    enabled = !state.isLoading && state.dictionaryName.isNotBlank(),
                    modifier = Modifier.padding(end = 15.dp),
                    colors = ButtonDefaults.buttonColors(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Save")
                }

                Button(
                    onClick = onNavigateBack,
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Back")
                }
            }

            // Loading indicator
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp),
                    color = Color.White
                )
            }
        }

        // Snackbar for messages
        SnackbarHost(hostState = snackbarHostState)
    }

    LaunchedEffect(state) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
        if (state.success) {
            // Навигация параллельно с показом Snackbar
            onNavigateBack()
            snackbarHostState.showSnackbar("Dictionary created successfully")
            viewModel.clearSuccess()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateDictionaryScreenPreview() {
    // Preview without Koin - just show the UI structure
    val gradientBrush = Brush.linearGradient(
        0.0f to Color(0xFF2196F3),
        1.0f to Color(0xFFF22828)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "New dictionary",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Dictionary Name TextField
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Name:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(100.dp),
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = "My Dictionary",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.DarkGray,
                        disabledIndicatorColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.Gray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                )
            }

            // Buttons Row
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.padding(end = 15.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Save")
                }

                Button(
                    onClick = {},
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Back")
                }
            }
        }
    }
}
