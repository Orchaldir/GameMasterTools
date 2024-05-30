package at.orchaldir.gm.utils.renderer

import kotlinx.serialization.Serializable

@Serializable
sealed class RenderColor
data class NamedColor(val color: String) : RenderColor()
data class RGB(val red: UByte, val green: UByte, val blue: UByte) : RenderColor()