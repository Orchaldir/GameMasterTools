package at.orchaldir.gm.core.model.util.render

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private val DEFAULT_COLOR = Color.Pink

enum class ColorLookupType {
    Fixed,
    Schema0,
    Schema1,
}

@Serializable
sealed interface ColorLookup {

    fun type() = when (this) {
        is FixedColor -> ColorLookupType.Fixed
        LookupSchema0 -> ColorLookupType.Schema0
        LookupSchema1 -> ColorLookupType.Schema1
    }

    fun requiredSchemaColors() = when (this) {
        is FixedColor -> 0
        LookupSchema0 -> 1
        LookupSchema1 -> 2
    }

    fun lookup(scheme: ColorScheme) = when (this) {
        is FixedColor -> color
        LookupSchema0 -> scheme.data.color0() ?: DEFAULT_COLOR
        LookupSchema1 -> scheme.data.color0() ?: DEFAULT_COLOR
    }

}

@Serializable
@SerialName("Fixed")
data class FixedColor(
    val color: Color,
) : ColorLookup

@Serializable
@SerialName("0")
data object LookupSchema0 : ColorLookup

@Serializable
@SerialName("1")
data object LookupSchema1 : ColorLookup
