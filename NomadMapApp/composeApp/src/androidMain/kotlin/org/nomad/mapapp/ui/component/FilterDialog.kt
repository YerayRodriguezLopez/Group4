package org.nomad.mapapp.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.nomad.mapapp.R

@Composable
fun FilterDialog(
    selectedTags: Set<String>,
    onTagsSelected: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    // This would normally be fetched from the API
    val availableTags = remember {
        listOf(
            "Sostenible", "Ecològic", "Artesanal", "Local", "Vegà",
            "Reciclat", "Comerç just", "Orgànic", "Zero residus", "Segona mà"
        )
    }

    var tempSelectedTags by remember { mutableStateOf(selectedTags) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter_by_tags),
                    style = MaterialTheme.typography.headlineSmall
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableTags) { tag ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tag)
                            Checkbox(
                                checked = tempSelectedTags.contains(tag),
                                onCheckedChange = { checked ->
                                    tempSelectedTags = if (checked) {
                                        tempSelectedTags + tag
                                    } else {
                                        tempSelectedTags - tag
                                    }
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onTagsSelected(tempSelectedTags)
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.apply))
                    }
                }
            }
        }
    }
}