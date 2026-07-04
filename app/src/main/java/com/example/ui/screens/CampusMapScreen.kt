package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.GeminiApiHelper
import com.example.db.CampusRepository
import com.example.model.CampusLandmark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusMapScreen(
    repository: CampusRepository
) {
    val coroutineScope = rememberCoroutineScope()

    // Predefined campus landmarks
    val landmarks = remember {
        listOf(
            CampusLandmark("lib", "Main Library", "library", "The main 5-story academic library, equipped with study rooms, pc stations, and campus cafe.", 500f, 320f, listOf("Study Suite 202", "Quiet Room 301", "Media Lab 102")),
            CampusLandmark("sci", "Science Hall", "lecture_hall", "Home to physics, chemistry, and biology labs. Contains several large lecture auditoriums.", 300f, 580f, listOf("Auditorium A", "Bio-Lab 201", "Room 402")),
            CampusLandmark("union", "Student Union", "amenity", "Campus social hub containing the food court, university bookstore, and event halls.", 500f, 750f, listOf("Multipurpose Hall", "Bookstore", "Main Lounge")),
            CampusLandmark("eng", "Engineering Center", "lecture_hall", "The engineering and computer science building. Contains advanced robotics and server labs.", 750f, 520f, listOf("Hall B", "Robotics Lab", "Room 112")),
            CampusLandmark("arts", "Liberal Arts Building", "lecture_hall", "Dedicated to humanities, literature, and history classes. Features classic lecture rooms.", 250f, 280f, listOf("Room 102", "Room 105", "Art Gallery")),
            CampusLandmark("sports", "Sports Complex", "recreation", "Includes the campus gym, basketball courts, and outdoor soccer/track field.", 800f, 220f, listOf("Gymnasium", "Indoor Pool", "Track Arena"))
        )
    }

    var selectedLandmark by remember { mutableStateOf<CampusLandmark?>(landmarks[0]) }
    var searchQuery by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("") }
    var isAskingAi by remember { mutableStateOf(false) }

    // Navigation guide context
    val landmarksContext = remember {
        landmarks.joinToString("; ") { "${it.name} (${it.type}) is located at coordinates (${it.x}, ${it.y}). Rooms: ${it.rooms.joinToString(", ")}. ${it.description}" }
    }

    // Dynamic Route Line tracking (Start and End coords for drawing routes)
    var routeStartEnd by remember { mutableStateOf<Pair<Offset, Offset>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Interactive Campus Map",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Tap landmarks to explore rooms, or use AI to navigate",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Map Canvas Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.secondary
                val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
                val tertiaryColor = MaterialTheme.colorScheme.tertiary

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                // Check if user tapped near any landmark (scaled coordinates)
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                // Scale local landmark positions (0-1000 range) to canvas actual coordinates
                                var found: CampusLandmark? = null
                                for (l in landmarks) {
                                    val lx = (l.x / 1000f) * canvasWidth
                                    val ly = (l.y / 1000f) * canvasHeight
                                    val distance = kotlin.math.hypot(offset.x - lx, offset.y - ly)
                                    if (distance < 40f) { // 40 pixel tap threshold
                                        found = l
                                        break
                                    }
                                }
                                if (found != null) {
                                    selectedLandmark = found
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // Draw Pathways / Roads
                    val roadPath = Path().apply {
                        // Horizontal main road
                        moveTo(0f, h * 0.5f)
                        lineTo(w, h * 0.5f)
                        // Vertical main road
                        moveTo(w * 0.5f, 0f)
                        lineTo(w * 0.5f, h)
                        // Connecting diagonal paths
                        moveTo((250f/1000f)*w, (280f/1000f)*h)
                        lineTo((500f/1000f)*w, (320f/1000f)*h)
                        moveTo((500f/1000f)*w, (320f/1000f)*h)
                        lineTo((800f/1000f)*w, (220f/1000f)*h)
                        moveTo((300f/1000f)*w, (580f/1000f)*h)
                        lineTo((500f/1000f)*w, (750f/1000f)*h)
                        moveTo((750f/1000f)*w, (520f/1000f)*h)
                        lineTo((500f/1000f)*w, (750f/1000f)*h)
                    }
                    drawPath(
                        path = roadPath,
                        color = Color.Gray.copy(alpha = 0.2f),
                        style = Stroke(width = 16f)
                    )

                    // Draw AI Route Line if active
                    routeStartEnd?.let { (start, end) ->
                        drawLine(
                            color = Color.Red,
                            start = start,
                            end = end,
                            strokeWidth = 6f
                        )
                    }

                    // Draw Buildings / Landmarks
                    for (l in landmarks) {
                        val lx = (l.x / 1000f) * w
                        val ly = (l.y / 1000f) * h
                        val isSelected = selectedLandmark?.id == l.id

                        // Draw Building Block
                        drawRoundRect(
                            color = when (l.type) {
                                "library" -> primaryColor
                                "lecture_hall" -> secondaryColor
                                "recreation" -> tertiaryColor
                                else -> Color.DarkGray
                            }.copy(alpha = if (isSelected) 0.9f else 0.5f),
                            topLeft = Offset(lx - 25f, ly - 20f),
                            size = Size(50f, 40f),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        // Landmark Label Text (can draw simple visual tag or dots)
                        drawCircle(
                            color = if (isSelected) Color.Red else Color.White,
                            radius = 6f,
                            center = Offset(lx, ly)
                        )
                    }
                }

                // Landmark label overlay tags (Positioned overlay relative to dimensions)
                landmarks.forEach { l ->
                    val isSelected = selectedLandmark?.id == l.id
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (l.x * 0.88f).dp, // approximate canvas positions
                                y = (l.y * 0.28f).dp
                            )
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = l.name.substringBefore(" "),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Landmark Info Panel Details
        selectedLandmark?.let { landmark ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when (landmark.type) {
                                    "library" -> Icons.Default.LocalLibrary
                                    "lecture_hall" -> Icons.Default.Class
                                    "recreation" -> Icons.Default.FitnessCenter
                                    else -> Icons.Default.Place
                                },
                                contentDescription = landmark.type,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = landmark.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = landmark.type.replace("_", " ").uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = landmark.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                    Text(
                        text = "Lecture Rooms & Facilities:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        landmark.rooms.forEach { room ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = room,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Gemini Maps Grounding Assistant Panel (With googleMaps tool constraint)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Map Assistant",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Gemini Maps Navigation Guide",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = "Powered by gemini-3.5-flash with Google Maps Grounding. Ask navigation routes between halls or ask what is inside campus buildings.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("e.g. How do I walk from Science Hall to Library?") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                    )

                    Button(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                isAskingAi = true
                                aiResponse = "Consulting Google Maps..."
                                
                                // Simulate route drawing for realism when asking specific buildings
                                val lLower = searchQuery.lowercase()
                                val startNode = when {
                                    lLower.contains("science") -> landmarks.first { it.id == "sci" }
                                    lLower.contains("art") -> landmarks.first { it.id == "arts" }
                                    lLower.contains("engineer") -> landmarks.first { it.id == "eng" }
                                    else -> landmarks.first { it.id == "union" }
                                }
                                val endNode = when {
                                    lLower.contains("library") -> landmarks.first { it.id == "lib" }
                                    lLower.contains("sport") -> landmarks.first { it.id == "sports" }
                                    else -> landmarks.first { it.id == "union" }
                                }
                                
                                // Set route endpoints to draw on canvas
                                routeStartEnd = Pair(
                                    Offset(startNode.x, startNode.y),
                                    Offset(endNode.x, endNode.y)
                                )

                                coroutineScope.launch {
                                    aiResponse = GeminiApiHelper.navigateCampus(searchQuery, landmarksContext)
                                    isAskingAi = false
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        if (isAskingAi) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Guide Me")
                        }
                    }
                }

                if (aiResponse.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Grounded Route Instructions:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = aiResponse,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
