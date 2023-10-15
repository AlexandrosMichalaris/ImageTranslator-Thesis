package com.example.textrecognitionproject.UIComponents

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    modifier: Modifier = Modifier,
    modifierInsideItems: Modifier,
    listOptions: List<String>,
    onSelection: (String) -> Unit,
    value: String,
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(value) }
    Column(Modifier.animateContentSize(animationSpec = tween())) {
        ExposedDropdownMenuBox(
            modifier = modifier,
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier
                        .menuAnchor()
                        .padding(16.dp),
                    text = textFieldValue,
                )
                if (listOptions.isNotEmpty()) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .rotate(if (expanded) {180f} else {0f})
                    )
                }
            }

            if (listOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = modifierInsideItems
                ) {
                    listOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(selectionOption) },
                            onClick = {
                                textFieldValue = selectionOption
                                expanded = false
                                onSelection(selectionOption)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}