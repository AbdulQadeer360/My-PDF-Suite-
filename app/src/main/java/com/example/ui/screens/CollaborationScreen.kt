package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PdfViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollaborationScreen(
    viewModel: PdfViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Joint session states
    var invitationLink by remember { mutableStateOf("") }
    var sessionActive by remember { mutableStateOf(false) }
    var inviteeEmail by remember { mutableStateOf("sarah_designer@cloud.com") }
    
    // Connected user states
    val activeCollaborators = remember { mutableStateListOf("Rahad Hussain (You)") }
    
    // Simulated remote annotations & actions
    val simulatedActivities = remember { mutableStateListOf<String>() }
    var simulatedObjectCount by remember { mutableIntStateOf(0) }
    var isSimulatingPeer by remember { mutableStateOf(false) }

    // Version histories list
    val versionHistoryList = remember { 
        mutableStateListOf(
            VersionItem("V1-INITIAL", "System Init", "ca301b", "14:02"),
            VersionItem("V2-DRAFT", "Rahad (Owner)", "9a42f5", "14:10")
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("collaboration_root"),
        topBar = {
            TopAppBar(
                title = { Text("Real-Time Compilation Collab", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("collaboration_back_btn")) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Introduction to PDF Collab Feasibility
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ARCHITECTURE & FEASIBILITY STUDY",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Real-time PDF synchronization operates through dynamic client-side coordination structures instead of raw binary locking.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    BlueprintDetailTile(
                        icon = Icons.Default.Share,
                        title = "1. Security Invites Mechanism",
                        desc = "Generate AES-256 tokens mapping a collaborative Session ID. Incoming invitees authenticate using security pins, downloading initial replica states."
                    )
                    BlueprintDetailTile(
                        icon = Icons.Default.Settings,
                        title = "2. Operations state Live Sync",
                        desc = "Sync JSON annotations patches containing precise coordinate arrays via WebSockets or Firebase Realtime DB. Resolve edit races using OT/CRDT convergence rules."
                    )
                    BlueprintDetailTile(
                        icon = Icons.Default.Build,
                        title = "3. Immutable Versioning Trees",
                        desc = "Keep an incremental database tracking sha-256 commits. Restoring a version rolls back database values, auto-regenerating physical PDF files offline."
                    )
                }
            }

            // Section 2: Invitation Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Session Coordinator Hub", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Simulate starting a collaborative PDF editing network segment.", fontSize = 11.sp, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    if (!sessionActive) {
                        OutlinedTextField(
                            value = inviteeEmail,
                            onValueChange = { inviteeEmail = it },
                            label = { Text("Collaborator Email Invite") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                sessionActive = true
                                invitationLink = "https://mypdfsuite.com/collab/session-77fa8b912"
                                activeCollaborators.add("Sarah Eng ($inviteeEmail)")
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Collaborator invited! Virtual connection open.")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Invite Peer & Open Room", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Room Active panel
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                .padding(12.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            Text("Active Shared Room Token", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Text(invitationLink, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Shared with: $inviteeEmail", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                sessionActive = false
                                activeCollaborators.clear()
                                activeCollaborators.add("Rahad Hussain (You)")
                                simulatedActivities.clear()
                                simulatedObjectCount = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Terminate Connection Room Layer", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Section 3: Live Collab Sandbox Simulator
            if (sessionActive) {
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
                            Text("Shared Live Preview Canvas", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                activeCollaborators.forEachIndexed { i, _ ->
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (i == 0) Color(0xFF3B82F6) else Color(0xFFEC4899)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(if (i == 0) "RH" else "S", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                        
                        // Shared Sheet area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(vertical = 10.dp)
                                .background(Color(0xFFFAFBFD))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("JOINT PDF DOCS WORKING REPLICA", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Shared Base Text layer loaded.", fontSize = 9.sp)
                                }

                                if (simulatedObjectCount > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sarah added $simulatedObjectCount rect/sticky overlays mock values.", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF3B82F6)))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rahad cursor: [pos: 120, 240] (idle)", fontSize = 8.sp, color = Color.DarkGray)
                                }
                                if (isSimulatingPeer) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFEC4899)))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Sarah cursor: [pos: 340, 110] (drawing...)", fontSize = 8.sp, color = Color(0xFFEC4899), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Simulation buttons
                        Button(
                            onClick = {
                                isSimulatingPeer = true
                                coroutineScope.launch {
                                    delay(1200)
                                    simulatedObjectCount++
                                    simulatedActivities.add(0, "Sarah added a sticky note: 'Correct this number'")
                                    versionHistoryList.add(
                                        0,
                                        VersionItem(
                                            "V3-EDIT",
                                            "Sarah Eng",
                                            "f49a8c",
                                            "Just Now"
                                        )
                                    )
                                    isSimulatingPeer = false
                                    snackbarHostState.showSnackbar("Received delta update: Sarah drew a sticky note.")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSimulatingPeer
                        ) {
                            if (isSimulatingPeer) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Receiving Live Peer Deltas...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simulate Remote Changes from Sarah", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Peer activities logs
                        if (simulatedActivities.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Joint Room Event Feed (Operations):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            simulatedActivities.forEach { act ->
                                Text("• $act", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Section 4: Version History List
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
                        Column {
                            Text("Continuous Revision History", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Database hashes storing PDF snapshot commits.", fontSize = 11.sp, color = Color.Gray)
                        }
                        IconButton(onClick = {
                            versionHistoryList.add(
                                0,
                                VersionItem("V4-USER-COMMIT", "Rahad (You)", "12f0da", "Just Now")
                            )
                        }) {
                            Icon(Icons.Default.Folder, contentDescription = "Commit snapshot")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    versionHistoryList.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(10.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.name, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("hash: [${item.hash}]", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Color.Gray)
                                }
                                Text("Author: ${item.author}  •  ${item.time}", fontSize = 10.sp, color = Color.DarkGray)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Rolled back Workspace state to core revision [${item.hash}] successfully.")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Restore", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BlueprintDetailTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
        }
    }
}

data class VersionItem(
    val name: String,
    val author: String,
    val hash: String,
    val time: String
)
