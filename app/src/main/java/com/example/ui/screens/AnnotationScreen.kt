package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PdfFile
import com.example.ui.PdfViewModel
import com.example.utils.PdfEngine
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnotationScreen(
    viewModel: PdfViewModel,
    onBack: () -> Unit
) {
    val pdfFiles by viewModel.pdfFiles.collectAsState()
    var selectedFile by remember { mutableStateOf<PdfFile?>(null) }
    
    // Annotation options
    var activeTool by remember { mutableStateOf("STICKY") } // STICKY, RECTANGLE, CIRCLE, ARROW, UNDERLINE, STRIKETHROUGH
    var annotationText by remember { mutableStateOf("Quick Memo Notes") }
    var activeColorHex by remember { mutableStateOf("#EF4444") } // Default Red
    
    // Sliders for placement
    var posX by remember { mutableFloatStateOf(80f) }
    var posY by remember { mutableFloatStateOf(160f) }
    var sizeX by remember { mutableFloatStateOf(120f) }
    var sizeY by remember { mutableFloatStateOf(60f) }

    // List of pending annotations to burn in
    val pendingAnnotations = remember { mutableStateListOf<PdfEngine.PdfAnnotation>() }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("annotation_root"),
        topBar = {
            TopAppBar(
                title = { Text("Advanced PDF Annotations", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("annotation_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Dropdown to select physical PDF
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. CHOOSE WORKSPACE DOCUMENT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    if (pdfFiles.isEmpty()) {
                        Text(
                            text = "No PDF files available. Generate one first using text conversion or templates!",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    } else {
                        // Dropdown selection (simplified horizontal scroll preview)
                        Text(
                            text = "Selected file: ${selectedFile?.name ?: "Tap a document below to select"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedFile != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            pdfFiles.take(4).forEach { file ->
                                Card(
                                    onClick = { 
                                        selectedFile = file
                                        pendingAnnotations.clear()
                                    },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedFile?.id == file.id) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (selectedFile?.id == file.id) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize().padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = file.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedFile != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // PDF Document interactive preview block
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "LIVE ANNOTATION PREVIEW CANVAS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "A4 Page (Simulated 595x400)",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Beautiful Interactive Canvas representation
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        .clickable { 
                                            // Quick tap to move coordinates
                                            posX = 140f
                                            posY = 80f
                                        }
                                ) {
                                    // Simulated document lines
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text("WELCOME TO MY PDF SUITE - EDITABLE WORKSPACE", fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("This sandbox reproduces your current page outline. Add sticky note mementos, underline strings, target elements with arrows, or highlight shapes.", fontSize = 9.sp, color = Color.Gray, lineHeight = 12.sp)
                                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))
                                        Text("Document analysis is processed inside device file configurations securely. Press 'Save PDF' below to record these overlays permanently into the core file bytes.", fontSize = 9.sp, color = Color.Gray, lineHeight = 12.sp)
                                    }

                                    // Render added annotations on top of canvas
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        pendingAnnotations.forEach { ann ->
                                            val c = try { Color(android.graphics.Color.parseColor(ann.colorHex)) } catch (e: Exception) { Color.Red }
                                            
                                            when (ann.type.uppercase(Locale.US)) {
                                                "STICKY" -> {
                                                    drawRect(
                                                        color = Color(0xFFFEF08A), // pale yellow sticky notes background
                                                        topLeft = Offset(ann.x / 2.5f, ann.y / 2.5f),
                                                        size = Size(ann.sizeX / 2.2f, ann.sizeY / 2.2f)
                                                    )
                                                    drawRect(
                                                        color = Color(0xFFEAB308), // gold border
                                                        topLeft = Offset(ann.x / 2.5f, ann.y / 2.5f),
                                                        size = Size(ann.sizeX / 2.2f, ann.sizeY / 2.2f),
                                                        style = Stroke(width = 2f)
                                                    )
                                                    drawCircle(
                                                        color = c,
                                                        radius = 4f,
                                                        center = Offset(ann.x / 2.5f + 8f, ann.y / 2.5f + 8f)
                                                    )
                                                }
                                                "RECTANGLE" -> {
                                                    drawRect(
                                                        color = c,
                                                        topLeft = Offset(ann.x / 2.5f, ann.y / 2.5f),
                                                        size = Size(ann.sizeX / 2.2f, ann.sizeY / 2.2f),
                                                        style = Stroke(width = 3.5f)
                                                    )
                                                }
                                                "CIRCLE" -> {
                                                    drawOval(
                                                        color = c,
                                                        topLeft = Offset(ann.x / 2.5f, ann.y / 2.5f),
                                                        size = Size(ann.sizeX / 2.2f, ann.sizeY / 2.2f),
                                                        style = Stroke(width = 3.5f)
                                                    )
                                                }
                                                "ARROW" -> {
                                                    // Draw line representing arrow
                                                    drawLine(
                                                        color = c,
                                                        start = Offset(ann.x / 2.5f, ann.y / 2.5f),
                                                        end = Offset((ann.x + ann.sizeX) / 2.5f, (ann.y + ann.sizeY) / 2.5f),
                                                        strokeWidth = 4f
                                                    )
                                                    drawCircle(
                                                        color = c,
                                                        radius = 7f,
                                                        center = Offset((ann.x + ann.sizeX) / 2.5f, (ann.y + ann.sizeY) / 2.5f)
                                                    )
                                                }
                                                "UNDERLINE" -> {
                                                    drawLine(
                                                        color = c,
                                                        start = Offset(ann.x / 2.5f, (ann.y + ann.sizeY) / 2.5f),
                                                        end = Offset((ann.x + ann.sizeX) / 2.5f, (ann.y + ann.sizeY) / 2.5f),
                                                        strokeWidth = 3f
                                                    )
                                                }
                                                "STRIKETHROUGH" -> {
                                                    drawLine(
                                                        color = c,
                                                        start = Offset(ann.x / 2.5f, (ann.y + ann.sizeY / 2) / 2.5f),
                                                        end = Offset((ann.x + ann.sizeX) / 2.5f, (ann.y + ann.sizeY / 2) / 2.5f),
                                                        strokeWidth = 3f
                                                    )
                                                }
                                            }
                                        }

                                        // Render ACTIVE annotation placement marker helper
                                        val activeC = try { Color(android.graphics.Color.parseColor(activeColorHex)) } catch (e: Exception) { Color.Red }
                                        drawRect(
                                            color = activeC.copy(alpha = 0.25f),
                                            topLeft = Offset(posX / 2.5f, posY / 2.5f),
                                            size = Size(sizeX / 2.2f, sizeY / 2.2f)
                                        )
                                        drawRect(
                                            color = activeC,
                                            topLeft = Offset(posX / 2.5f, posY / 2.5f),
                                            size = Size(sizeX / 2.2f, sizeY / 2.2f),
                                            style = Stroke(width = 1.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Properties & Controls toolbox
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("2. CONFIGURE TOOL VALUE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                // TOOL TYPES ROws
                                Text("Choose Annotation Tool Type:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("STICKY", "RECTANGLE", "CIRCLE").forEach { tool ->
                                        Button(
                                            onClick = { activeTool = tool },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (activeTool == tool) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = tool,
                                                fontSize = 10.sp,
                                                color = if (activeTool == tool) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("ARROW", "UNDERLINE", "STRIKETHROUGH").forEach { tool ->
                                        Button(
                                            onClick = { activeTool = tool },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (activeTool == tool) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = tool,
                                                fontSize = 9.sp,
                                                color = if (activeTool == tool) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // COLOR SELECTOR ROW
                                Text("Fill Stroke Color Hex:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val colors = listOf("#EF4444" to "Red", "#3B82F6" to "Blue", "#10B981" to "Green", "#F59E0B" to "Amber")
                                    colors.forEach { (hex, lbl) ->
                                        val displayC = Color(android.graphics.Color.parseColor(hex))
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (activeColorHex == hex) displayC.copy(alpha = 0.15f) else Color.Transparent)
                                                .clickable { activeColorHex = hex }
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(displayC))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(lbl, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (activeTool == "STICKY") {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    OutlinedTextField(
                                        value = annotationText,
                                        onValueChange = { annotationText = it },
                                        label = { Text("Note content / tag") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // COORDINATES PRECISE SLIDERS
                                Text("Adjust Position Bounds on Page:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Column {
                                    Text("Horizontal Offset (X): ${posX.toInt()} dp", fontSize = 10.sp)
                                    Slider(value = posX, onValueChange = { posX = it }, valueRange = 10f..450f)
                                    
                                    Text("Vertical Offset (Y): ${posY.toInt()} dp", fontSize = 10.sp)
                                    Slider(value = posY, onValueChange = { posY = it }, valueRange = 10f..450f)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Scale Width: ${sizeX.toInt()} dp", fontSize = 10.sp)
                                            Slider(value = sizeX, onValueChange = { sizeX = it }, valueRange = 20f..300f)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Scale Height: ${sizeY.toInt()} dp", fontSize = 10.sp)
                                            Slider(value = sizeY, onValueChange = { sizeY = it }, valueRange = 15f..200f)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Button to ADD to Page
                                Button(
                                    onClick = {
                                        pendingAnnotations.add(
                                            PdfEngine.PdfAnnotation(
                                                type = activeTool,
                                                x = posX,
                                                y = posY,
                                                sizeX = sizeX,
                                                sizeY = sizeY,
                                                text = if (activeTool == "STICKY") annotationText else "",
                                                colorHex = activeColorHex
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                                ) {
                                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Stage Annotation on Preview", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Pending list of elements which will be permanently recorded
                    if (pendingAnnotations.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "STAGED FOR BAKING (${pendingAnnotations.size})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        TextButton(onClick = { pendingAnnotations.clear() }) {
                                            Text("Clear All", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))

                                    pendingAnnotations.forEachIndexed { index, item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(android.graphics.Color.parseColor(item.colorHex)))
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "${index + 1}. ${item.type} [x:${item.x.toInt()},y:${item.y.toInt()}]",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            IconButton(
                                                onClick = { pendingAnnotations.removeAt(index) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                                    }
                                }
                            }
                        }

                        // SAVE BUTTON IN SCREEN
                        item {
                            Button(
                                onClick = {
                                    val targetPdf = selectedFile
                                    if (targetPdf != null) {
                                        showProgress = true
                                        viewModel.saveAnnotatedPdf(
                                            outputName = "Annotated_${targetPdf.name}",
                                            originalFile = targetPdf,
                                            annotations = pendingAnnotations.toList(),
                                            onFinished = { success ->
                                                showProgress = false
                                                if (success) {
                                                    showSuccessDialog = true
                                                    pendingAnnotations.clear()
                                                }
                                            }
                                        )
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Please select a PDF file first.")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("save_annotations_btn"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Bakely Save Annotations Into PDF File", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            } else {
                // Select placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Document Active",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Please select or highlight an existing PDF from the workspace selection panel at the top to display drawing palettes.",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("Superb")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Annotations Saved!")
                }
            },
            text = {
                Text("Your sticky notes, rectangles, and arrows are permanently compiled inside the target PDF document. File uploaded to local database.")
            }
        )
    }

    if (showProgress) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text("Saving permanent PDF structures...", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
