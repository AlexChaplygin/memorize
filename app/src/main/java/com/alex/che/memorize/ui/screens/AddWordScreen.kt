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
import com.alex.che.memorize.viewmodel.AddWordViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddWordScreen(
    dictionaryId: Int,
    onNavigateBack: () -> Unit,
    viewModel: AddWordViewModel = koinViewModel(parameters = { parametersOf(dictionaryId) })
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
            // Word TextField
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Word:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(100.dp),
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = state.word,
                    onValueChange = { viewModel.onWordChange(it) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
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

            // Translation TextField
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Translation:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(100.dp),
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = state.translation,
                    onValueChange = { viewModel.onTranslationChange(it) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (state.word.isNotBlank() && state.translation.isNotBlank()) {
                                viewModel.saveWord(state.word, state.translation)
                            }
                        }
                    ),
                    enabled = !state.isLoading,
                    maxLines = 5,
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
                        if (state.word.isNotBlank() && state.translation.isNotBlank()) {
                            viewModel.saveWord(state.word, state.translation)
                        }
                    },
                    enabled = !state.isLoading && state.word.isNotBlank() && state.translation.isNotBlank(),
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
        state.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessMessage()
        }
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddWordScreenPreview() {
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
            // Word TextField
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Word:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(100.dp),
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = "Example",
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

            // Translation TextField
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Translation:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(100.dp),
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = "Пример",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    maxLines = 5,
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
