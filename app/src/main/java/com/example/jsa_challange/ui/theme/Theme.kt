package com.example.jsa_challange.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.nio.file.attribute.AclEntry

private val DarkColorPalette = darkColorScheme(
        primary = Color(0xF000835A)
)

private val LightColorPalette = lightColorScheme(
        primary = Color(0xF058FFCB)

        /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


@Composable
fun JSA_ChallangeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val dynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = if (dynamic){
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }else{
        if (darkTheme) {
            DarkColorPalette
        } else {
            LightColorPalette
        }
    }
    androidx.compose.material3.MaterialTheme(
            colorScheme = colorScheme,
            content = content
    )
}