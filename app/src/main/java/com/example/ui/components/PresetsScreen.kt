package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ManifestProfile
import com.example.viewmodel.ManifestViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PresetsScreen(
    presets: List<ManifestProfile>,
    history: List<ManifestProfile>,
    viewModel: ManifestViewModel,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Manifest Presets",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Load ready-made manifest blueprints",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.setShowSavePresetDialog(true) },
                    modifier = Modifier.testTag("button_save_current_as_preset")
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Current")
                }
            }
        }

        items(presets, key = { it.id }) { preset ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_preset_${preset.id}"),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        FilledTonalButton(
                            onClick = { viewModel.applyPreset(preset) },
                            modifier = Modifier.testTag("button_load_preset_${preset.id}")
                        ) {
                            Text("Apply")
                        }
                    }

                    Text(
                        text = preset.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Package: ${preset.manifest.packageName}",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "Target SDK: ${preset.manifest.targetSdk}",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "v${preset.manifest.versionName}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        if (history.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Save History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Previous versions saved to local disk",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(history, key = { it.id }) { item ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_history_${item.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = dateFormat.format(Date(item.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        FilledTonalButton(
                            onClick = { viewModel.applyPreset(item) },
                            modifier = Modifier.testTag("button_restore_history_${item.id}")
                        ) {
                            Text("Restore")
                        }
                    }
                }
            }
        }
    }
}
