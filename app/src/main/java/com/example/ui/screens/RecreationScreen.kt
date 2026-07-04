package com.example.ui.screens

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecreationScreen() {
    var mainTab by remember { mutableStateOf("hobbies") } // "hobbies", "relaxation"

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
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = "Recreation",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Recreation & Life",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Unwind on campus with classic games, e-novels, and custom rig builders",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Primary Tabs (Hobbies, Relaxation)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("hobbies" to "Hobbies", "relaxation" to "Relaxation").forEach { (tabId, label) ->
                val isSelected = mainTab == tabId
                val bg = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                val tc = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bg)
                        .clickable { mainTab = tabId }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = tc
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            if (mainTab == "hobbies") {
                HobbiesSection()
            } else {
                RelaxationSection()
            }
        }
    }
}

// ==========================================
// HOBBIES SECTION
// ==========================================
@Composable
fun HobbiesSection() {
    var hobbySubTab by remember { mutableStateOf("pcpartpicker") } // "pcpartpicker", "webnovel"

    Column(modifier = Modifier.fillMaxSize()) {
        // Hobby selection pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = hobbySubTab == "pcpartpicker",
                onClick = { hobbySubTab = "pcpartpicker" },
                label = { Text("PCPartPicker Build Feed") },
                leadingIcon = { Icon(Icons.Default.DeveloperBoard, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = hobbySubTab == "webnovel",
                onClick = { hobbySubTab = "webnovel" },
                label = { Text("Web Novels") },
                leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (hobbySubTab == "pcpartpicker") {
                PCPartPickerFeed()
            } else {
                WebNovelViewer()
            }
        }
    }
}

@Composable
fun PCPartPickerFeed() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Status indicator
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Computer, contentDescription = "PCPartPicker Logo", tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(
                        text = "Live PCPartPicker Explorer",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Real-time completed builds, compatibility engine, and hardware prices.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // The live feed web view
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            databaseEnabled = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.0.0 Mobile Safari/537.36"
                        }
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                return false // Load internally
                            }
                        }
                        loadUrl("https://pcpartpicker.com/builds/")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun WebNovelViewer() {
    // Elegant warm-toned E-Reader Theme custom wrapper (independent of app's main theme)
    // Warm Sepia Theme Colors
    val readerBackground = Color(0xFFF4ECD8)
    val readerSurface = Color(0xFFEFE5CD)
    val readerText = Color(0xFF4A3C28)
    val readerPrimary = Color(0xFF8C6239)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBackground)
    ) {
        // Sepia E-Reader Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(readerSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "Reader Mode",
                    tint = readerPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "Sepia E-Reader: Webnovel",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = readerText
                    )
                    Text(
                        text = "webnovel.com live stream • Ambient Eye-Care Mode",
                        fontSize = 11.sp,
                        color = readerText.copy(alpha = 0.7f)
                    )
                }
            }

            // Quick reset button
            IconButton(
                onClick = { /* Could reload WebView */ },
                modifier = Modifier
                    .background(readerText.copy(alpha = 0.05f), CircleShape)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload",
                    tint = readerText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Live WebView taking everything live from webnovel.com
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, readerSurface, RoundedCornerShape(12.dp))
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            databaseEnabled = true
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.0.0 Mobile Safari/537.36"
                        }
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                return false // Load inside view
                            }
                        }
                        loadUrl("https://www.webnovel.com")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ==========================================
