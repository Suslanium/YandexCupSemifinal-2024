package com.suslanium.yandexcupsemifinal.ui.screens.main.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.suslanium.yandexcupsemifinal.R
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.AdditionalToolsState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.generator.FrameGeneratorType

data class FrameGenerationDropdownItem(
    @StringRes val titleRes: Int,
    val generationType: FrameGeneratorType,
)

private val frameGenerationDropdownItems = listOf(
    FrameGenerationDropdownItem(
        titleRes = R.string.koch_snowflake,
        generationType = FrameGeneratorType.KochSnowflake,
    ),
    FrameGenerationDropdownItem(
        titleRes = R.string.koch_antisnowflake,
        generationType = FrameGeneratorType.KochAntiSnowflake,
    )
)

@Composable
fun FrameGenerationDialog(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {
    if (state.additionalToolsState == AdditionalToolsState.FrameGenerationDialog) {
        var isDropDownVisible by remember { mutableStateOf(false) }
        var itemPosition by remember { mutableIntStateOf(0) }
        var frameAmount by remember { mutableStateOf("100") }
        Dialog(onDismissRequest = { onEvent(MainScreenEvent.FrameGenerationDismissed) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = stringResource(R.string.generation_type),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            isDropDownVisible = true
                        },
                    ) {
                        Text(
                            text = stringResource(frameGenerationDropdownItems[itemPosition].titleRes),
                            fontSize = 16.sp,
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_drop_down),
                            contentDescription = null,
                        )
                        DropdownMenu(
                            expanded = isDropDownVisible,
                            onDismissRequest = { isDropDownVisible = false },
                        ) {
                            frameGenerationDropdownItems.forEachIndexed { index, item ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(item.titleRes))
                                    },
                                    onClick = {
                                        itemPosition = index
                                        isDropDownVisible = false
                                    },
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.frame_amount),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = frameAmount,
                        onValueChange = { frameAmount = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onEvent(
                                MainScreenEvent.FrameGenerationConfirmed(
                                    frameGenerationDropdownItems[itemPosition].generationType,
                                    frameAmount,
                                )
                            )
                        },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(text = stringResource(R.string.generate))
                    }
                }
            }
        }
    }
}