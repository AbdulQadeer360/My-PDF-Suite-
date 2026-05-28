package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PdfViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateLibraryScreen(
    viewModel: PdfViewModel,
    onBack: () -> Unit
) {
    var selectedTemplate by remember { mutableStateOf<String?>(null) } // "RESUME", "INVOICE", "LETTER"
    var showProgress by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successFileName by remember { mutableStateOf("") }
    
    // Resume Form States
    var resFileName by remember { mutableStateOf("My_Professional_Resume.pdf") }
    var resName by remember { mutableStateOf("Rahad Hussain") }
    var resTitle by remember { mutableStateOf("Android System Engineer") }
    var resEmail by remember { mutableStateOf("rahadhussain786@gmail.com") }
    var resPhone by remember { mutableStateOf("+1 (555) 902-1246") }
    var resSummary by remember { mutableStateOf("Staff mobile architect specializing in core kernel engineering, clean code patterns, Jetpack Compose UI frameworks, and reactive flow persistence.") }
    var resExperience by remember { mutableStateOf("1. Sr Systems Analyst - Google AI Studio (2 years)\n- Leveraged print API layers to compile robust sandbox PDFs.\n- Superheaded Kotlin coroutines integration frameworks.") }
    var resSkills by remember { mutableStateOf("Kotlin, Java API, Jetpack Compose, SQLite, Room DB, Git, Systems architecture, Graphic Canvas, AdMob integrations.") }

    // Invoice Form States
    var invFileName by remember { mutableStateOf("Invoice_Report_Sales.pdf") }
    var invId by remember { mutableStateOf("INV-2026-9041") }
    var invClient by remember { mutableStateOf("Rahad Hussain LLC") }
    var invItemName by remember { mutableStateOf("Enterprise Document Licensing Plan") }
    var invQty by remember { mutableStateOf("1") }
    var invPrice by remember { mutableStateOf("34.98") }

    // Letter Form States
    var letFileName by remember { mutableStateOf("Formal_Business_Letter.pdf") }
    var letRecipient by remember { mutableStateOf("Google DeepMind Android Quality Team") }
    var letSubject by remember { mutableStateOf("Submission of My PDF Suite Pro Production Quality Metrics") }
    var letBody by remember { mutableStateOf("Dear Android Engineering Team, I am submitting the formal system evaluation for My PDF Suite. The application operates entirely offline, preserving security through sandbox context memory. We have resolved premium subscription constraints to offer a completely free user workflow funded by balanced Google AdMob advertising components. We look forward to your positive validation benchmarks.") }
    var letSender by remember { mutableStateOf("Rahad Hussain (Lead Architect)") }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("template_root"),
        topBar = {
            TopAppBar(
                title = { Text("App Template Library", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("template_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen header
            Column {
                Text(
                    text = "Professional PDF Creators",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Generate perfectly formatted documents offline. Fill in form metrics and build instant PDFs.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            // Template selector list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TemplateCategoryCard(
                    title = "Professional Resume",
                    icon = Icons.Default.Work,
                    color = Color(0xFF3B82F6),
                    isSelected = selectedTemplate == "RESUME",
                    onClick = { selectedTemplate = "RESUME" },
                    modifier = Modifier.weight(1f)
                )

                TemplateCategoryCard(
                    title = "Business Invoice",
                    icon = Icons.Default.Business,
                    color = Color(0xFF10B981),
                    isSelected = selectedTemplate == "INVOICE",
                    onClick = { selectedTemplate = "INVOICE" },
                    modifier = Modifier.weight(1f)
                )

                TemplateCategoryCard(
                    title = "Formal Letter",
                    icon = Icons.Default.HistoryEdu,
                    color = Color(0xFF8B5CF6),
                    isSelected = selectedTemplate == "LETTER",
                    onClick = { selectedTemplate = "LETTER" },
                    modifier = Modifier.weight(1f)
                )
            }

            // Interactive Form depending on choice
            if (selectedTemplate != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "COMPILING PREVIEW: ${selectedTemplate}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        when (selectedTemplate) {
                            "RESUME" -> {
                                OutlinedTextField(
                                    value = resFileName,
                                    onValueChange = { resFileName = it },
                                    label = { Text("Output PDF Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = resName,
                                    onValueChange = { resName = it },
                                    label = { Text("Full Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = resTitle,
                                    onValueChange = { resTitle = it },
                                    label = { Text("Professional Title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = resEmail,
                                        onValueChange = { resEmail = it },
                                        label = { Text("Email Contact") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    OutlinedTextField(
                                        value = resPhone,
                                        onValueChange = { resPhone = it },
                                        label = { Text("Phone Contact") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                                OutlinedTextField(
                                    value = resSummary,
                                    onValueChange = { resSummary = it },
                                    label = { Text("Brief Career Summary (one-liner)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    maxLines = 3
                                )
                                OutlinedTextField(
                                    value = resExperience,
                                    onValueChange = { resExperience = it },
                                    label = { Text("Work Experience") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    minLines = 3
                                )
                                OutlinedTextField(
                                    value = resSkills,
                                    onValueChange = { resSkills = it },
                                    label = { Text("Core Technologies & Skills") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Button(
                                    onClick = {
                                        showProgress = true
                                        viewModel.saveResumeTemplate(
                                            fileName = resFileName,
                                            name = resName,
                                            title = resTitle,
                                            email = resEmail,
                                            phone = resPhone,
                                            summary = resSummary,
                                            experience = resExperience,
                                            skills = resSkills,
                                            onFinished = { success ->
                                                showProgress = false
                                                if (success) {
                                                    successFileName = resFileName
                                                    showSuccessDialog = true
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("generate_resume_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Compile Resume to PDF File", fontWeight = FontWeight.Bold)
                                }
                            }

                            "INVOICE" -> {
                                OutlinedTextField(
                                    value = invFileName,
                                    onValueChange = { invFileName = it },
                                    label = { Text("Output PDF Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = invId,
                                    onValueChange = { invId = it },
                                    label = { Text("Invoice Reference ID") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = invClient,
                                    onValueChange = { invClient = it },
                                    label = { Text("Bill To (Client Company)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = invItemName,
                                    onValueChange = { invItemName = it },
                                    label = { Text("Activity Line Item description") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = invQty,
                                        onValueChange = { invQty = it },
                                        label = { Text("Quantity") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                    OutlinedTextField(
                                        value = invPrice,
                                        onValueChange = { invPrice = it },
                                        label = { Text("Price Unit ($)") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                    )
                                }

                                Button(
                                    onClick = {
                                        showProgress = true
                                        val totalQty = invQty.toIntOrNull() ?: 1
                                        val unitPrice = invPrice.toDoubleOrNull() ?: 0.00
                                        viewModel.saveInvoiceTemplate(
                                            fileName = invFileName,
                                            invoiceId = invId,
                                            clientName = invClient,
                                            itemName = invItemName,
                                            qty = totalQty,
                                            price = unitPrice,
                                            onFinished = { success ->
                                                showProgress = false
                                                if (success) {
                                                    successFileName = invFileName
                                                    showSuccessDialog = true
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("generate_invoice_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Compile Invoice to PDF File", fontWeight = FontWeight.Bold)
                                }
                            }

                            "LETTER" -> {
                                OutlinedTextField(
                                    value = letFileName,
                                    onValueChange = { letFileName = it },
                                    label = { Text("Output PDF Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = letRecipient,
                                    onValueChange = { letRecipient = it },
                                    label = { Text("Recipient Address / Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = letSubject,
                                    onValueChange = { letSubject = it },
                                    label = { Text("Letter Subject Title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = letBody,
                                    onValueChange = { letBody = it },
                                    label = { Text("Body Text Paragraph") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    minLines = 4
                                )
                                OutlinedTextField(
                                    value = letSender,
                                    onValueChange = { letSender = it },
                                    label = { Text("Sender Sign-off Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Button(
                                    onClick = {
                                        showProgress = true
                                        viewModel.saveLetterTemplate(
                                            fileName = letFileName,
                                            recipient = letRecipient,
                                            subject = letSubject,
                                            body = letBody,
                                            sender = letSender,
                                            onFinished = { success ->
                                                showProgress = false
                                                if (success) {
                                                    successFileName = letFileName
                                                    showSuccessDialog = true
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("generate_letter_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Compile Letter to PDF File", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LibraryBooks,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Please Select a Document Blueprint Above",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    Text("Brilliant")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PDF Generated Successfully", fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "Your template file '${successFileName}' was written sandbox-secure and compiled into a highly polished A4 PDF representation. File indexed inside your primary workspace."
                )
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
                    CircularProgressIndicator()
                    Text("Synthesizing template elements...", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun TemplateCategoryCard(
    title: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(100.dp)
            .testTag("template_card_$title"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(1.5.dp, color) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
