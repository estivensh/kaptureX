package io.github.estivensh.extensions

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.noClickable() = then(Modifier.clickable(enabled = false) {})