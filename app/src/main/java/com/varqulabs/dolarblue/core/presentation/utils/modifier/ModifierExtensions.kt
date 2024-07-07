package com.varqulabs.dolarblue.core.presentation.utils.modifier

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.delay

// Public, exposed to apply in Our Composables, with the ripple effect
fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    var isEnabled by remember { mutableStateOf(enabled) }

    LaunchedEffect(isEnabled) {
        if (!isEnabled) delay(450L)
        isEnabled = true
    }

    this@clickableSingle.clickable(
        enabled = isEnabled,
        onClickLabel = onClickLabel,
        onClick = { onClick(); isEnabled = false },
        role = role,
        indication = rememberRipple(),
        interactionSource = remember { MutableInteractionSource() }
    )
}

// Public, exposed to apply in Our Composables, but without the ripple effect
fun Modifier.clickableSingleWithOutRipple(action: () -> Unit): Modifier = composed {
    this@clickableSingleWithOutRipple.clickableSingle(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) { action() }
}

// Private to be used in the Modifier.clickableSingleWithOutRipple
private fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    interactionSource: MutableInteractionSource,
    indication: Indication?,
    onClick: () -> Unit,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
        properties["indication"] = indication
        properties["interactionSource"] = interactionSource
    }
) {
    var isEnabled by remember { mutableStateOf(enabled) }

    LaunchedEffect(isEnabled) {
        if (!isEnabled) delay(450L)
        isEnabled = true
    }

    this@clickableSingle.clickable(
        enabled = isEnabled,
        onClickLabel = onClickLabel,
        onClick = { onClick(); isEnabled = false },
        role = role,
        indication = indication,
        interactionSource = interactionSource
    )
}