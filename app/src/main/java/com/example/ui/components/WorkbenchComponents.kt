package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WorkbenchActive
import com.example.ui.theme.WorkbenchBackground
import com.example.ui.theme.WorkbenchBorder
import com.example.ui.theme.WorkbenchCode
import com.example.ui.theme.WorkbenchCyan
import com.example.ui.theme.WorkbenchElevated
import com.example.ui.theme.WorkbenchLabel
import com.example.ui.theme.WorkbenchLime
import com.example.ui.theme.WorkbenchSurface
import com.example.ui.theme.WorkbenchTextPrimary
import com.example.ui.theme.WorkbenchTextSecondary
import com.example.viewmodel.ScreenTab

@Composable
fun WorkbenchScreenFrame(
    eyebrow: String,
    title: String,
    statusLabel: String,
    statusColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WorkbenchBackground)
    ) {
        WorkbenchScreenHeader(
            eyebrow = eyebrow,
            title = title,
            statusLabel = statusLabel,
            statusColor = statusColor
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            content()
        }
    }
}

@Composable
fun WorkbenchScreenHeader(
    eyebrow: String,
    title: String,
    statusLabel: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WorkbenchSurface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = eyebrow,
                    style = WorkbenchLabel,
                    color = WorkbenchCyan
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = WorkbenchTextPrimary
                )
            }
            WorkbenchStatusPill(
                label = statusLabel,
                color = statusColor
            )
        }
    }
}

@Composable
fun WorkbenchStatusPill(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WorkbenchElevated,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, color),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = label,
                style = WorkbenchLabel,
                color = color,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WorkbenchActionButton(
    label: String,
    onClick: () -> Unit,
    primary: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String? = null
) {
    val surfaceModifier = modifier
        .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
        .alpha(if (enabled) 1f else 0.45f)
        .clickable(enabled = enabled, onClick = onClick)

    Surface(
        color = if (primary) WorkbenchCyan else WorkbenchElevated,
        contentColor = if (primary) WorkbenchBackground else WorkbenchTextPrimary,
        shape = RoundedCornerShape(12.dp),
        border = if (primary) null else BorderStroke(1.dp, WorkbenchBorder),
        shadowElevation = if (primary) 6.dp else 0.dp,
        modifier = surfaceModifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = WorkbenchLabel,
                color = if (primary) WorkbenchBackground else WorkbenchTextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WorkbenchStatusBanner(
    title: String,
    detail: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WorkbenchActive,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(42.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = color
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WorkbenchTextSecondary
                )
            }
        }
    }
}

@Composable
fun WorkbenchSection(
    kicker: String,
    title: String,
    modifier: Modifier = Modifier,
    accent: Color = WorkbenchCyan,
    elevated: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        color = if (elevated) WorkbenchElevated else WorkbenchSurface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, WorkbenchBorder),
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = kicker,
                style = WorkbenchLabel,
                color = accent
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = WorkbenchTextPrimary
            )
            content()
        }
    }
}

@Composable
fun TechnicalTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    testTag: String? = null
) {
    val fieldModifier = modifier
        .fillMaxWidth()
        .heightIn(min = 76.dp)
        .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)

    Surface(
        color = WorkbenchElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, WorkbenchBorder),
        modifier = fieldModifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = WorkbenchLabel,
                color = WorkbenchTextSecondary
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = keyboardOptions,
                cursorBrush = SolidColor(WorkbenchCyan),
                textStyle = WorkbenchCode.copy(color = WorkbenchTextPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RowScope.WorkbenchMetricTile(
    label: String,
    value: String,
    valueColor: Color = WorkbenchTextPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WorkbenchElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, WorkbenchBorder),
        modifier = modifier
            .weight(1f)
            .height(84.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = WorkbenchLabel,
                color = WorkbenchTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun WorkbenchPermissionRow(
    name: String,
    badge: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(
        color = WorkbenchElevated,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = WorkbenchCode,
                color = WorkbenchTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            if (trailing != null) {
                trailing()
            } else {
                WorkbenchStatusPill(
                    label = badge,
                    color = badgeColor
                )
            }
        }
    }
}

@Composable
fun WorkbenchBottomNavigation(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple("EDIT", ScreenTab.Form, "nav_item_form"),
        Triple("INSPECT", ScreenTab.Inspector, "nav_item_inspector"),
        Triple("XML", ScreenTab.XmlPreview, "nav_item_xml"),
        Triple("DIFF", ScreenTab.Diff, "nav_item_diff"),
        Triple("PRESETS", ScreenTab.Presets, "nav_item_presets")
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(WorkbenchSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("bottom_navigation"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (label, tab, tag) ->
            val selected = currentTab == tab
            Surface(
                color = if (selected) WorkbenchActive else WorkbenchSurface,
                shape = RoundedCornerShape(12.dp),
                border = if (selected) BorderStroke(1.dp, WorkbenchCyan) else null,
                modifier = Modifier
                    .width(64.dp)
                    .height(48.dp)
                    .testTag(tag)
                    .clickable { onTabSelected(tab) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (selected) 22.dp else 4.dp)
                            .height(3.dp)
                            .background(
                                if (selected) WorkbenchCyan else WorkbenchBorder,
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = label,
                        style = WorkbenchLabel,
                        color = if (selected) WorkbenchCyan else WorkbenchTextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