// RELAXATION SECTION
// ==========================================
@Composable
fun RelaxationSection() {
    var activeGame by remember { mutableStateOf("minesweeper") } // "minesweeper", "tetris", "solitaire"

    Column(modifier = Modifier.fillMaxSize()) {
        // Game selection Row
        ScrollableTabRow(
            selectedTabIndex = when (activeGame) {
                "minesweeper" -> 0
                "tetris" -> 1
                else -> 2
            },
            containerColor = Color.Transparent,
            edgePadding = 16.dp,
            divider = {}
        ) {
            Tab(
                selected = activeGame == "minesweeper",
                onClick = { activeGame = "minesweeper" },
                text = { Text("Minesweeper", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = activeGame == "tetris",
                onClick = { activeGame = "tetris" },
                text = { Text("Tetris", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = activeGame == "solitaire",
                onClick = { activeGame = "solitaire" },
                text = { Text("Klondike Solitaire", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (activeGame) {
                "minesweeper" -> MinesweeperGame()
                "tetris" -> TetrisGame()
                "solitaire" -> KlondikeSolitaireGame()
            }
        }
    }
}

// ==========================================
// 1. MINESWEEPER GAME
// ==========================================
data class MineCell(
    val r: Int,
    val c: Int,
    var isMine: Boolean = false,
    var isRevealed: Boolean = false,
    var isFlagged: Boolean = false,
    var adjacentCount: Int = 0
)

@Composable
fun MinesweeperGame() {
    val rows = 9
    val cols = 9
    val totalMines = 10

    var grid by remember { mutableStateOf(createMinesweeperGrid(rows, cols, totalMines)) }
    var isGameOver by remember { mutableStateOf(false) }
    var isGameWon by remember { mutableStateOf(false) }
    var flagsCount by remember { mutableStateOf(0) }

    fun resetGame() {
        grid = createMinesweeperGrid(rows, cols, totalMines)
        isGameOver = false
        isGameWon = false
        flagsCount = 0
    }

    fun checkWinCondition() {
        val allNonMinesRevealed = grid.all { cell -> cell.isMine || cell.isRevealed }
        if (allNonMinesRevealed) {
            isGameWon = true
            isGameOver = true
        }
    }

    fun revealCell(cell: MineCell) {
        if (isGameOver || cell.isRevealed || cell.isFlagged) return

        val newGrid = grid.toMutableList()
        val index = cell.r * cols + cell.c
        val current = newGrid[index].copy(isRevealed = true)
        newGrid[index] = current

        if (current.isMine) {
            // Explode! Reveal all mines
            grid = newGrid.map { if (it.isMine) it.copy(isRevealed = true) else it }
            isGameOver = true
            return
        }

        if (current.adjacentCount == 0) {
            // Cascade reveal empty cells
            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(Pair(current.r, current.c))
            val visited = mutableSetOf<Pair<Int, Int>>()
            visited.add(Pair(current.r, current.c))

            while (queue.isNotEmpty()) {
                val (currR, currC) = queue.removeFirst()
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        val nr = currR + dr
                        val nc = currC + dc
                        if (nr in 0 until rows && nc in 0 until cols && !visited.contains(Pair(nr, nc))) {
                            val nIdx = nr * cols + nc
                            val neighbor = newGrid[nIdx]
                            if (!neighbor.isMine && !neighbor.isRevealed && !neighbor.isFlagged) {
                                newGrid[nIdx] = neighbor.copy(isRevealed = true)
                                visited.add(Pair(nr, nc))
                                if (neighbor.adjacentCount == 0) {
                                    queue.add(Pair(nr, nc))
                                }
                            }
                        }
                    }
                }
            }
        }

        grid = newGrid
        checkWinCondition()
    }

    fun toggleFlag(cell: MineCell) {
        if (isGameOver || cell.isRevealed) return
        val newGrid = grid.toMutableList()
        val index = cell.r * cols + cell.c
        val current = newGrid[index]
        val isFlaggedNow = !current.isFlagged

        newGrid[index] = current.copy(isFlagged = isFlaggedNow)
        grid = newGrid
        flagsCount += if (isFlaggedNow) 1 else -1
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Minesweeper Status Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Flag, contentDescription = "Flags", tint = MaterialTheme.colorScheme.error)
                    Text(text = "Flags: $flagsCount/$totalMines", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { resetGame() },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isGameWon) Icons.Default.SentimentVerySatisfied
                                      else if (isGameOver) Icons.Default.SentimentVeryDissatisfied
                                      else Icons.Default.SentimentSatisfied,
                        contentDescription = "Restart",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = if (isGameWon) "You Won! 🎉" else if (isGameOver) "Kaboom! 💥" else "Find all mines",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGameWon) Color(0xFF4CAF50) else if (isGameOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            // Grid Canvas/Buttons
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (r in 0 until rows) {
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            for (c in 0 until cols) {
                                val cell = grid[r * cols + c]
                                val bg = when {
                                    cell.isRevealed -> if (cell.isMine) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(bg)
                                        .pointerInput(cell) {
                                            detectTapGestures(
                                                onTap = { revealCell(cell) },
                                                onLongPress = { toggleFlag(cell) }
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (cell.isRevealed) {
                                        if (cell.isMine) {
                                            Icon(Icons.Default.Dangerous, contentDescription = "Bomb", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        } else if (cell.adjacentCount > 0) {
                                            Text(
                                                text = cell.adjacentCount.toString(),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = when (cell.adjacentCount) {
                                                    1 -> Color.Blue
                                                    2 -> Color(0xFF4CAF50)
                                                    3 -> Color.Red
                                                    4 -> Color(0xFF800080)
                                                    else -> Color.DarkGray
                                                }
                                            )
                                        }
                                    } else if (cell.isFlagged) {
                                        Icon(Icons.Default.Flag, contentDescription = "Flagged", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = "Tip: Tap to reveal, long press to flag mines.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

fun createMinesweeperGrid(rows: Int, cols: Int, totalMines: Int): List<MineCell> {
    val list = MutableList(rows * cols) { idx -> MineCell(r = idx / cols, c = idx % cols) }
    var minesPlaced = 0
    val rand = Random()

    while (minesPlaced < totalMines) {
        val r = rand.nextInt(rows)
        val c = rand.nextInt(cols)
        val idx = r * cols + c
        if (!list[idx].isMine) {
            list[idx].isMine = true
            minesPlaced++
        }
    }

    // Calc adjacent mines count
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            val idx = r * cols + c
            if (list[idx].isMine) continue

            var count = 0
            for (dr in -1..1) {
                for (dc in -1..1) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until rows && nc in 0 until cols) {
                        if (list[nr * cols + nc].isMine) {
                            count++
                        }
                    }
                }
            }
            list[idx].adjacentCount = count
        }
    }

    return list
}

// ==========================================
// 2. TETRIS GAME
// ==========================================
@Composable
fun TetrisGame() {
    val rows = 20
    val cols = 10

    var grid by remember { mutableStateOf(Array(rows) { Array(cols) { Color.Transparent } }) }
    var gameScore by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    // Piece definitions (matrices)
    val shapes = listOf(
        // I shape
        listOf(listOf(1, 1, 1, 1)),
        // J shape
        listOf(listOf(1, 0, 0), listOf(1, 1, 1)),
        // L shape
        listOf(listOf(0, 0, 1), listOf(1, 1, 1)),
        // O shape
        listOf(listOf(1, 1), listOf(1, 1)),
        // S shape
        listOf(listOf(0, 1, 1), listOf(1, 1, 0)),
        // T shape
        listOf(listOf(0, 1, 0), listOf(1, 1, 1)),
        // Z shape
        listOf(listOf(1, 1, 0), listOf(0, 1, 1))
    )

    val shapeColors = listOf(
        Color(0xFF00FFFF), // Cyan
        Color(0xFF0000FF), // Blue
        Color(0xFFFFA500), // Orange
        Color(0xFFFFD700), // Yellow
        Color(0xFF00FF00), // Green
        Color(0xFF800080), // Purple
        Color(0xFFFF0000)  // Red
    )

    var currentShapeIdx by remember { mutableStateOf((0..6).random()) }
    var currentShape by remember { mutableStateOf(shapes[currentShapeIdx]) }
    var currentPieceColor by remember { mutableStateOf(shapeColors[currentShapeIdx]) }
    var pieceR by remember { mutableStateOf(0) }
    var pieceC by remember { mutableStateOf(cols / 2 - 1) }

    fun checkCollision(newR: Int, newC: Int, shape: List<List<Int>>): Boolean {
        for (r in shape.indices) {
            for (c in shape[r].indices) {
                if (shape[r][c] != 0) {
                    val gridR = newR + r
                    val gridC = newC + c
                    if (gridR >= rows || gridC < 0 || gridC >= cols) return true
                    if (gridR >= 0 && grid[gridR][gridC] != Color.Transparent) return true
                }
            }
        }
        return false
    }

    fun mergePiece() {
        val newGrid = Array(rows) { r -> Array(cols) { c -> grid[r][c] } }
        for (r in currentShape.indices) {
            for (c in currentShape[r].indices) {
                if (currentShape[r][c] != 0) {
                    val gridR = pieceR + r
                    val gridC = pieceC + c
                    if (gridR in 0 until rows && gridC in 0 until cols) {
                        newGrid[gridR][gridC] = currentPieceColor
                    }
                }
            }
        }

        // Clear full lines
        var cleared = 0
        val finalGrid = Array(rows) { Array(cols) { Color.Transparent } }
        var targetR = rows - 1
        for (r in rows - 1 downTo 0) {
            val isFull = newGrid[r].all { it != Color.Transparent }
            if (isFull) {
                cleared++
            } else {
                for (c in 0 until cols) {
                    finalGrid[targetR][c] = newGrid[r][c]
                }
                targetR--
            }
        }

        grid = finalGrid
        gameScore += cleared * 100

        // Spawn new piece
        currentShapeIdx = (0..6).random()
        currentShape = shapes[currentShapeIdx]
        currentPieceColor = shapeColors[currentShapeIdx]
        pieceR = 0
        pieceC = cols / 2 - currentShape[0].size / 2

        if (checkCollision(pieceR, pieceC, currentShape)) {
            isGameOver = true
            isPlaying = false
        }
    }

    fun tick() {
        if (!isPlaying || isGameOver) return
        if (!checkCollision(pieceR + 1, pieceC, currentShape)) {
            pieceR++
        } else {
            mergePiece()
        }
    }

    // Gameloop
    LaunchedEffect(isPlaying, isGameOver) {
        while (isPlaying && !isGameOver) {
            delay(500)
            tick()
        }
    }

    fun rotatePiece() {
        if (isGameOver || !isPlaying) return
        val currentH = currentShape.size
        val currentW = currentShape[0].size
        val rotated = List(currentW) { r ->
            List(currentH) { c ->
                currentShape[currentH - 1 - c][r]
            }
        }
        if (!checkCollision(pieceR, pieceC, rotated)) {
            currentShape = rotated
        }
    }

    fun moveLeft() {
        if (isGameOver || !isPlaying) return
        if (!checkCollision(pieceR, pieceC - 1, currentShape)) {
            pieceC--
        }
    }

    fun moveRight() {
        if (isGameOver || !isPlaying) return
        if (!checkCollision(pieceR, pieceC + 1, currentShape)) {
            pieceC++
        }
    }

    fun dropDown() {
        if (isGameOver || !isPlaying) return
        if (!checkCollision(pieceR + 1, pieceC, currentShape)) {
            pieceR++
        } else {
            mergePiece()
        }
    }

    fun resetGame() {
        grid = Array(rows) { Array(cols) { Color.Transparent } }
        gameScore = 0
        isGameOver = false
        currentShapeIdx = (0..6).random()
        currentShape = shapes[currentShapeIdx]
        currentPieceColor = shapeColors[currentShapeIdx]
        pieceR = 0
        pieceC = cols / 2 - currentShape[0].size / 2
        isPlaying = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Stats & State
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Score: $gameScore", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Button(
                    onClick = {
                        if (isGameOver) resetGame()
                        else isPlaying = !isPlaying
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = if (isGameOver) "Restart" else if (isPlaying) "Pause" else "Play")
                }
            }

            // Tetris Screen Grid Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(0.5f)
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0 until rows) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (c in 0 until cols) {
                                // Draw cell
                                var color = grid[r][c]

                                // Overlay active block piece
                                val pieceRowOffset = r - pieceR
                                val pieceColOffset = c - pieceC
                                if (pieceRowOffset in currentShape.indices && pieceColOffset in currentShape[0].indices) {
                                    if (currentShape[pieceRowOffset][pieceColOffset] != 0 && isPlaying) {
                                        color = currentPieceColor
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(1.dp)
                                        .background(if (color == Color.Transparent) Color(0xFF1E1E1E) else color, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                }

                if (isGameOver) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("GAME OVER", color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Text("Score: $gameScore", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Game Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { moveLeft() }, modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Left", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                IconButton(onClick = { rotatePiece() }, modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)) {
                    Icon(Icons.Default.RotateRight, contentDescription = "Rotate", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                IconButton(onClick = { dropDown() }, modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Soft Drop", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                IconButton(onClick = { moveRight() }, modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Right", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
    }
}

// ==========================================
// 3. KLONDIKE SOLITAIRE GAME (LITE & COMPACT)
// ==========================================
data class CardModel(
    val id: String = UUID.randomUUID().toString(),
    val rank: String,
    val suit: String,
    val isRed: Boolean,
    var isFaceUp: Boolean = false
)

@Composable
fun KlondikeSolitaireGame() {
    val suits = listOf("♥", "♦", "♣", "♠") // Hearts, Diamonds (Red), Clubs, Spades (Black)
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

    // Board states
    var deckState by remember { mutableStateOf<List<CardModel>>(emptyList()) }
    var stockPile by remember { mutableStateOf<List<CardModel>>(emptyList()) }
    var wastePile by remember { mutableStateOf<List<CardModel>>(emptyList()) }
    // 4 Foundations (Hearts, Diamonds, Clubs, Spades)
    var foundations by remember { mutableStateOf(List(4) { emptyList<CardModel>() }) }
    // 4 Tableau Columns (Simplified for screen real estate)
    var tableaus by remember { mutableStateOf(List(4) { emptyList<CardModel>() }) }

    var selectedWasteCard by remember { mutableStateOf<CardModel?>(null) }
    var selectedTableauIdx by remember { mutableStateOf<Pair<Int, Int>?>(null) } // tableauIndex to cardIndex
    var winMessage by remember { mutableStateOf(false) }

    fun initializeSolitaire() {
        val newDeck = mutableListOf<CardModel>()
        suits.forEach { s ->
            ranks.forEach { r ->
                newDeck.add(CardModel(rank = r, suit = s, isRed = (s == "♥" || s == "♦")))
            }
        }
        newDeck.shuffle()

        // Populate Tableaus: col 0 has 1 card, col 1 has 2 cards, col 2 has 3 cards, col 3 has 4 cards
        val tempTableaus = MutableList(4) { mutableListOf<CardModel>() }
        var deckIdx = 0
        for (i in 0 until 4) {
            for (j in 0..i) {
                val card = newDeck[deckIdx++]
                if (j == i) card.isFaceUp = true // Flip top card up
                tempTableaus[i].add(card)
            }
        }

        // Rest of cards go to Stock
        val tempStock = mutableListOf<CardModel>()
        while (deckIdx < newDeck.size) {
            tempStock.add(newDeck[deckIdx++])
        }

        tableaus = tempTableaus
        stockPile = tempStock
        wastePile = emptyList()
        foundations = List(4) { emptyList() }
        selectedWasteCard = null
        selectedTableauIdx = null
        winMessage = false
    }

    // Initialize once
    LaunchedEffect(Unit) {
        initializeSolitaire()
    }

    fun drawCard() {
        if (stockPile.isEmpty()) {
            // Recycle waste back to stock
            stockPile = wastePile.map { it.copy(isFaceUp = false) }.reversed()
            wastePile = emptyList()
        } else {
            val topCard = stockPile.first().copy(isFaceUp = true)
            stockPile = stockPile.drop(1)
            wastePile = listOf(topCard) + wastePile
        }
        selectedWasteCard = null
        selectedTableauIdx = null
    }

    fun checkWinState() {
        if (foundations.all { it.size == 13 }) {
            winMessage = true
        }
    }

    fun canMoveToFoundation(card: CardModel, fIdx: Int): Boolean {
        val fPile = foundations[fIdx]
        if (fPile.isEmpty()) {
            return card.rank == "A" && card.suit == suits[fIdx]
        }
        val top = fPile.last()
        val currentRankIdx = ranks.indexOf(card.rank)
        val expectedRankIdx = ranks.indexOf(top.rank) + 1
        return expectedRankIdx == currentRankIdx && card.suit == top.suit
    }

    fun canMoveToTableau(card: CardModel, tIdx: Int): Boolean {
        val tPile = tableaus[tIdx]
        if (tPile.isEmpty()) {
            return card.rank == "K"
        }
        val top = tPile.last()
        val currentRankIdx = ranks.indexOf(card.rank)
        val targetRankIdx = ranks.indexOf(top.rank) - 1
        return targetRankIdx == currentRankIdx && card.isRed != top.isRed
    }

    fun onWasteCardClick() {
        selectedTableauIdx = null
        if (wastePile.isNotEmpty()) {
            val top = wastePile.first()
            selectedWasteCard = if (selectedWasteCard == top) null else top
        }
    }

    fun onTableauClick(tColIdx: Int, cardIdx: Int) {
        val tPile = tableaus[tColIdx]
        if (tPile.isEmpty()) {
            // Can move King here if selected
            selectedWasteCard?.let { card ->
                if (canMoveToTableau(card, tColIdx)) {
                    tableaus = tableaus.mapIndexed { idx, col ->
                        if (idx == tColIdx) col + card else col
                    }
                    wastePile = wastePile.drop(1)
                    selectedWasteCard = null
                }
            }
            selectedTableauIdx?.let { (srcCol, srcCardIdx) ->
                val moving = tableaus[srcCol].subList(srcCardIdx, tableaus[srcCol].size)
                if (moving.first().rank == "K") {
                    tableaus = tableaus.mapIndexed { idx, col ->
                        when (idx) {
                            tColIdx -> col + moving
                            srcCol -> col.subList(0, srcCardIdx)
                            else -> col
                        }
                    }
                    // Flip new top card
                    if (tableaus[srcCol].isNotEmpty()) {
                        tableaus[srcCol].last().isFaceUp = true
                    }
                    selectedTableauIdx = null
                }
            }
            return
        }

        val clickedCard = tPile[cardIdx]
        if (!clickedCard.isFaceUp) {
            if (cardIdx == tPile.size - 1) {
                val updated = tPile.toMutableList()
                updated[cardIdx] = clickedCard.copy(isFaceUp = true)
                tableaus = tableaus.mapIndexed { idx, col -> if (idx == tColIdx) updated else col }
            }
            return
        }

        // Select card for moving
        if (selectedWasteCard != null) {
            if (canMoveToTableau(selectedWasteCard!!, tColIdx)) {
                tableaus = tableaus.mapIndexed { idx, col ->
                    if (idx == tColIdx) col + selectedWasteCard!! else col
                }
                wastePile = wastePile.drop(1)
                selectedWasteCard = null
            }
        } else if (selectedTableauIdx != null) {
            val (srcCol, srcCardIdx) = selectedTableauIdx!!
            if (srcCol != tColIdx) {
                val moving = tableaus[srcCol].subList(srcCardIdx, tableaus[srcCol].size)
                if (canMoveToTableau(moving.first(), tColIdx)) {
                    tableaus = tableaus.mapIndexed { idx, col ->
                        when (idx) {
                            tColIdx -> col + moving
                            srcCol -> col.subList(0, srcCardIdx)
                            else -> col
                        }
                    }
                    if (tableaus[srcCol].isNotEmpty()) {
                        tableaus[srcCol].last().isFaceUp = true
                    }
                    selectedTableauIdx = null
                } else {
                    selectedTableauIdx = Pair(tColIdx, cardIdx)
                }
            } else {
                selectedTableauIdx = null
            }
        } else {
            selectedTableauIdx = Pair(tColIdx, cardIdx)
        }
    }

    fun onFoundationClick(fIdx: Int) {
        selectedWasteCard?.let { card ->
            if (canMoveToFoundation(card, fIdx)) {
                foundations = foundations.mapIndexed { idx, col -> if (idx == fIdx) col + card else col }
                wastePile = wastePile.drop(1)
                selectedWasteCard = null
                checkWinState()
            }
        }
        selectedTableauIdx?.let { (srcCol, srcCardIdx) ->
            val srcPile = tableaus[srcCol]
            if (srcCardIdx == srcPile.size - 1) {
                val card = srcPile.last()
                if (canMoveToFoundation(card, fIdx)) {
                    foundations = foundations.mapIndexed { idx, col -> if (idx == fIdx) col + card else col }
                    tableaus = tableaus.mapIndexed { idx, col -> if (idx == srcCol) col.dropLast(1) else col }
                    if (tableaus[srcCol].isNotEmpty()) {
                        tableaus[srcCol].last().isFaceUp = true
                    }
                    selectedTableauIdx = null
                    checkWinState()
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Klondike Solitaire Lite", fontSize = 15.sp, fontWeight = FontWeight.Bold)

                IconButton(
                    onClick = { initializeSolitaire() },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            // Pile layout row (Stock, Waste, 4 Foundations)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Stock + Waste pile
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Stock Card
                    Box(
                        modifier = Modifier
                            .size(width = 38.dp, height = 54.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (stockPile.isEmpty()) Color.LightGray.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.primary
                            )
                            .clickable { drawCard() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (stockPile.isNotEmpty()) {
                            Text("↺", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("∅", color = Color.Gray, fontSize = 14.sp)
                        }
                    }

                    // Waste Card
                    Box(
                        modifier = Modifier
                            .size(width = 38.dp, height = 54.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (selectedWasteCard != null) MaterialTheme.colorScheme.primaryContainer
                                else if (wastePile.isEmpty()) Color.LightGray.copy(alpha = 0.2f)
                                else Color.White
                            )
                            .border(
                                width = if (selectedWasteCard != null) 2.dp else 1.dp,
                                color = if (selectedWasteCard != null) MaterialTheme.colorScheme.primary else Color.LightGray,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onWasteCardClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (wastePile.isNotEmpty()) {
                            val card = wastePile.first()
                            Text(
                                text = "${card.rank}\n${card.suit}",
                                color = if (card.isRed) Color.Red else Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 11.sp
                            )
                        }
                    }
                }

                // Foundations row (Hearts, Diamonds, Clubs, Spades)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 0 until 4) {
                        val fPile = foundations[i]
                        Box(
                            modifier = Modifier
                                .size(width = 38.dp, height = 54.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                                .clickable { onFoundationClick(i) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (fPile.isEmpty()) {
                                Text(
                                    text = suits[i],
                                    color = if (i < 2) Color.Red.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                    fontSize = 16.sp
                                )
                            } else {
                                val top = fPile.last()
                                Text(
                                    text = "${top.rank}\n${top.suit}",
                                    color = if (top.isRed) Color.Red else Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Tableau columns row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (tIdx in 0 until 4) {
                    val tPile = tableaus[tIdx]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp)
                            .background(Color.LightGray.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                    ) {
                        if (tPile.isEmpty()) {
                            // Clickable empty column
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onTableauClick(tIdx, 0) },
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text("[K]", color = Color.Gray.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                tPile.forEachIndexed { cardIdx, card ->
                                    val isSelected = selectedTableauIdx == Pair(tIdx, cardIdx)
                                    val topOffset = (cardIdx * 16).dp

                                    CardRender(
                                        card = card,
                                        isSelected = isSelected,
                                        modifier = Modifier
                                            .offset(y = topOffset)
                                            .size(width = 38.dp, height = 54.dp)
                                            .clickable { onTableauClick(tIdx, cardIdx) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (winMessage) {
                Text(text = "CONGRATULATIONS! You Solved It! 🎉🏆", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            } else {
                Text(text = "Rules: Build Foundations A to K. Stack columns down alternating red/black.", fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun CardRender(
    card: CardModel,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (card.isFaceUp) Color.White else MaterialTheme.colorScheme.primary)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (card.isFaceUp) {
            Text(
                text = "${card.rank}\n${card.suit}",
                color = if (card.isRed) Color.Red else Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp
            )
        } else {
            // Card Back decoration
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
            )
        }
    }
}
