/*
 * Look4Sat. Amateur radio satellite tracker and pass predictor.
 * Copyright (C) 2019-2026 Arty Bishop and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.rtbishop.look4sat.feature.passes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rtbishop.look4sat.core.domain.utility.allBandConfigs
import com.rtbishop.look4sat.core.presentation.LocalSpacing
import com.rtbishop.look4sat.core.presentation.MainTheme
import com.rtbishop.look4sat.core.presentation.R
import com.rtbishop.look4sat.core.presentation.SharedDialog
import com.rtbishop.look4sat.core.presentation.elevationColor

private val hourSteps = listOf(1, 2, 4, 8, 12, 24, 48, 72, 96, 120, 144, 168, 192, 216, 240)

@Preview
@Composable
private fun PassesDialogPreview() {
    MainTheme { PassesDialog(24, 16.0, true, {}) { _, _, _ -> } }
}

@Composable
internal fun PassesDialog(
    hours: Int,
    elevation: Double,
    showDeepSpace: Boolean,
    cancel: () -> Unit,
    accept: (Int, Double, Boolean) -> Unit
) {
    val hoursIndex = remember { mutableIntStateOf(hourSteps.indexOfFirst { it >= hours }.coerceAtLeast(0)) }
    val elevationValueNew = remember { mutableDoubleStateOf(elevation) }
    var deepSpaceEnabled by remember { mutableStateOf(showDeepSpace) }
    val onAccept = {
        accept(hourSteps[hoursIndex.intValue], elevationValueNew.doubleValue, deepSpaceEnabled).also { cancel() }
    }
    SharedDialog(title = stringResource(R.string.pass_filter_title), onCancel = cancel, onAccept = onAccept) {
        ToggleRow(
            title = stringResource(R.string.pass_filter_deep_space),
            checked = deepSpaceEnabled,
            onCheckedChange = { deepSpaceEnabled = it }
        )
        SliderRow(
            title = stringResource(R.string.pass_filter_elev),
            value = elevationValueNew.doubleValue,
            displayValue = "${elevationValueNew.doubleValue.toInt()}°",
            valueResId = R.drawable.ic_elevation,
            valueRange = 0f..60f,
            accentColor = elevationColor(elevationValueNew.doubleValue)
        ) { elevationValueNew.doubleValue = it.toDouble() }
        SliderRow(
            title = stringResource(R.string.pass_filter_hours),
            value = hoursIndex.intValue.toDouble(),
            displayValue = "${hourSteps[hoursIndex.intValue]}h",
            valueResId = R.drawable.ic_clock,
            valueRange = 0f..(hourSteps.size - 1).toFloat(),
            steps = hourSteps.size - 2
        ) { hoursIndex.intValue = it.toInt().coerceIn(0, hourSteps.size - 1) }
    }
}

@Composable
private fun SliderRow(
    title: String,
    value: Double,
    displayValue: String,
    valueResId: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    onChange: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.small),
        modifier = Modifier.padding(horizontal = LocalSpacing.current.large)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                painter = painterResource(id = valueResId),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = displayValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = accentColor
            )
        }
        Slider(value = value.toFloat(), onValueChange = onChange, valueRange = valueRange, steps = steps)
    }
}

@Preview(showBackground = true)
@Composable
private fun TransponderDialogPreview() {
    MainTheme { TransponderDialog(emptyList(), emptyList(), emptyList(), {}) { _, _ -> } }
}

@Composable
internal fun TransponderDialog(
    modes: List<String>,
    bands: List<String>,
    availableModes: List<String>,
    cancel: () -> Unit,
    accept: (List<String>, List<String>) -> Unit
) {
    val selectedModes = remember { mutableStateOf(modes.toSet()) }
    val selectedBands = remember { mutableStateOf(bands.toSet()) }
    val toggleMode = { mode: String ->
        selectedModes.value = if (mode in selectedModes.value) selectedModes.value - mode else selectedModes.value + mode
    }
    val toggleBand = { band: String ->
        selectedBands.value = if (band in selectedBands.value) selectedBands.value - band else selectedBands.value + band
    }
    val onAccept = { accept(selectedModes.value.toList(), selectedBands.value.toList()).also { cancel() } }
    SharedDialog(title = stringResource(R.string.pass_transponder_title), onCancel = cancel, onAccept = onAccept) {
        // Modes section
        SectionHeader("Modulation mode")
        LazyVerticalGrid(
            columns = GridCells.Adaptive(240.dp),
            modifier = Modifier
                .fillMaxHeight(0.38f)
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(availableModes) { index, item ->
                FilterRow(
                    label = "${index + 1}).",
                    text = item,
                    checked = item in selectedModes.value,
                    onClick = { toggleMode(item) }
                )
            }
        }
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.background)
        // Bands section
        SectionHeader("Band configuration")
        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            modifier = Modifier
                .fillMaxHeight(0.62f)
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(allBandConfigs) { index, item ->
                FilterRow(
                    label = "${index + 1}).",
                    text = item,
                    checked = item in selectedBands.value,
                    onClick = { toggleBand(item) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
private fun FilterRow(label: String, text: String, checked: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp),
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = LocalSpacing.current.large)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
