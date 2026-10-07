package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ManifestData
import com.example.util.ManifestXmlParser

@Composable
fun DiffViewerScreen(
    current: ManifestData,
    original: ManifestData,
    modifier: Modifier = Modifier
) {
    val currentXml = ManifestXmlParser.serialize(current)
    val originalXml = ManifestXmlParser.serialize(original)

    val currentLines = currentXml.lines()
    val originalLines = originalXml.lines()

    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(
                text = "Manifest Changes Diff",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (current == original) "No changes made yet." else "Lines highlighted in green (added/changed) vs red (original)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("diff_viewer_surface"),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF181825)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(vScroll)
                    .horizontalScroll(hScroll)
            ) {
                val maxLines = maxOf(currentLines.size, originalLines.size)
                for (i in 0 until maxLines) {
                    val currLine = currentLines.getOrNull(i)
                    val origLine = originalLines.getOrNull(i)

                    if (currLine != null && origLine != null) {
                        if (currLine == origLine) {
                            Text(
                                text = "  $currLine",
                                color = Color(0xFFCDD6F4),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = "- $origLine",
                                color = Color(0xFFF38BA8),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.background(Color(0xFF451928))
                            )
                            Text(
                                text = "+ $currLine",
                                color = Color(0xFFA6E3A1),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.background(Color(0xFF1D422B))
                            )
                        }
                    } else if (currLine != null) {
                        Text(
                            text = "+ $currLine",
                            color = Color(0xFFA6E3A1),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.background(Color(0xFF1D422B))
                        )
                    } else if (origLine != null) {
                        Text(
                            text = "- $origLine",
                            color = Color(0xFFF38BA8),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.background(Color(0xFF451928))
                        )
                    }
                }
            }
        }
    }
}
