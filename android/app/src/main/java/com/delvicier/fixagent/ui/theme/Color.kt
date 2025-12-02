package com.delvicier.fixagent.ui.theme

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.Brush

val BrandBlue0 = Color(0xFF0378FE)
val BrandPurple0 = Color(0xFF521FEF)

object MyColors {
    val BrandBlue: Color = Color(0xFF18216c)
    val BrandPurple: Color = Color(0xFF016394)
}

val PrimaryGradientBrush = Brush.horizontalGradient(
    colors = listOf(MyColors.BrandBlue, MyColors.BrandPurple)
)
val BrandBlue = MyColors.BrandPurple


val Light_OnBorder = Color(0xFFfafafa)
val Light_Border = Color(0xFFe2e2e2)
val Light_Background = Color(0xFFfefefe)
val Light_Surface = Color(0xFFfefefe)
val Light_OnBackground = Color(0xFF1A1A1A)
val Light_OnSurface = Color(0xFF1A1A1A)
val Light_Primary = Color.Black

val Light_inversePrimary = Color.Black
val Light_OnSecondary = Color.Black

val Light_OnPrimary = Color.White
val Light_SecondaryContainer = Color(0xFFEFEFEF)
val Light_Tertiary = Color(0xFFFFFFFF)
val Light_OnTertiary = Color(0xFFfefefe)


val Dark_OnBorder = Color(0xFF282828)
val Dark_Border = Color(0xFF282828)
val Dark_Background = Color(0xFF171717)
val Dark_Surface = Color(0xFF1E1E1E)
val Dark_OnBackground = Color(0xFFE0E0E0)
val Dark_OnSurface = Color(0xFFE0E0E0)
val Dark_Primary = Color.White

val Dark_inversePrimary = MyColors.BrandBlue
val Dark_OnSecondary = MyColors.BrandPurple

val Dark_OnPrimary = Color(0xFF002F68)
val Dark_SecondaryContainer = Color(0xFF2C2C2C)

val Dark_Tertiary = Color(0xFF191919)

val Dark_OnTertiary = Color.Black