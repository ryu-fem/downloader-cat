package com.ryu.downloadercat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ryu.downloadercat.R

/**
 * Outfit is bundled only for the wordmark. Body text uses the platform font so
 * Arabic renders correctly without shipping another font family.
 */
val OutfitFamily = FontFamily(
    Font(R.font.outfit_light, FontWeight.Light),
    Font(R.font.outfit_extrabold, FontWeight.ExtraBold),
)

fun appTypography(isArabic: Boolean): Typography {
    val headingTracking = if (isArabic) 0.sp else (-0.9).sp
    val titleTracking = if (isArabic) 0.sp else (-0.4).sp
    val headingLineHeight = if (isArabic) 38.sp else 34.sp

    return Typography(
        headlineLarge = TextStyle(
            fontSize = 30.sp,
            lineHeight = headingLineHeight,
            fontWeight = FontWeight.Bold,
            letterSpacing = headingTracking,
        ),
        headlineMedium = TextStyle(
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = titleTracking,
        ),
        titleLarge = TextStyle(
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = titleTracking,
        ),
        titleMedium = TextStyle(
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Bold,
        ),
        bodyLarge = TextStyle(
            fontSize = 15.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Normal,
        ),
        bodyMedium = TextStyle(
            fontSize = 13.5.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Normal,
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Normal,
        ),
        labelLarge = TextStyle(
            fontSize = 13.5.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        labelMedium = TextStyle(
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        labelSmall = TextStyle(
            fontSize = 10.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Medium,
        ),
    )
}
