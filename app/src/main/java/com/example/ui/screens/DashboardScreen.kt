package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.data.PdfFile
import com.example.ui.PdfViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: PdfViewModel,
    navController: NavController,
    onNavigateToTools: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToOcr: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val userEmail by viewModel.currentUser.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val pdfFiles by viewModel.pdfFiles.collectAsState()
    val favoriteFiles by viewModel.favoriteFiles.collectAsState()

    var activeTab by remember { mutableStateOf("home") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("dashboard_root"),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp)
                    .padding(top = 28.dp, bottom = 12.dp)
            ) {
                // Mockup inspired layout: Beautiful branding title + avatar profile
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MY PDF ",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "SUITE",
                                color = BluePrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                letterSpacing = (-0.5).sp
                            )
                        }
                        Text(
                            text = "Wednesday, Oct 24", // The dynamic style from Professional Polish Mockup
                            color = Color(0xFF64748B), // Slate-500
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Dynamic Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BluePrimary.copy(alpha = 0.12f))
                            .clickable { onNavigateToSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                // User salutation greeting row below
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Hello, $userName 👋",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (userEmail == "GUEST") "Offline sandbox mode" else userEmail ?: "",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(180.dp)
                        )
                    }

                    // Premium dynamic badge
                    Card(
                        onClick = { onNavigateToPremium() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPremium) EmeraldTertiary.copy(alpha = 0.12f) else BluePrimary.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("premium_dashboard_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isPremium) EmeraldTertiary else BluePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPremium) "PRO ACTIVE" else "UPGRADE",
                                color = if (isPremium) EmeraldTertiary else BluePrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == "home",
                    onClick = { activeTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = BluePrimary.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "tools",
                    onClick = {
                        activeTab = "tools"
                        onNavigateToTools()
                    },
                    icon = { Icon(Icons.Default.Category, contentDescription = "Tools") },
                    label = { Text("Tools") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = BluePrimary.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "ocr",
                    onClick = {
                        activeTab = "ocr"
                        onNavigateToOcr()
                    },
                    icon = { Icon(Icons.Default.Build, contentDescription = "OCR") },
                    label = { Text("OCR Scanner") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = BluePrimary.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "settings",
                    onClick = {
                        activeTab = "settings"
                        onNavigateToSettings()
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = BluePrimary.copy(alpha = 0.12f)
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Elegant Search Engine Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Search local conversion, edits or text...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear research query",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("dashboard_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Feature Stat Block Banner
                if (searchQuery.isEmpty()) {
                    // Promotional gradient banner card matching mockup exactly
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToAi() },
                            shape = RoundedCornerShape(26.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(BluePrimary, BlueSecondary)
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White.copy(alpha = 0.2f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "NEW FEATURE",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                letterSpacing = 1.sp
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF34D399)))
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF60A5FA)))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Summarize your PDFs\nwith AI Insights",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp,
                                        lineHeight = 24.sp
                                    )

                                    Text(
                                        text = "Save hours of reading with smart summaries.",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                    )

                                    Button(
                                        onClick = { onNavigateToAi() },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "Try AI Beta",
                                            color = BluePrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                BluePrimary.copy(alpha = 0.04f),
                                                EmeraldTertiary.copy(alpha = 0.02f)
                                            )
                                        )
                                    )
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Document Cloud Vault",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "${pdfFiles.size} Compiled items saved locally",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    Button(
                                        onClick = { onNavigateToTools() },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                                    ) {
                                        Text("Convert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Tool categories scrollable shortcuts row
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quick Tool Actions",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "See All",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = BluePrimary,
                                    modifier = Modifier.clickable { onNavigateToTools() }
                                )
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(bottom = 6.dp)
                            ) {
                                item {
                                    ToolShortcutTile(
                                        title = "Text to PDF",
                                        subtitle = "Compose & render",
                                        icon = Icons.Default.TextSnippet,
                                        color = BluePrimary,
                                        onClick = { navController.navigate("convert_text") }
                                    )
                                }
                                item {
                                    ToolShortcutTile(
                                        title = "Image to PDF",
                                        subtitle = "Capture & convert",
                                        icon = Icons.Default.PictureAsPdf,
                                        color = Color(0xFFEF4444), // Rose/Red matching design mockup
                                        onClick = { navController.navigate("convert_image") }
                                    )
                                }
                                item {
                                    ToolShortcutTile(
                                        title = "OCR Scanner", // exact tool title & color matching from design
                                        subtitle = "Extract text live",
                                        icon = Icons.Default.Build,
                                        color = Color(0xFF6366F1), // Indigo/Blue-Purple matching mockup
                                        onClick = { onNavigateToOcr() }
                                    )
                                }
                                item {
                                    ToolShortcutTile(
                                        title = "Protect PDF",
                                        subtitle = "Secure encryption",
                                        icon = Icons.Default.Security,
                                        color = Color(0xFFD97706), // Amber matching mockup
                                        onClick = { navController.navigate("security") }
                                    )
                                }
                                item {
                                    ToolShortcutTile(
                                        title = "Annotations",
                                        subtitle = "Draw & Notes",
                                        icon = Icons.Default.Build,
                                        color = Color(0xFFEF4444),
                                        onClick = { navController.navigate("annotations") }
                                    )
                                }
                                item {
                                    ToolShortcutTile(
                                        title = "Templates",
                                        subtitle = "Resume & Invoice",
                                        icon = Icons.Default.TextSnippet,
                                        color = Color(0xFF10B981),
                                        onClick = { navController.navigate("templates") }
                                    )
                                }
                                item {
                                    ToolShortcutTile(
                                        title = "Collab Sync",
                                        subtitle = "Live Segment Sim",
                                        icon = Icons.Default.Category,
                                        color = Color(0xFF8B5CF6),
                                        onClick = { navController.navigate("collaboration") }
                                    )
                                }
                            }
                        }
                    }
                }

                // File filtration Category select row
                item {
                    Column {
                        Text(
                            text = "Managed Files Directory",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("All", "Favorite", "CONVERTED", "SCANNED").forEach { cat ->
                                val isSelected = selectedCategoryFilter == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) BluePrimary else MaterialTheme.colorScheme.surface)
                                        .clickable { selectedCategoryFilter = cat }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // File list rendering
                val filteredList = pdfFiles.filter { file ->
                    when (selectedCategoryFilter) {
                        "All" -> true
                        "Favorite" -> file.isFavorite
                        else -> file.category == selectedCategoryFilter
                    }
                }

                if (filteredList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No catalogued files in this segment.",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Tap quick text converter to generate your first PDF report!",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 4.dp)
                            )
                        }
                    }
                } else {
                    items(filteredList) { pdf ->
                        DashboardFileItemRow(
                            pdfFile = pdf,
                            isPremium = isPremium,
                            viewModel = viewModel,
                            navController = navController,
                            onShare = {
                                try {
                                    val sendFile = File(pdf.path)
                                    if (sendFile.exists()) {
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            sendFile
                                        )
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share Document"))
                                    } else {
                                        coroutineScope.launch { snackbarHostState.showSnackbar("File source has been removed.") }
                                    }
                                } catch (e: Exception) {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("No compatible share client identified.") }
                                }
                            },
                            onDelete = {
                                viewModel.deleteFile(pdf)
                                coroutineScope.launch { snackbarHostState.showSnackbar("Document successfully removed from cache.") }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToolShortcutTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(width = 135.dp, height = 110.dp)
            .testTag("tool_shortcut_tile_$title"),
        shape = RoundedCornerShape(24.dp), // modern 24dp curves matching rounded-3xl from mockup
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardFileItemRow(
    pdfFile: PdfFile,
    isPremium: Boolean,
    viewModel: PdfViewModel,
    navController: NavController,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pdf_item_${pdfFile.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // PDF file icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (pdfFile.isLocked) Color(0xFFEF4444).copy(alpha = 0.1f)
                        else BluePrimary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (pdfFile.isLocked) Icons.Default.Lock else Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = if (pdfFile.isLocked) Color(0xFFEF4444) else BluePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pdfFile.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (pdfFile.isLocked) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted key layer enabled",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pdfFile.size,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = pdfFile.category,
                        color = if (pdfFile.category == "SCANNED") EmeraldTertiary else BluePrimary.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Interactive Stars
            IconButton(
                onClick = { viewModel.toggleFavorite(pdfFile) },
                modifier = Modifier.testTag("star_pdf_btn_${pdfFile.id}")
            ) {
                Icon(
                    imageVector = if (pdfFile.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite star icon",
                    tint = if (pdfFile.isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
            }

            // Options drop actions
            Box {
                IconButton(
                    onClick = { expandedMenu = true },
                    modifier = Modifier.testTag("menu_pdf_btn_${pdfFile.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Context options",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                DropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("AI Discuss Documents", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF8B5CF6)
                            )
                        },
                        onClick = {
                            expandedMenu = false
                            viewModel.selectFile(pdfFile)
                            navController.navigate("ai")
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Watermark Layers", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = OrangeWatermark
                            )
                        },
                        onClick = {
                            expandedMenu = false
                            viewModel.selectFile(pdfFile)
                            navController.navigate("watermark")
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Compress Resource", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Compress,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            expandedMenu = false
                            viewModel.selectFile(pdfFile)
                            navController.navigate("compress")
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Encrypt Password", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            expandedMenu = false
                            viewModel.selectFile(pdfFile)
                            navController.navigate("security")
                        }
                    )

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text("Share PDF File", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            expandedMenu = false
                            onShare()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Remove from List", fontSize = 13.sp, color = Color.Red) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Red
                            )
                        },
                        onClick = {
                            expandedMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

// Local palette aliases defined in theme

