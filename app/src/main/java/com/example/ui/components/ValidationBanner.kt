package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ValidationResult

@Composable
fun ValidationBanner(
    validation: ValidationResult,
    modifier: Modifier = Modifier
) {
    if (!validation.hasIssues) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .testTag("validation_banner_success"),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE6F4EA)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Valid Manifest",
                    tint = Color(0xFF137333)
                )
                Text(
                    text = "Manifest parameters are valid and ready to build.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF137333)
                )
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (validation.errors.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("validation_errors_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Errors",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Validation Errors (${validation.errors.size})",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        validation.errors.forEach { err ->
                            Text(
                                text = "• $err",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                            )
                        }
                    }
                }
            }

            if (validation.warnings.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("validation_warnings_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF7E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warnings",
                                tint = Color(0xFFB06000)
                            )
                            Text(
                                text = "Best Practice Warnings (${validation.warnings.size})",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF7A4100)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        validation.warnings.forEach { warn ->
                            Text(
                                text = "• $warn",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7A4100),
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
