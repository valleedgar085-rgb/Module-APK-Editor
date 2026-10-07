package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ManifestData
import com.example.model.ValidationResult
import com.example.ui.theme.WorkbenchActive
import com.example.ui.theme.WorkbenchBorder
import com.example.ui.theme.WorkbenchCode
import com.example.ui.theme.WorkbenchCyan
import com.example.ui.theme.WorkbenchElevated
import com.example.ui.theme.WorkbenchError
import com.example.ui.theme.WorkbenchLabel
import com.example.ui.theme.WorkbenchLime
import com.example.ui.theme.WorkbenchSurface
import com.example.ui.theme.WorkbenchTextPrimary
import com.example.ui.theme.WorkbenchTextSecondary
import com.example.ui.theme.WorkbenchWarning
import com.example.util.ManifestXmlParser

private data class ManifestDiffLine(
    val lineNumber: Int,
    val prefix: String,
    val content: String
)

@Composable
fun DiffViewerScreen(
    current: ManifestData,
    original: ManifestData,
    validation: ValidationResult,
    onShowXml: () -> Unit,
    onDiscard: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentXml = remember(current) { ManifestXmlParser.serialize(current) }
    val originalXml = remember(original) { ManifestXmlParser.serialize(original) }
    val diffLines = remember(currentXml, originalXml) {
        buildDiffLines(originalXml, currentXml)
    }
    val changeCount = remember(diffLines) {
        diffLines.map { it.lineNumber }.distinct().size
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                ReviewTab(
                    label = "XML",
                    selected = false,
                    onClick = onShowXml,
                    modifier = Modifier.weight(1f)
                )
                ReviewTab(
                    label = "DIFF",
                    selected = true,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkbenchMetricTile(
                label = "CHANGES",
                value = changeCount.toString(),
                valueColor = WorkbenchCyan
            )
            WorkbenchMetricTile(
                label = "ERRORS",
                value = validation.errors.size.toString(),
                valueColor = if (validation.errors.isEmpty()) WorkbenchLime else WorkbenchError
            )
            WorkbenchMetricTile(
                label = "WARNINGS",
                value = validation.warnings.size.toString(),
                valueColor = WorkbenchWarning
            )
        }

        WorkbenchSection(
            kicker = "ANDROIDMANIFEST.XML",
            title = "Proposed diff",
            elevated = true,
            modifier = Modifier.testTag("diff_viewer_surface")
        ) {
            if (diffLines.isEmpty()) {
                Text(
                    text = "No changes made yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WorkbenchTextSecondary
                )
            } else {
                diffLines.forEach { line ->
                    DiffLineRow(line)
                }
            }
        }

        when {
            validation.errors.isNotEmpty() -> {
                WorkbenchStatusBanner(
                    title = "Blocking manifest error",
                    detail = validation.errors.first(),
                    color = WorkbenchError
                )
            }
            validation.warnings.isNotEmpty() -> {
                WorkbenchStatusBanner(
                    title = "One best-practice warning",
                    detail = validation.warnings.first(),
                    color = WorkbenchWarning
                )
            }
            else -> {
                WorkbenchStatusBanner(
                    title = "Manifest validation clean",
                    detail = "No errors or warnings are blocking this manifest.",
                    color = WorkbenchLime
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            WorkbenchActionButton(
                label = "DISCARD",
                onClick = onDiscard,
                primary = false,
                enabled = diffLines.isNotEmpty()
            )
            WorkbenchActionButton(
                label = "SAVE MANIFEST",
                onClick = onSave,
                primary = true,
                enabled = diffLines.isNotEmpty()
            )
        }
    }
}

@Composable
private fun ReviewTab(
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
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = WorkbenchLabel,
                color = if (selected) WorkbenchCyan else WorkbenchTextSecondary
            )
        }
    }
}

@Composable
private fun DiffLineRow(line: ManifestDiffLine) {
    val isAdded = line.prefix == "+"
    val accent = if (isAdded) {
        if (line.content.contains("targetSdkVersion")) WorkbenchLime else WorkbenchCyan
    } else {
        WorkbenchError
    }

    Surface(
        color = if (isAdded) WorkbenchActive else WorkbenchSurface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = line.lineNumber.toString(),
                style = WorkbenchCode,
                color = WorkbenchTextSecondary,
                modifier = Modifier.width(24.dp)
            )
            Text(
                text = line.prefix,
                style = WorkbenchCode,
                color = accent
            )
            Text(
                text = line.content,
                style = WorkbenchCode,
                color = if (isAdded) accent else WorkbenchTextSecondary
            )
        }
    }
}

private fun buildDiffLines(
    originalXml: String,
    currentXml: String
): List<ManifestDiffLine> {
    val originalLines = originalXml.lines()
    val currentLines = currentXml.lines()
    val maxLines = maxOf(originalLines.size, currentLines.size)
    val result = mutableListOf<ManifestDiffLine>()

    for (index in 0 until maxLines) {
        val original = originalLines.getOrNull(index)
        val current = currentLines.getOrNull(index)
        if (original == current) continue

        val lineNumber = index + 1
        if (original != null) {
            result += ManifestDiffLine(
                lineNumber = lineNumber,
                prefix = "-",
                content = original.trim()
            )
        }
        if (current != null) {
            result += ManifestDiffLine(
                lineNumber = lineNumber,
                prefix = "+",
                content = current.trim()
            )
        }
    }

    return result
}
