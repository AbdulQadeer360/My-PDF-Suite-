package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.PdfFile
import com.example.ui.PdfViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ToolsScreen(
    viewModel: PdfViewModel,
    navController: NavController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pdfFiles by viewModel.pdfFiles.collectAsState()

    // State indicators for active in-place overlay tools
    var activeDialogTool by remember { mutableStateOf<String?>(null) }
    
    // Status metrics
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("tools_root"),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("tools_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MY PDF SUITE",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "TOP PDF TOOLS",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "High-performance offline document utilities",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Category 1: FILE MERGE & EXTRACT
            item {
                SectionHeader(title = "STRUCTURE & PAGES")
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ToolOptionRow(
                        title = "1. Merge PDF",
                        subtitle = "Combine multiple PDFs into one file",
                        icon = Icons.Default.CallMerge,
                        color = Color(0xFF6366F1),
                        onClick = { activeDialogTool = "MERGE" }
                    )
                    ToolOptionRow(
                        title = "2. Split PDF",
                        subtitle = "Split PDF pages into separate files",
                        icon = Icons.Default.CallSplit,
                        color = Color(0xFFEC4899),
                        onClick = { activeDialogTool = "SPLIT" }
                    )
                    ToolOptionRow(
                        title = "12. Rearrange PDF Pages",
                        subtitle = "Drag and reorder PDF pages dynamically",
                        icon = Icons.Default.SwapVert,
                        color = Color(0xFFD97706),
                        onClick = { activeDialogTool = "REARRANGE" }
                    )
                    ToolOptionRow(
                        title = "13. Delete PDF Pages",
                        subtitle = "Remove unwanted pages from PDFs",
                        icon = Icons.Default.Delete,
                        color = Color(0xFFEF4444),
                        onClick = { activeDialogTool = "DELETE_PAGES" }
                    )
                }
            }

            // Category 2: CONVERSION ENGINE
            item {
                SectionHeader(title = "CONVERSIONS")
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ToolOptionRow(
                        title = "4. PDF to Word",
                        subtitle = "Convert PDF into editable Word document",
                        icon = Icons.Default.Description,
                        color = Color(0xFF3B82F6),
                        onClick = { activeDialogTool = "PDF_TO_WORD" }
                    )
                    ToolOptionRow(
                        title = "5. Word to PDF",
                        subtitle = "Convert Word files into PDF format",
                        icon = Icons.Default.PictureAsPdf,
                        color = Color(0xFF10B981),
                        onClick = { activeDialogTool = "WORD_TO_PDF" }
                    )
                    ToolOptionRow(
                        title = "6. JPG to PDF",
                        subtitle = "Convert images into PDF documents",
                        icon = Icons.Default.Image,
                        color = Color(0xFF059669),
                        onClick = { navController.navigate("convert_image") }
                    )
                    ToolOptionRow(
                        title = "7. PDF to JPG",
                        subtitle = "Convert PDF pages into image files",
                        icon = Icons.Default.Collections,
                        color = Color(0xFFF59E0B),
                        onClick = { activeDialogTool = "PDF_TO_JPG" }
                    )
                }
            }

            // Category 3: SCANNING, SIGNATURE & BRANDING
            item {
                SectionHeader(title = "SCANNING & LAYERS")
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ToolOptionRow(
                        title = "8. Scan Document",
                        subtitle = "Scan papers using mobile camera and save",
                        icon = Icons.Default.CameraAlt,
                        color = Color(0xFF06B6D4),
                        onClick = { activeDialogTool = "SCAN" }
                    )
                    ToolOptionRow(
                        title = "9. Add Signature",
                        subtitle = "Add hand-sketched digital signatures to PDFs",
                        icon = Icons.Default.Edit,
                        color = Color(0xFF6D28D9),
                        onClick = { activeDialogTool = "SIGNATURE" }
                    )
                    ToolOptionRow(
                        title = "14. Add Watermark",
                        subtitle = "Add text or image watermark to PDFs",
                        icon = Icons.Default.BrandingWatermark,
                        color = Color(0xFFE11D48),
                        onClick = { navController.navigate("watermark") }
                    )
                    ToolOptionRow(
                        title = "15. OCR Text Scanner",
                        subtitle = "Extract text from scanned images and PDFs",
                        icon = Icons.Default.TextSnippet,
                        color = Color(0xFF8B5CF6),
                        onClick = { navController.navigate("ocr") }
                    )
                }
            }

            // Category 4: UTILITY & CODES
            item {
                SectionHeader(title = "SECURITY & STORAGE COMPRESSION")
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ToolOptionRow(
                        title = "3. Compress PDF",
                        subtitle = "Reduce PDF file size while maintaining safety",
                        icon = Icons.Default.Compress,
                        color = Color(0xFFEC4899),
                        onClick = { navController.navigate("compress") }
                    )
                    ToolOptionRow(
                        title = "10. Lock PDF",
                        subtitle = "Add password protection to PDF files",
                        icon = Icons.Default.Lock,
                        color = Color(0xFF374151),
                        onClick = { navController.navigate("security") }
                    )
                    ToolOptionRow(
                        title = "11. Unlock PDF",
                        subtitle = "Remove encryption password from PDFs",
                        icon = Icons.Default.LockOpen,
                        color = Color(0xFF10B981),
                        onClick = { activeDialogTool = "UNLOCK" }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // -------------------------------------------------------------
    // OVERLAY INTERACTIVE MODAL DIALOGS FOR THE 10 UTILITIES:
    // -------------------------------------------------------------

    // 1. Merge PDF Dialog
    if (activeDialogTool == "MERGE") {
        val selectedFiles = remember { mutableStateListOf<PdfFile>() }
        var outputName by remember { mutableStateOf("Merged_Archive_" + System.currentTimeMillis() % 1000) }
        var isWorking by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("Merge Multiple PDFs", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select 2 or more files and provide an output name:", fontSize = 12.sp)
                    
                    OutlinedTextField(
                        value = outputName,
                        onValueChange = { outputName = it },
                        label = { Text("Output name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (pdfFiles.isEmpty()) {
                        Text(
                            "No files in sandbox memory. Convert some images or templates first!",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Text("Available Files in Vault:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                        Box(modifier = Modifier.height(130.dp).border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)).padding(4.dp)) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(pdfFiles) { file ->
                                    val checked = selectedFiles.contains(file)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (checked) selectedFiles.remove(file) else selectedFiles.add(file)
                                            }
                                            .padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = checked,
                                            onCheckedChange = {
                                                if (checked) selectedFiles.remove(file) else selectedFiles.add(file)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(file.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(file.size, fontSize = 9.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedFiles.size < 2) {
                            Toast.makeText(context, "Please select at least 2 database PDFs!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isWorking = true
                        viewModel.mergeMultiplePdfs(selectedFiles, outputName) { success ->
                            isWorking = false
                            activeDialogTool = null
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "Files successfully merged into $outputName.pdf!" else "Failed to merge Files."
                                )
                            }
                        }
                    },
                    enabled = !isWorking && selectedFiles.size >= 2
                ) {
                    if (isWorking) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    else Text("Merge Selected (${selectedFiles.size})")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }, enabled = !isWorking) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Split PDF Dialog
    if (activeDialogTool == "SPLIT") {
        var selectedFile by remember { mutableStateOf<PdfFile?>(null) }
        var targetPartName by remember { mutableStateOf("") }
        var pageNumToExtract by remember { mutableStateOf("1") }
        var isWorking by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("Split PDF Pages", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a file to extract page segment into a separate PDF:", fontSize = 12.sp)

                    if (pdfFiles.isEmpty()) {
                        Text("No files in database. Please generate or import PDFs first.", color = Color.Red, fontSize = 11.sp)
                    } else {
                        var expandedFileMenu by remember { mutableStateOf(false) }
                        Box {
                            Button(
                                onClick = { expandedFileMenu = true },
                                modifier = Modifier.fillMaxWidth().testTag("split_file_spinner"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(selectedFile?.name ?: "Select File...")
                            }
                            DropdownMenu(expanded = expandedFileMenu, onDismissRequest = { expandedFileMenu = false }) {
                                pdfFiles.forEach { file ->
                                    DropdownMenuItem(
                                        text = { Text(file.name) },
                                        onClick = {
                                            selectedFile = file
                                            targetPartName = "Split_Part1_" + file.name
                                            expandedFileMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (selectedFile != null) {
                        OutlinedTextField(
                            value = targetPartName,
                            onValueChange = { targetPartName = it },
                            label = { Text("Output PDF Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = pageNumToExtract,
                            onValueChange = { pageNumToExtract = it },
                            label = { Text("Page Index to extract") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val file = selectedFile
                        val pageIdx = pageNumToExtract.toIntOrNull() ?: 1
                        if (file == null || targetPartName.isEmpty()) {
                            Toast.makeText(context, "Input missing details!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isWorking = true
                        viewModel.splitPdfFile(file, pageIdx, targetPartName) { success ->
                            isWorking = false
                            activeDialogTool = null
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "Split generated successfully: $targetPartName" else "Failed to split PDF."
                                )
                            }
                        }
                    },
                    enabled = !isWorking && selectedFile != null && targetPartName.isNotEmpty()
                ) {
                    if (isWorking) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    else Text("Extract & Split")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }, enabled = !isWorking) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. PDF to Word Dialog
    if (activeDialogTool == "PDF_TO_WORD") {
        var selectedFile by remember { mutableStateOf<PdfFile?>(null) }
        var outWordName by remember { mutableStateOf("") }
        var isWorking by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("PDF to editable Word Document", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Convert static files into fully styled, layout-retained editable Word segments (.docx):", fontSize = 12.sp)

                    if (pdfFiles.isEmpty()) {
                        Text("Convert documents on the dashboard or template library first!", color = Color.Red, fontSize = 11.sp)
                    } else {
                        var expandedFileMenu by remember { mutableStateOf(false) }
                        Box {
                            Button(
                                onClick = { expandedFileMenu = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(selectedFile?.name ?: "Select source PDF...")
                            }
                            DropdownMenu(expanded = expandedFileMenu, onDismissRequest = { expandedFileMenu = false }) {
                                pdfFiles.forEach { file ->
                                    DropdownMenuItem(
                                        text = { Text(file.name) },
                                        onClick = {
                                            selectedFile = file
                                            outWordName = file.name.substringBeforeLast(".pdf") + "_Converted.docx"
                                            expandedFileMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (selectedFile != null) {
                        OutlinedTextField(
                            value = outWordName,
                            onValueChange = { outWordName = it },
                            label = { Text("Word document name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val file = selectedFile
                        if (file == null || outWordName.isEmpty()) return@Button
                        isWorking = true
                        viewModel.convertPdfToWordDocument(file, outWordName) { success ->
                            isWorking = false
                            activeDialogTool = null
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "Word document generated successfully inside device: $outWordName!" else "Conversion failure."
                                )
                            }
                        }
                    },
                    enabled = !isWorking && selectedFile != null && outWordName.isNotEmpty()
                ) {
                    if (isWorking) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    else Text("Convert to Word")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }, enabled = !isWorking) {
                    Text("Cancel")
                }
            }
        )
    }

    // 5. Word to PDF Dialog
    if (activeDialogTool == "WORD_TO_PDF") {
        var wordTextBody by remember { mutableStateOf("WORD FORMAT INVOICE REPORT\n\n- Client: Abdul Wajid\n- Task Segment: Design & Developed Suite\n- Pricing Tier: Enterprise Deluxe Pro\n\nThank you for choosing high velocity local sandbox rendering indices!") }
        var outFilename by remember { mutableStateOf("WordOutput_" + System.currentTimeMillis() % 1000 + ".pdf") }
        var isWorking by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("Word/Doc file to PDF", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Type word text content or composition templates to compile directly into an offline PDF:", fontSize = 12.sp)

                    OutlinedTextField(
                        value = outFilename,
                        onValueChange = { outFilename = it },
                        label = { Text("Target filename") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = wordTextBody,
                        onValueChange = { wordTextBody = it },
                        label = { Text("Word Text Content Body") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (wordTextBody.isEmpty() || outFilename.isEmpty()) return@Button
                        isWorking = true
                        viewModel.convertTextToPdf(outFilename, wordTextBody) { success ->
                            isWorking = false
                            activeDialogTool = null
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "Word file turned to PDF successfully: $outFilename" else "Generation failure"
                                )
                            }
                        }
                    },
                    enabled = !isWorking && wordTextBody.isNotEmpty() && outFilename.isNotEmpty()
                ) {
                    if (isWorking) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    else Text("Convert to PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }, enabled = !isWorking) {
                    Text("Cancel")
                }
            }
        )
    }

    // 7. PDF to JPG Dialog
    if (activeDialogTool == "PDF_TO_JPG") {
        var selectedFile by remember { mutableStateOf<PdfFile?>(null) }
        var outJpgName by remember { mutableStateOf("") }
        var isWorking by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("PDF to JPG Page Renderer", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Convert static files page segments into clear JPEG photo artifacts automatically saved to cache:", fontSize = 12.sp)

                    if (pdfFiles.isEmpty()) {
                        Text("No PDFs available.", color = Color.Red, fontSize = 11.sp)
                    } else {
                        var expandedFileMenu by remember { mutableStateOf(false) }
                        Box {
                            Button(
                                onClick = { expandedFileMenu = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(selectedFile?.name ?: "Select PDF...")
                            }
                            DropdownMenu(expanded = expandedFileMenu, onDismissRequest = { expandedFileMenu = false }) {
                                pdfFiles.forEach { file ->
                                    DropdownMenuItem(
                                        text = { Text(file.name) },
                                        onClick = {
                                            selectedFile = file
                                            outJpgName = file.name.substringBeforeLast(".pdf") + "_Page1.jpg"
                                            expandedFileMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (selectedFile != null) {
                        OutlinedTextField(
                            value = outJpgName,
                            onValueChange = { outJpgName = it },
                            label = { Text("Target Image Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val file = selectedFile
                        if (file == null || outJpgName.isEmpty()) return@Button
                        isWorking = true
                        viewModel.convertPdfPagesToJpg(file, outJpgName) { success ->
                            isWorking = false
                            activeDialogTool = null
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "JPEG image exported successfully: $outJpgName!" else "Export failure."
                                )
                            }
                        }
                    },
                    enabled = !isWorking && selectedFile != null && outJpgName.isNotEmpty()
                ) {
                    if (isWorking) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    else Text("Render Page to JPG")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }, enabled = !isWorking) {
                    Text("Cancel")
                }
            }
        )
    }

    // 8. Scan Document Dialog (Visual Viewfinder Simulator)
    if (activeDialogTool == "SCAN") {
        var scanStage by remember { mutableStateOf("VIEWFINDER") } // "VIEWFINDER", "PROCESS", "SAVE"
        var scannerTextBuffer by remember { mutableStateOf("SCANNED CORPORATE AGREEMENT\n\n1. PARTIES\nThis sandboxed statement binds development teams and Wajid designers.\n\n2. AUDITS\nVerification matches automatic checklists. Build structures compile on-demand.") }
        var targetScanPdfName by remember { mutableStateOf("CameraScan_" + System.currentTimeMillis() % 1000 + ".pdf") }
        var isWorking by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("AI Document Camera Scanner", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (scanStage == "VIEWFINDER") {
                        Text("Point camera at document boundary and click shutter tag:", fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Camera viewfinder styling
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.FilterCenterFocus,
                                    contentDescription = null,
                                    tint = Color.Green,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("A4 DOCUMENT DETECTED", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("Flash setting: OFF  |  Focus-lock: ACTIVE", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                            }
                            
                            // Simple visual layout crop indicator
                            Box(modifier = Modifier.fillMaxSize().padding(14.dp).border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp)))
                        }
                    } else if (scanStage == "PROCESS") {
                        Text("Processing character scan buffers with high density OCR layout grids...", fontSize = 12.sp)
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        // SAVE STAGE
                        Text("Scan Successful! Verify extracted letter layouts and save:", fontSize = 11.sp)
                        OutlinedTextField(
                            value = targetScanPdfName,
                            onValueChange = { targetScanPdfName = it },
                            label = { Text("Save filename") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = scannerTextBuffer,
                            onValueChange = { scannerTextBuffer = it },
                            label = { Text("Parsed content text") },
                            modifier = Modifier.fillMaxWidth().height(110.dp)
                        )
                    }
                }
            },
            confirmButton = {
                if (scanStage == "VIEWFINDER") {
                    Button(onClick = {
                        scope.launch {
                            scanStage = "PROCESS"
                            delay(1300)
                            scanStage = "SAVE"
                        }
                    }) {
                        Text("Shutter Snap")
                    }
                } else if (scanStage == "SAVE") {
                    Button(
                        onClick = {
                            isWorking = true
                            viewModel.performScannedDocPdf(targetScanPdfName, scannerTextBuffer) { success ->
                                isWorking = false
                                activeDialogTool = null
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (success) "Scanned document saved to vault storage: $targetScanPdfName" else "Save failed."
                                    )
                                }
                            }
                        },
                        enabled = !isWorking && targetScanPdfName.isNotEmpty()
                    ) {
                        if (isWorking) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                        else Text("Burn & Save PDF")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }, enabled = !isWorking) {
                    Text("Close")
                }
            }
        )
    }

    // 9. Add Signature Dialog (Touch Sketch-canvas Signature Overlay Generator)
    if (activeDialogTool == "SIGNATURE") {
        var selectedFile by remember { mutableStateOf<PdfFile?>(null) }
        var outFilename by remember { mutableStateOf("") }
        var isWorking by remember { mutableStateOf(false) }

        // Touch gesture paths representation
        val gesturePaths = remember { mutableStateListOf<List<androidx.compose.ui.geometry.Offset>>() }
        var currentStrokePath by remember { mutableStateOf<List<androidx.compose.ui.geometry.Offset>>(emptyList()) }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("Draw Finger Signature", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Draw your digital hand signature onto the pad and select target file:", fontSize = 11.sp)

                    // Sketch pad canvas box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            currentStrokePath = listOf(offset)
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            currentStrokePath = currentStrokePath + change.position
                                        },
                                        onDragEnd = {
                                            gesturePaths.add(currentStrokePath)
                                            currentStrokePath = emptyList()
                                        }
                                    )
                                }
                        ) {
                            gesturePaths.forEach { stroke ->
                                for (i in 0 until stroke.size - 1) {
                                    drawLine(
                                        color = Color(30, 21, 68),
                                        start = stroke[i],
                                        end = stroke[i + 1],
                                        strokeWidth = 6f
                                    )
                                }
                            }
                            if (currentStrokePath.size > 1) {
                                for (i in 0 until currentStrokePath.size - 1) {
                                    drawLine(
                                        color = Color(30, 21, 68),
                                        start = currentStrokePath[i],
                                        end = currentStrokePath[i + 1],
                                        strokeWidth = 6f
                                    )
                                }
                            }
                        }
                        
                        // Clear sketch badge
                        Text(
                            "Clear signature",
                            fontSize = 10.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clickable {
                                    gesturePaths.clear()
                                }
                        )
                    }

                    if (pdfFiles.isNotEmpty()) {
                        var expandedFileMenu by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.padding(top = 4.dp)) {
                            Button(
                                onClick = { expandedFileMenu = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(selectedFile?.name ?: "Select Target PDF...")
                            }
                            DropdownMenu(expanded = expandedFileMenu, onDismissRequest = { expandedFileMenu = false }) {
                                pdfFiles.forEach { file ->
                                    DropdownMenuItem(
                                        text = { Text(file.name) },
                                        onClick = {
                                            selectedFile = file
                                            outFilename = "Signed_" + file.name
                                            expandedFileMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text("No PDFs exist in database.", color = Color.Red, fontSize = 11.sp)
                    }

                    if (selectedFile != null) {
                        OutlinedTextField(
                            value = outFilename,
                            onValueChange = { outFilename = it },
                            label = { Text("Output PDF Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val file = selectedFile
                        if (file == null || outFilename.isEmpty()) return@Button
                        
                        // Transfer gesture paths coordinates to real Android OS bitmap canvas
                        val signatureBitmap = Bitmap.createBitmap(300, 150, Bitmap.Config.ARGB_8888)
                        val bCanvas = AndroidCanvas(signatureBitmap)
                        bCanvas.drawColor(android.graphics.Color.WHITE)
                        val paint = AndroidPaint().apply {
                            color = android.graphics.Color.BLUE
                            strokeWidth = 8f
                            style = AndroidPaint.Style.STROKE
                            isAntiAlias = true
                        }
                        
                        gesturePaths.forEach { path ->
                            if (path.isNotEmpty()) {
                                val aPath = AndroidPath()
                                aPath.moveTo(path[0].x * (300f / 350f), path[0].y * (150f / 140f))
                                for (i in 1 until path.size) {
                                    aPath.lineTo(path[i].x * (300f / 350f), path[i].y * (150f / 140f))
                                }
                                bCanvas.drawPath(aPath, paint)
                            }
                        }

                        isWorking = true
                        viewModel.signPdfFile(file, signatureBitmap, 500f, outFilename) { success ->
                            isWorking = false
                            activeDialogTool = null
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "Signature burned successfully: $outFilename" else "Signature fail"
                                )
                            }
                        }
                    },
                    enabled = !isWorking && selectedFile != null && gesturePaths.isNotEmpty() && outFilename.isNotEmpty()
                ) {
                    if (isWorking) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    else Text("Acknowledge & Sign PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }, enabled = !isWorking) {
                    Text("Cancel")
                }
            }
        )
    }

    // 11. Unlock PDF Dialog
    if (activeDialogTool == "UNLOCK") {
        var selectedFile by remember { mutableStateOf<PdfFile?>(null) }
        var typeCodePassword by remember { mutableStateOf("") }
        var isWorking by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("Unlock Password Protected PDF", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a securely locked document and type the password to clear decryption blocks:", fontSize = 12.sp)

                    val lockedFiles = pdfFiles.filter { it.isLocked }
                    if (lockedFiles.isEmpty()) {
                        Text("No locked files found. Lock a file first in the Security tool!", color = Color.Red, fontSize = 11.sp)
                    } else {
                        var expandedFileMenu by remember { mutableStateOf(false) }
                        Box {
                            Button(
                                onClick = { expandedFileMenu = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(selectedFile?.name ?: "Select Protected File...")
                            }
                            DropdownMenu(expanded = expandedFileMenu, onDismissRequest = { expandedFileMenu = false }) {
                                lockedFiles.forEach { file ->
                                    DropdownMenuItem(
                                        text = { Text(file.name) },
                                        onClick = {
                                            selectedFile = file
                                            expandedFileMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (selectedFile != null) {
                        OutlinedTextField(
                            value = typeCodePassword,
                            onValueChange = { typeCodePassword = it },
                            label = { Text("Encryption password code") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val file = selectedFile
                        if (file == null) return@Button
                        
                        // Validate password
                        if (file.password != typeCodePassword) {
                            Toast.makeText(context, "Incorrect security code!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        isWorking = true
                        viewModel.unlockFile(file)
                        scope.launch {
                            delay(600)
                            isWorking = false
                            activeDialogTool = null
                            snackbarHostState.showSnackbar("Password unlocked successfully. Encryption block removed.")
                        }
                    },
                    enabled = !isWorking && selectedFile != null && typeCodePassword.isNotEmpty()
                ) {
                    Text("Decrypt Outline")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 12. Rearrange PDF Pages Dialog
    if (activeDialogTool == "REARRANGE") {
        var selectedFile by remember { mutableStateOf<PdfFile?>(null) }
        var outFilename by remember { mutableStateOf("") }
        var isWorking by remember { mutableStateOf(false) }

        // Simulated pages ordering representation
        val pageOrderList = remember { mutableStateListOf<Int>() }

        AlertDialog(
            onDismissRequest = { if (!isWorking) activeDialogTool = null },
            title = { Text("Rearrange Page Slots", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Reorder layouts. Pick a PDF in database, adjust positions, save:", fontSize = 11.sp)

                    if (pdfFiles.isNotEmpty()) {
                        var expandedFileMenu by remember { mutableStateOf(false) }
                        Box {
                            Button(
                                onClick = { expandedFileMenu = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(selectedFile?.name ?: "Select PDF...")
                            }
                            DropdownMenu(expanded = expandedFileMenu, onDismissRequest = { expandedFileMenu = false }) {
                                pdfFiles.forEach { file ->
                                    DropdownMenuItem(
                                        text = { Text(file.name) },
                                        onClick = {
                                            selectedFile = file
                                            outFilename = "Reordered_" + file.name
                                            pageOrderList.clear()
                                            // Replicate sample layout page indices
                                            for (i in 1..4) { pageOrderList.add(i) }
                                            expandedFileMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text("No PDFs found.", color = Color.Red, fontSize = 11.sp)
                    }

                    if (selectedFile != null) {
                        OutlinedTextField(
                            value = outFilename,
                            onValueChange = { outFilename = it },
                            label = { Text("Output PDF Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text("Adjust Order Sequence:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Box(modifier = Modifier.height(115.dp).border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)).padding(2.dp)) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                items(pageOrderList.toList()) { pageNum ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Page Slot: original #$pageNum", fontSize = 12.sp)
                                        Row {
                                            Text(
                                                "Up",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp).clickable {
                                                    val idx = pageOrderList.indexOf(pageNum)
                                                    if (idx > 0) {
                                                        pageOrderList.removeAt(idx)
                                                        pageOrderList.add(idx - 1, pageNum)
                                                    }
                                                }
                                            )
                                            Text(
                                                "Down",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp).clickable {
                                                    val idx = pageOrderList.indexOf(pageNum)
                                                    if (idx < pageOrderList.size - 1) {
                                                        pageOrderList.removeAt(idx)
                                                        pageOrderList.add(idx + 1, pageNum)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val file = selectedFile
                        if (file == null || outFilename.isEmpty()) return@Button
                        isWorking = true
                        viewModel.rearrangePdfFilePages(file, pageOrderList.toList(), outFilename) { success ->
                            isWorking = false
                            activeDialogTool = null
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "Pages rearranged successfully: $outFilename" else "Action failed."
                                )
                            }
                        }
                    },
                    enabled = !isWorking && selectedFile != null && outFilename.isNotEmpty()
                ) {
                    Text("Reorder & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogTool = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 13. Delete PDF Pages Dialog
    if (activeDialogTool == "DELETE_PAGES") {
         var selectedFile by remember { mutableStateOf<PdfFile?>(null) }
         var outFilename by remember { mutableStateOf("") }
         var isWorking by remember { mutableStateOf(false) }

         // Pages checklists
         val retainedPages = remember { mutableStateListOf<Int>() }

         AlertDialog(
             onDismissRequest = { if (!isWorking) activeDialogTool = null },
             title = { Text("Exclude & Delete PDF Pages", fontWeight = FontWeight.Bold) },
             text = {
                 Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                     Text("Exclude pages from file of original document:", fontSize = 12.sp)

                     if (pdfFiles.isNotEmpty()) {
                         var expandedFileMenu by remember { mutableStateOf(false) }
                         Box {
                             Button(
                                 onClick = { expandedFileMenu = true },
                                 modifier = Modifier.fillMaxWidth(),
                                 colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), contentColor = MaterialTheme.colorScheme.primary)
                             ) {
                                 Text(selectedFile?.name ?: "Select PDF...")
                             }
                             DropdownMenu(expanded = expandedFileMenu, onDismissRequest = { expandedFileMenu = false }) {
                                 pdfFiles.forEach { file ->
                                     DropdownMenuItem(
                                         text = { Text(file.name) },
                                         onClick = {
                                             selectedFile = file
                                             outFilename = "Trimmed_" + file.name
                                             retainedPages.clear()
                                             for (i in 1..4) { retainedPages.add(i) }
                                             expandedFileMenu = false
                                         }
                                     )
                                 }
                             }
                         }
                     } else {
                         Text("No PDFs found.", color = Color.Red, fontSize = 11.sp)
                     }

                     if (selectedFile != null) {
                         OutlinedTextField(
                             value = outFilename,
                             onValueChange = { outFilename = it },
                             label = { Text("Output PDF Name") },
                             modifier = Modifier.fillMaxWidth(),
                             singleLine = true
                         )

                         Text("Select Pages to KEEP:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                         Box(modifier = Modifier.height(115.dp).border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)).padding(2.dp)) {
                             LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                 items(listOf(1, 2, 3, 4)) { pageNum ->
                                     val isKept = retainedPages.contains(pageNum)
                                     Row(
                                         modifier = Modifier
                                             .fillMaxWidth()
                                             .clickable {
                                                 if (isKept) retainedPages.remove(pageNum) else retainedPages.add(pageNum)
                                             }
                                             .padding(4.dp),
                                         verticalAlignment = Alignment.CenterVertically
                                     ) {
                                         Checkbox(
                                             checked = isKept,
                                             onCheckedChange = {
                                                 if (isKept) retainedPages.remove(pageNum) else retainedPages.add(pageNum)
                                             }
                                         )
                                         Spacer(modifier = Modifier.width(6.dp))
                                         Text("Page #$pageNum", fontSize = 12.sp)
                                     }
                                 }
                             }
                         }
                     }
                 }
             },
             confirmButton = {
                 Button(
                     onClick = {
                         val file = selectedFile
                         if (file == null || outFilename.isEmpty()) return@Button
                         if (retainedPages.isEmpty()) {
                             Toast.makeText(context, "Please select at least 1 page to keep!", Toast.LENGTH_SHORT).show()
                             return@Button
                         }
                         isWorking = true
                         viewModel.deletePdfFilePages(file, retainedPages.toList(), outFilename) { success ->
                             isWorking = false
                             activeDialogTool = null
                             scope.launch {
                                 snackbarHostState.showSnackbar(
                                     if (success) "Trimming complete! Pages removed successfully." else "Exclusion failure."
                                 )
                             }
                         }
                     },
                     enabled = !isWorking && selectedFile != null && outFilename.isNotEmpty() && retainedPages.isNotEmpty()
                 ) {
                     Text("Exclude & Trim PDF")
                 }
             },
             dismissButton = {
                 TextButton(onClick = { activeDialogTool = null }) {
                     Text("Cancel")
                 }
             }
         )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.5.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun ToolOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tool_row_$title"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
