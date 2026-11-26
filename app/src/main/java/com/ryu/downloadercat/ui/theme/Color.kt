package com.ryu.downloadercat.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * App-specific color tokens. The UI is black, white and grays only, so we do not
 * use the Material accent colors directly.
 */
@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceCard: Color,
    val surfaceRaised: Color,
    val surfaceSoft: Color,
    val onBackground: Color,
    val muted: Color,
    val subtle: Color,
    val border: Color,
    val borderSubtle: Color,
    val borderStrong: Color,
    val focus: Color,
    val ring: Color,
    val dashedBackground: Color,
    val backdrop: Color,
    val isDark: Boolean,
)

val DarkColors = AppColors(
    background = Color(0xFF000000),
    surface = Color(0xFF101013),
    surfaceCard = Color(0xFF151519),
    surfaceRaised = Color(0xFF1D1D22),
    surfaceSoft = Color(0xFF26262C),
    onBackground = Color(0xFFFFFFFF),
    muted = Color(0xFFA1A1AA),
    subtle = Color(0xFF787882),
    border = Color(0xFF28282F),
    borderSubtle = Color(0xFF1C1C21),
    borderStrong = Color(0xFF4B4B55),
    focus = Color(0xFFFFFFFF),
    ring = Color(0x33FFFFFF),
    dashedBackground = Color(0xFF0B0B0D),
    backdrop = Color(0xFF070709),
    isDark = true,
)

val LightColors = AppColors(
    background = Color(0xFFF8F8FA),
    surface = Color(0xFFFFFFFF),
    surfaceCard = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFF1F1F5),
    surfaceSoft = Color(0xFFE5E5EC),
    onBackground = Color(0xFF09090B),
    muted = Color(0xFF52525B),
    subtle = Color(0xFF71717A),
    border = Color(0xFFE2E2EA),
    borderSubtle = Color(0xFFECECF2),
    borderStrong = Color(0xFF9E9EAB),
    focus = Color(0xFF09090B),
    ring = Color(0x2409090B),
    dashedBackground = Color(0xFFF2F2F6),
    backdrop = Color(0xFFECECF0),
    isDark = false,
)

/**
 * Material 3 still needs a ColorScheme for components such as menus and text
 * fields. Every role is mapped to the black/white token set so no purple leaks in.
 */
internal val DarkColorScheme = darkColorScheme(
    primary = DarkColors.onBackground,
    onPrimary = DarkColors.background,
    primaryContainer = DarkColors.surfaceSoft,
    onPrimaryContainer = DarkColors.onBackground,
    inversePrimary = DarkColors.onBackground,
    secondary = DarkColors.surfaceRaised,
    onSecondary = DarkColors.onBackground,
    secondaryContainer = DarkColors.surfaceSoft,
    onSecondaryContainer = DarkColors.onBackground,
    tertiary = DarkColors.surfaceRaised,
    onTertiary = DarkColors.onBackground,
    tertiaryContainer = DarkColors.surfaceSoft,
    onTertiaryContainer = DarkColors.onBackground,
    background = DarkColors.background,
    onBackground = DarkColors.onBackground,
    surface = DarkColors.surface,
    onSurface = DarkColors.onBackground,
    surfaceVariant = DarkColors.surfaceRaised,
    onSurfaceVariant = DarkColors.muted,
    surfaceTint = Color.Transparent,
    inverseSurface = DarkColors.onBackground,
    inverseOnSurface = DarkColors.background,
    error = DarkColors.onBackground,
    onError = DarkColors.background,
    errorContainer = DarkColors.surfaceSoft,
    onErrorContainer = DarkColors.onBackground,
    outline = DarkColors.border,
    outlineVariant = DarkColors.borderSubtle,
    scrim = Color.Black,
    surfaceBright = DarkColors.surfaceRaised,
    surfaceDim = DarkColors.surface,
    surfaceContainer = DarkColors.surface,
    surfaceContainerHigh = DarkColors.surfaceRaised,
    surfaceContainerHighest = DarkColors.surfaceSoft,
    surfaceContainerLow = DarkColors.surface,
    surfaceContainerLowest = DarkColors.background,
)

internal val LightColorScheme = lightColorScheme(
    primary = LightColors.onBackground,
    onPrimary = LightColors.background,
    primaryContainer = LightColors.surfaceSoft,
    onPrimaryContainer = LightColors.onBackground,
    inversePrimary = LightColors.onBackground,
    secondary = LightColors.surfaceRaised,
    onSecondary = LightColors.onBackground,
    secondaryContainer = LightColors.surfaceSoft,
    onSecondaryContainer = LightColors.onBackground,
    tertiary = LightColors.surfaceRaised,
    onTertiary = LightColors.onBackground,
    tertiaryContainer = LightColors.surfaceSoft,
    onTertiaryContainer = LightColors.onBackground,
    background = LightColors.background,
    onBackground = LightColors.onBackground,
    surface = LightColors.surface,
    onSurface = LightColors.onBackground,
    surfaceVariant = LightColors.surfaceRaised,
    onSurfaceVariant = LightColors.muted,
    surfaceTint = Color.Transparent,
    inverseSurface = LightColors.onBackground,
    inverseOnSurface = LightColors.background,
    error = LightColors.onBackground,
    onError = LightColors.background,
    errorContainer = LightColors.surfaceSoft,
    onErrorContainer = LightColors.onBackground,
    outline = LightColors.border,
    outlineVariant = LightColors.borderSubtle,
    scrim = Color.Black,
    surfaceBright = LightColors.surfaceRaised,
    surfaceDim = LightColors.surface,
    surfaceContainer = LightColors.surface,
    surfaceContainerHigh = LightColors.surfaceRaised,
    surfaceContainerHighest = LightColors.surfaceSoft,
    surfaceContainerLow = LightColors.surface,
    surfaceContainerLowest = LightColors.background,
)
