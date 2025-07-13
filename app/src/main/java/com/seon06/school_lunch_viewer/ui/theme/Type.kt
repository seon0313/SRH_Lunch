package com.seon06.school_lunch_viewer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.seon06.school_lunch_viewer.R

val paperlogy1 = FontFamily(
    Font(R.font.paperlogy1, FontWeight.Normal, FontStyle.Normal),
)
val paperlogy2 = FontFamily(
    Font(R.font.paperlogy2, FontWeight.Normal, FontStyle.Normal),
)
val paperlogy3 = FontFamily(
    Font(R.font.paperlogy3, FontWeight.Normal, FontStyle.Normal),
)
val paperlogy4 = FontFamily(
    Font(R.font.paperlogy4, FontWeight.Normal, FontStyle.Normal),
)
val paperlogy5 = FontFamily(
    Font(R.font.paperlogy5, FontWeight.Normal, FontStyle.Normal),
)
val paperlogy6 = FontFamily(
    Font(R.font.paperlogy6, FontWeight.Normal, FontStyle.Normal),
)
val paperlogy7 = FontFamily(
    Font(R.font.paperlogy7, FontWeight.Normal, FontStyle.Normal),
)
val paperlogy8 = FontFamily(
    Font(R.font.paperlogy8, FontWeight.Normal, FontStyle.Normal),
)
val paperlogy9 = FontFamily(
    Font(R.font.paperlogy9, FontWeight.Normal, FontStyle.Normal),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = paperlogy5,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        /*lineHeight = 24.sp,
        letterSpacing = 0.5.sp*/
    ),
    // Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = paperlogy9,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = paperlogy7,
        fontWeight = FontWeight.Normal,
    ),
    labelMedium = TextStyle(
        fontFamily = paperlogy8,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )/*
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)