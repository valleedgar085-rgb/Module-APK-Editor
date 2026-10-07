package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ManifestViewModel

@Composable
fun XmlPreviewScreen(
    xmlContent: String,
    viewModel: ManifestViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AndroidManifest.xml",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Live formatted syntax view",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.refreshXmlPreview() },
                    modifier = Modifier.testTag("button_refresh_xml")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh XML")
                }

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("AndroidManifest.xml", xmlContent)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied XML to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("button_copy_xml")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy XML")
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("xml_code_surface"),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E1E2E)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(vScroll)
                    .horizontalScroll(hScroll)
            ) {
                Text(
                    text = xmlContent,
                    color = Color(0xFFA6E3A1),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
