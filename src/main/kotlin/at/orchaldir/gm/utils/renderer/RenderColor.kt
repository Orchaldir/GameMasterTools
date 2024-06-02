package at.orchaldir.gm.utils.renderer

import kotlinx.serialization.Serializable

@Serializable
sealed class RenderColor
data class NamedColor(val color: String) : RenderColor()
data class RGB(val red: UByte, val green: UByte, val blue: UByte) : RenderColor() {

    constructor(red: Int, green: Int, blue: Int) : this(red.toUByte(), green.toUByte(), blue.toUByte())

    fun toHexCode() = String.format("#%02x%02x%02x", red.toInt(), green.toInt(), blue.toInt())

}