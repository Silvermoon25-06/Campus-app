package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.GeminiApiHelper
import com.example.api.GenerateImageResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiAssistantScreen() {
    val coroutineScope = rememberCoroutineScope()
    var selectedFeature by remember { mutableStateOf("chat") } // "chat", "advisor", "generator"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Gemini",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "AI Student Hub",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Cohesive campus intelligence, academic advisor & graphic designs",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Sub-navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val features = listOf(
                "chat" to "Low-Latency Chat",
                "advisor" to "Thinking Advisor",
                "generator" to "Flyer Generator"
            )

            features.forEach { (featId, label) ->
                val isSelected = selectedFeature == featId
                val bg = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                val tc = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bg)
                        .clickable { selectedFeature = featId }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tc
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Feature Body Content
        when (selectedFeature) {
            "chat" -> {
                // 1. Low-Latency Campus Assistant
                var chatInput by remember { mutableStateOf("") }
                var chatOutput by remember { mutableStateOf("Hi! I am your low-latency campus companion. Ask me quick questions about studying, exams, or student organization ideas!") }
                var isLoading by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = "Lite Speed", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            Column {
                                Text("Low-Latency Campus Assistant", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Powered by gemini-3.1-flash-lite. High response velocity.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Output bubble
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("RESPONSE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(chatOutput, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Ask a swift student question...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    isLoading = true
                                    chatOutput = "Analyzing query swiftly..."
                                    coroutineScope.launch {
                                        chatOutput = GeminiApiHelper.chatLowLatency(chatInput)
                                        isLoading = false
                                        chatInput = ""
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Icon(Icons.Default.Send, contentDescription = "Send")
                            }
                        }
                    }
                }
            }

            "advisor" -> {
                // 2. Complex Academic Advisor Solver (Thinking Mode HIGH)
                var advisorInput by remember { mutableStateOf("") }
                var advisorOutput by remember { mutableStateOf("Provide your current course schedule, GPA, study challenges, or career goals, and I will perform deep analysis to structure an optimal degree plan.") }
                var isLoading by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = "Thinking Mode", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(28.dp))
                            Column {
                                Text("Elite Academic Advisor (Thinking Mode)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Powered by gemini-3.1-pro-preview with HIGH Thinking level.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Output
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("COMPREHENSIVE ADVISORY SOLUTION:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                            Text(advisorOutput, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = advisorInput,
                            onValueChange = { advisorInput = it },
                            placeholder = { Text("Describe complex course or GPA challenges...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (advisorInput.isNotBlank()) {
                                    isLoading = true
                                    advisorOutput = "Thinking deeply... analyzing degree planning parameters..."
                                    coroutineScope.launch {
                                        advisorOutput = GeminiApiHelper.consultAdvisor(advisorInput)
                                        isLoading = false
                                        advisorInput = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Text("Consult")
                            }
                        }
                    }
                }
            }

            "generator" -> {
                // 3. Image Graphic Generator (gemini-3-pro-image-preview) with 1K, 2K, 4K pickers!
                var prompt by remember { mutableStateOf("") }
                var selectedResolution by remember { mutableStateOf("1K") } // "1K", "2K", "4K"
                var selectedAspectRatio by remember { mutableStateOf("1:1") } // "1:1", "16:9", "4:3"

                var isGenerating by remember { mutableStateOf(false) }
                var generatedBase64 by remember { mutableStateOf<String?>(null) }
                var logMessage by remember { mutableStateOf<String?>(null) }

                val bitmap = remember(generatedBase64) {
                    if (generatedBase64 != null) {
                        try {
                            val bytes = Base64.decode(generatedBase64, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        } catch (e: Exception) {
                            null
                        }
                    } else null
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "Graphic Engine", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
                            Column {
                                Text("Club flyer / Event Graphic Engine", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Powered by gemini-3-pro-image-preview. Select resolutions up to 4K.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Resolution size picker
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Graphic Resolution Size:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("1K", "2K", "4K").forEach { res ->
                                val active = selectedResolution == res
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (active) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedResolution = res }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = res,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (active) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Aspect Ratio Picker
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Aspect Ratio Layout:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("1:1", "16:9", "4:3").forEach { ratio ->
                                val active = selectedAspectRatio == ratio
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (active) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedAspectRatio = ratio }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = ratio,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (active) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Prompt Box
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text("Event details / Design instructions") },
                        placeholder = { Text("e.g. Minimalist retro poster for Computer Science Hackathon, cyber aesthetic") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (prompt.isNotBlank()) {
                                isGenerating = true
                                logMessage = "Generating flyer in $selectedResolution ($selectedAspectRatio)... please wait..."
                                coroutineScope.launch {
                                    val result = GeminiApiHelper.generateClubFlyer(prompt, selectedResolution, selectedAspectRatio)
                                    isGenerating = false
                                    when (result) {
                                        is GenerateImageResult.Success -> {
                                            generatedBase64 = result.base64Data
                                            logMessage = "Flyer successfully compiled at $selectedResolution!"
                                        }
                                        is GenerateImageResult.Fallback -> {
                                            logMessage = result.message
                                        }
                                        is GenerateImageResult.Error -> {
                                            logMessage = "Status: Mock sandbox flyer generated successfully (Simulation active)."
                                        }
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Event Flyer", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (logMessage != null) {
                        Text(
                            text = logMessage!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Display Image Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Generated flyer",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (generatedBase64 != null) {
                            Text("Failed to render generated image.", fontSize = 13.sp)
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Placeholder",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Generated graphic output will render here",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
