package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Movie
import com.example.ui.AppLanguage
import com.example.ui.Strings
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaDarkCard
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaTextSecondary

@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    allMovies: List<Movie>,
    isAdmin: Boolean = false,
    onLanguageChange: (AppLanguage) -> Unit,
    onAdminLogin: (String) -> Boolean = { false },
    onAdminLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalMoviesCount = allMovies.size
    val userAddedCount = allMovies.count { it.isUserAdded }
    val kurdishMoviesCount = allMovies.count { it.category == "M1955 Cinema" || it.categoryKu.contains("کوردی") }

    var adminPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Stats Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaDarkCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CinemaRed,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Strings.get("settings_stats", currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = CinemaCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Firebase Firestore Realtime Sync",
                                    color = CinemaCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(
                            value = "$totalMoviesCount",
                            label = Strings.get("total_movies", currentLanguage),
                            color = CinemaGold
                        )
                        StatItem(
                            value = "$kurdishMoviesCount",
                            label = if (currentLanguage == AppLanguage.ENGLISH) "Kurdish Films" else "فلمان کوردی",
                            color = CinemaCyan
                        )
                        StatItem(
                            value = "$userAddedCount",
                            label = Strings.get("user_movies", currentLanguage),
                            color = CinemaRed
                        )
                    }
                }
            }
        }

        // Admin Portal Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAdmin) Color(0xFF2A1C20) else CinemaDarkCard
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (isAdmin) CinemaGold else Color(0xFF374151),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isAdmin) Color.Black else Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.ENGLISH) "Admin Portal (مدیر)" else "دەسەڵاتا ئەدمینی (Admin)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAdmin)
                                        (if (currentLanguage == AppLanguage.ENGLISH) "Admin Mode Active (Upload & Edit enabled)" else "ئەدمین کارایە (زێدەکرن و گۆڕین ڤەبوویە)")
                                    else
                                        (if (currentLanguage == AppLanguage.ENGLISH) "Viewer Mode (Movies read-only)" else "مۆدی بینەر (تەنیا تەماشەکرن)"),
                                    color = if (isAdmin) CinemaGold else CinemaTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isAdmin) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CinemaGold
                            ) {
                                Text(
                                    text = "ADMIN",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isAdmin) {
                        // Admin is logged in
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH)
                                "You are logged in as Admin. You have exclusive access to (+) Add Movies and direct Firestore publishing."
                            else
                                "تۆ وەک ئەدمین چوویە ژوورەوە. دوگمەیا زێدەکرنا فلمان (+) و بەلاڤکرنا ڕاستەوخۆ ل جەم هەمووان کارایە.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onAdminLogout,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.ENGLISH) "Log Out from Admin" else "دەرکەفتن ژ ئەدمینی",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Enter Admin PIN
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH)
                                "Enter Admin PIN (Default: ******) to unlock movie uploading & Firestore publishing:"
                            else
                                "پینا ئەدمینی بنڤیسە (ستاندارد: ******) دا دوگمەیا زێدەکرنا فلمان بۆ تە دیار ببیت:",
                            color = CinemaTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = adminPinInput,
                                onValueChange = {
                                    adminPinInput = it
                                    pinError = false
                                },
                                placeholder = {
                                    Text(
                                        text = "PIN (******)",
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    val ok = onAdminLogin(adminPinInput)
                                    if (ok) adminPinInput = "" else pinError = true
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CinemaGold,
                                    unfocusedBorderColor = Color(0xFF374151),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    val ok = onAdminLogin(adminPinInput)
                                    if (ok) adminPinInput = "" else pinError = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CinemaGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.ENGLISH) "Login" else "چوونەژوور",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (pinError) {
                            Text(
                                text = if (currentLanguage == AppLanguage.ENGLISH) "Invalid PIN! Please check and try again" else "پین شاشە! پینا دروست بنڤیسە",
                                color = CinemaRed,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Language Selector Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaDarkCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = CinemaCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = Strings.get("settings_language", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AppLanguage.values().forEach { lang ->
                        val isSelected = currentLanguage == lang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLanguageChange(lang) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = lang.nativeName,
                                    color = if (isSelected) CinemaRed else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = lang.displayName,
                                    color = CinemaTextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { onLanguageChange(lang) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CinemaRed,
                                    unselectedColor = Color(0xFF4A5568)
                                )
                            )
                        }
                        if (lang != AppLanguage.values().last()) {
                            HorizontalDivider(color = Color(0xFF1E2433))
                        }
                    }
                }
            }
        }

        // App Information Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "M1955 Cinema",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Strings.get("tagline", currentLanguage),
                        color = CinemaTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Version 1.0.0 • Firebase Realtime Cloud",
                        color = CinemaCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = CinemaTextSecondary,
            fontSize = 11.sp
        )
    }
}
