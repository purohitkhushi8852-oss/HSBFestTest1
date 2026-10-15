package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zenith26.fest.data.api.ApiConfig
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestCyan
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestSurfaceLight
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.theme.FestTextPrimary
import com.zenith26.fest.ui.theme.FestTextSecondary
import com.zenith26.fest.ui.theme.ModalHeaderGradient
import com.zenith26.fest.ui.theme.PrimaryButtonGradient

@Composable
fun ApiConfigDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onUrlUpdated: (String) -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, FestBorder, RoundedCornerShape(20.dp)),
            color = FestSurface
        ) {
            var urlInput by remember { mutableStateOf(ApiConfig.getBaseUrl()) }

            Column {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ModalHeaderGradient)
                        .border(1.dp, FestBorder.copy(alpha = 0.8f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Backend Network Config",
                        color = FestTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = FestTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Base API URL",
                        color = FestTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF020617))
                            .border(1.dp, FestBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            cursorBrush = SolidColor(FestCyan),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quick Presets:",
                        color = FestTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FestSurfaceLight)
                                .border(1.dp, FestBorder, RoundedCornerShape(8.dp))
                                .clickable { urlInput = ApiConfig.DEFAULT_EMULATOR_URL }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Emulator",
                                color = FestTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FestSurfaceLight)
                                .border(1.dp, FestBorder, RoundedCornerShape(8.dp))
                                .clickable { urlInput = ApiConfig.DEFAULT_HOST_WIFI_URL }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Wi-Fi (172.16.52.51)",
                                color = FestCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FestSurfaceLight)
                                .border(1.dp, FestBorder, RoundedCornerShape(8.dp))
                                .clickable { urlInput = ApiConfig.DEFAULT_FALLBACK_LAN_URL }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LAN (172.16.60.111)",
                                color = FestTextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Emulators reach host PC via http://10.0.2.2:8081/\n• Physical phones on Wi-Fi reach host PC via http://172.16.52.51:8081/\n• Offline fallback active automatically if network is unreachable.",
                        color = FestTextMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = FestBorder.copy(alpha = 0.5f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FestSurfaceLight)
                                .clickable {
                                    ApiConfig.resetToDefault()
                                    urlInput = ApiConfig.DEFAULT_EMULATOR_URL
                                    onUrlUpdated(ApiConfig.DEFAULT_EMULATOR_URL)
                                    onDismiss()
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = "Reset", color = FestTextMuted, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryButtonGradient)
                                .clickable {
                                    ApiConfig.setBaseUrl(urlInput.trim())
                                    onUrlUpdated(urlInput.trim())
                                    onDismiss()
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Save & Apply",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
