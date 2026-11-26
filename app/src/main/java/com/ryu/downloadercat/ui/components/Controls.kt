package com.ryu.downloadercat.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ryu.downloadercat.R
import com.ryu.downloadercat.ui.theme.AppTheme

data class DropdownOption<T>(
    val value: T,
    val label: String,
)

@Composable
fun AppSwitchVisual(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val thumbOffset by animateDpAsState(
        targetValue = when {
            checked && rtl -> 4.dp
            checked -> 36.dp
            rtl -> 36.dp
            else -> 4.dp
        },
        animationSpec = tween(240),
        label = "switchOffset",
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) colors.onBackground else colors.muted,
        animationSpec = tween(200),
        label = "switchThumb",
    )
    Box(
        modifier = modifier
            .width(72.dp)
            .height(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(40.dp)
                .clip(CircleShape)
                .background(colors.surfaceSoft),
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset, y = 4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(thumbColor),
            )
        }
    }
}

@Composable
fun AppStepper(
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    canDecrease: Boolean,
    canIncrease: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .height(54.dp)
            .clip(CircleShape)
            .background(colors.surfaceSoft)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(
            text = "−",
            enabled = canDecrease,
            onClick = onDecrease,
            contentDescription = stringResource(R.string.aria_concurrency_down),
        )
        Text(
            text = value.toString(),
            color = colors.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.Center,
        )
        StepperButton(
            text = "+",
            enabled = canIncrease,
            onClick = onIncrease,
            contentDescription = stringResource(R.string.aria_concurrency_up),
        )
    }
}

@Composable
private fun StepperButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = colors.muted.copy(alpha = if (enabled) 1f else 0.45f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun <T> AppDropdown(
    value: T,
    options: List<DropdownOption<T>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    dropdownContentDescription: String? = null,
) {
    val colors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == value }?.label.orEmpty()

    Box(modifier) {
        Row(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(50))
                .background(colors.surfaceRaised)
                .border(1.dp, colors.border, RoundedCornerShape(50))
                .clickable { expanded = true }
                .then(
                    if (dropdownContentDescription != null) {
                        Modifier.semantics { contentDescription = dropdownContentDescription }
                    } else {
                        Modifier
                    },
                )
                .padding(start = 14.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedLabel,
                color = colors.onBackground,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = colors.muted,
                modifier = Modifier.size(18.dp),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            color = colors.onBackground,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    onClick = {
                        onSelect(option.value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    shape: Shape = RoundedCornerShape(16.dp),
    minHeight: Dp = 52.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    textAlign: TextAlign? = null,
    imeAction: ImeAction = ImeAction.Default,
) {
    val colors = AppTheme.colors
    val resolvedStyle = textStyle.copy(
        color = colors.onBackground,
        textAlign = textAlign ?: TextAlign.Start,
    )
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        textStyle = resolvedStyle,
        cursorBrush = SolidColor(colors.onBackground),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .clip(shape)
                    .background(colors.surfaceRaised)
                    .border(1.dp, colors.border, shape)
                    .padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = colors.subtle,
                            style = resolvedStyle,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                }
                if (trailing != null) {
                    Spacer(Modifier.width(8.dp))
                    trailing()
                }
            }
        },
    )
}

@Composable
fun LanguageToggle(
    isArabic: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val thumbOffset by animateDpAsState(
        targetValue = if (isArabic) 80.dp else 4.dp,
        animationSpec = tween(300),
        label = "languageThumb",
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .width(160.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(colors.surfaceSoft)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset, y = 4.dp)
                    .width(76.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(colors.onBackground),
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.language_english),
                        color = if (!isArabic) colors.background else colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.language_arabic),
                        color = if (isArabic) colors.background else colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun AppProgressBar(
    percent: Float,
    paused: Boolean,
    complete: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val fraction = percent.coerceIn(0f, 100f) / 100f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (complete) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(colors.onBackground),
            )
        } else {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .weight(fraction)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(colors.onBackground.copy(alpha = if (paused) 0.4f else 1f)),
                )
            }
            if (fraction < 1f) {
                Box(
                    modifier = Modifier
                        .weight(1f - fraction)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(colors.surfaceSoft),
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 2.dp)
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(colors.onBackground),
                    )
                }
            }
        }
    }
}

@Composable
fun OptionRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(colors.surfaceRaised)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckboxMark(checked)
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            color = colors.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
