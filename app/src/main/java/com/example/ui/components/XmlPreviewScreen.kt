package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WorkbenchActive
import com.example.ui.theme.WorkbenchBorder
import com.example.ui.theme.WorkbenchCode
import com.example.ui.theme.WorkbenchCyan
import com.example.ui.theme.WorkbenchElevated
import com.example.ui.theme.WorkbenchLabel
import com.example.ui.theme.WorkbenchSurface
import com.example.ui.theme.WorkbenchTextSecondary
import com.example.viewmodel.ManifestViewModel
import com.example.viewmodel.ScreenTab

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
        Surface(
            color = WorkbenchSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                XmlReviewTab(
                    label = "XML",
                    selected = true,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                XmlReviewTab(
                    label = "DIFF",
                    selected = false,
                    onClick = { viewModel.setTab(ScreenTab.Diff) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, androidx.compose.ui.Alignment.End)
        ) {
            WorkbenchActionButton(
                label = "REFRESH",
                onClick = viewModel::refreshXmlPreview,
                primary = false,
                testTag = "button_refresh_xml"
            )
            WorkbenchActionButton(
                label = "COPY XML",
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("AndroidManifest.xml", xmlContent)
                    )
                    Toast.makeText(context, "Copied XML to clipboard!", Toast.LENGTH_SHORT).show()
                },
                primary = true,
                testTag = "button_copy_xml"
            )
        }

        WorkbenchSection(
            kicker = "ANDROIDMANIFEST.XML",
            title = "Live formatted syntax view",
            elevated = true,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                color = WorkbenchElevated,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, WorkbenchBorder),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("xml_code_surface")
            ) {
                Text(
                    text = xmlContent,
                    style = WorkbenchCode,
                    color = WorkbenchCyan,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(vScroll)
                        .horizontalScroll(hScroll)
                        .padding(12.dp)
                )
            }
        }

        Text(
            text = "Changes update automatically as you edit the manifest.",
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = WorkbenchTextSecondary
        )
    }
}

@Composable
private fun XmlReviewTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) WorkbenchActive else WorkbenchSurface,
        shape = RoundedCornerShape(12.dp),
        border = if (selected) BorderStroke(1.dp, WorkbenchCyan) else null,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = WorkbenchLabel,
            color = if (selected) WorkbenchCyan else WorkbenchTextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
