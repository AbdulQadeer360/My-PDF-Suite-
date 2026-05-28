package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
fun PremiumScreen(
    viewModel: PdfViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Simulated Ad States
    var showInterstitialSim by remember { mutableStateOf(false) }
    var interstitialCountdown by remember { mutableIntStateOf(5) }
    
    var showRewardedSim by remember { mutableStateOf(false) }
    var rewardedCountdown by remember { mutableIntStateOf(5) }
    var rewardedStatus by remember { mutableStateOf("Watching video space...") }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("premium_root"),
        topBar = {
            TopAppBar(
                title = { Text("100% Free Core Suite", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("premium_back_btn")) {
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
            // Elegant premium hero card declaring 100% free with premium quality
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "NO PREMIUM SUBSCRIPTIONS",
                            color = Color(0xFF60A5FA),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = "100% Unlocked Core Features",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = "My PDF Suite provides state-of-the-art annotations, templates, compression, security tools and OCR tools entirely for free. Funded by Google AdMob support channels.",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Section: AdMob Integration status
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Google AdMob Performance", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("To sustain servers and offline utilities compilers, we maintain non-intrusive, balanced AdMob networks.", fontSize = 11.sp, color = Color.Gray)

                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    AdBenefitRow(
                        icon = Icons.Default.Campaign,
                        title = "Zero Task Interruption",
                        desc = "No overlay interstitials play while processing conversions, protecting workspace tasks."
                    )
                    AdBenefitRow(
                        icon = Icons.Default.DoneAll,
                        title = "No Hidden Gates",
                        desc = "All converters including OCR text extraction, watermark, security lock, templates are free."
                    )
                }
            }

            // Section: IN-APP REWARDED & INTERSTITIALS SIMULATOR DECK
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ADMOB SIMULATOR DECK",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text("Auditing AdMob behaviors manually prior to submission.", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                interstitialCountdown = 5
                                showInterstitialSim = true
                                coroutineScope.launch {
                                    while (interstitialCountdown > 0) {
                                        delay(1000)
                                        interstitialCountdown--
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Sim Interstitial", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                rewardedCountdown = 5
                                rewardedStatus = "Streaming virtual AdMob sponsor content..."
                                showRewardedSim = true
                                coroutineScope.launch {
                                    while (rewardedCountdown > 0) {
                                        delay(1000)
                                        rewardedCountdown--
                                    }
                                    rewardedStatus = "Ad watched! 1 credit unit rewarded."
                                }
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Watch Rewarded", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Standard Live Banner Ad simulation matching standard AdMob width guidelines (320x50 dps)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "STANDARDIZED GOOGLE ADMOB BANNER AD REPRESENTATION",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(320.dp)
                        .height(55.dp)
                        .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF475569), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Ad", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Fast PDF compress offline standard.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text("Google Approved Sponsor Agency Ltd (ca-app-pub-3940251119/2026)", fontSize = 8.sp, color = Color(0xFF64748B))
                        }
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    }
                }
            }
        }
    }

    // Interstitial Sim Modal
    if (showInterstitialSim) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.DarkGray, RoundedCornerShape(4.dp))
                            .padding(6.dp)
                    ) {
                        Text("Google AdMob Interstitial Sim", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (interstitialCountdown > 0) {
                        Text("Skippable in ${interstitialCountdown}s", color = Color.White, fontSize = 12.sp)
                    } else {
                        IconButton(onClick = { showInterstitialSim = false }) {
                            Icon(Icons.Default.Clear, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))

                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(96.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Simulated Interstitial Ad", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(
                    "This represents standard full screen AdMob interstitials which play on non-intrusive action endpoints.",
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text("App Developer Sandbox Verification", color = Color.Gray, fontSize = 10.sp)
            }
        }
    }

    // Rewarded Sim Modal
    if (showRewardedSim) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sponsor Rewarded Ad", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        if (rewardedCountdown > 0) {
                            Text("Reward in ${rewardedCountdown}s", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        } else {
                            IconButton(onClick = { showRewardedSim = false }) {
                                Icon(Icons.Default.Clear, contentDescription = "Close")
                            }
                        }
                    }

                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(64.dp))
                    Text(rewardedStatus, fontSize = 13.sp, textAlign = TextAlign.Center)

                    if (rewardedCountdown == 0) {
                        Button(
                            onClick = { showRewardedSim = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Claim Reward & Dismiss")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdBenefitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(desc, fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
        }
    }
}
