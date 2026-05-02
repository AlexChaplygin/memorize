package com.alex.che.memorize.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alex.che.memorize.entity.Word
import com.alex.che.memorize.viewmodel.TrainState
import com.alex.che.memorize.viewmodel.TrainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainScreen(
    dictionaryId: Int,
    trainDifficultWords: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: TrainViewModel = koinViewModel(
        parameters = { parametersOf(dictionaryId, trainDifficultWords) }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val gradientBrush = Brush.linearGradient(
        0.0f to Color(0xFF2196F3),
        1.0f to Color(0xFFF22828)
    )

    // Get hint chars once and remember them to prevent re-layout
    val hintChars = remember(state.currentWord) { viewModel.getHintChars() }.take(14)

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp),
                    color = Color.White
                )
            } else if (state.trainingComplete || state.currentWord == null) {
                // Training complete screen
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp)
                ) {
                    Text(
                        text = "Training complete!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You practiced ${state.totalWords} words",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
                }
            } else {
                // Training screen
                TrainingContent(
                    state = state,
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    hintChars = hintChars
                )
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }

    LaunchedEffect(state) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TrainingContent(
    state: TrainState,
    viewModel: TrainViewModel,
    snackbarHostState: SnackbarHostState,
    hintChars: List<Char>
) {
    val currentWord = state.currentWord ?: return

    // Auto-check on every text change
    LaunchedEffect(state.userAnswer) {
        if (state.userAnswer.isNotEmpty() && state.userAnswer == currentWord.word) {
            viewModel.checkAnswer()
        }
    }

    // Progress text
    Text(
        text = "Word ${state.currentCount} out of ${state.totalWords}.",
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        color = Color.White,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Word to translate (shows translation, user types word)
    Text(
        text = currentWord.translation,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // Answer TextField with backspace button
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        TextField(
            value = state.userAnswer,
            onValueChange = { viewModel.onUserAnswerChange(it) },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (state.userAnswer.isNotBlank()) {
                        viewModel.checkAnswer()
                    }
                }
            ),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Backspace button
        IconButton(
            onClick = { viewModel.removeHintChar() },
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Backspace",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Hint characters grid - centered and fixed to prevent re-layout
    if (hintChars.isNotEmpty()) {
        val numCols = minOf(hintChars.size, 7)
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        ) {
            hintChars.forEach { char ->
                var isPressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.85f else 1f,
                    animationSpec = tween(durationMillis = 100),
                    label = "scale"
                )

                // Reset isPressed after animation
                LaunchedEffect(isPressed) {
                    if (isPressed) {
                        delay(150)
                        isPressed = false
                    }
                }

                Button(
                    onClick = {
                        isPressed = true
                        viewModel.addHintChar(char)
                    },
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = char.toString(),
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Check button
    Button(
        onClick = {
            if (state.userAnswer.isNotBlank()) {
                viewModel.checkAnswer()
            }
        },
        enabled = state.userAnswer.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors()
    ) {
        Text("Check", fontSize = 16.sp)
    }

    // Help button and Is difficult checkbox
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        // Help button
        IconButton(
            onClick = { viewModel.addHintFromHelp() },
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Help",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
        
        // Is difficult checkbox
        androidx.compose.material3.Checkbox(
            checked = currentWord.isDifficult,
            onCheckedChange = { viewModel.toggleIsDifficult(it) },
            colors = androidx.compose.material3.CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White
            )
        )
        Text(
            text = "Is difficult",
            color = Color.White,
            fontSize = 16.sp
        )
    }

    // Divider
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White)
            .padding(top = 24.dp)
    )

    // Loading/Result indicator
    if (state.showResult) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (state.isAnswerCorrect == true) "✓ Correct!" else "✗ Try again",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (state.isAnswerCorrect == true) Color.Green else Color.Red
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrainScreenPreview() {
    val gradientBrush = Brush.linearGradient(
        0.0f to Color(0xFF2196F3),
        1.0f to Color(0xFFF22828)
    )

    val previewWord = Word(
        id = 1,
        word = "Example",
        translation = "Пример",
        isDifficult = false,
        dictionaryId = 1,
        createdDate = LocalDateTime.now(),
        checkDate = LocalDateTime.now()
    )

    val previewState = TrainState(
        currentWord = previewWord,
        userAnswer = "Examp",
        isAnswerCorrect = null,
        currentCount = 5,
        totalWords = 50,
        isLoading = false,
        trainingComplete = false
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Progress text
            Text(
                text = "Word 5 out of 50.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Word to translate
            Text(
                text = "Пример",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Answer TextField
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                TextField(
                    value = "Examp",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {},
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Backspace",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Hint characters grid
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                repeat(14) { index ->
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(
                            text = "A",
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Check button
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text("Check", fontSize = 16.sp)
            }

            // Help button and Is difficult checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                // Help button
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Help",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                
                // Is difficult checkbox
                androidx.compose.material3.Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.White
                    )
                )
                Text(
                    text = "Is difficult",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
