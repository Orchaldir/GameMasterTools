package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private val DEFAULT_COLOR = Color.Pink

enum class ColorLookupType {
    Fixed,
    Material,
    Schema0,
    Schema1,
}

@Serializable
sealed interface ColorLookup {

    fun type() = when (this) {
        is FixedColor -> ColorLookupType.Fixed
        LookupMaterial -> ColorLookupType.Material
        LookupSchema0 -> ColorLookupType.Schema0
        LookupSchema1 -> ColorLookupType.Schema1
    }

    fun requiredSchemaColors() = when (this) {
        is FixedColor, LookupMaterial -> 0
        LookupSchema0 -> 1
        LookupSchema1 -> 2
    }

    fun lookup(colors: Colors) = when (this) {
        is FixedColor -> color
        LookupMaterial -> null
        LookupSchema0 -> colors.color0()
        LookupSchema1 -> colors.color0()
    }

    fun lookup(state: State, colors: Colors, material: MaterialId) = lookup(colors)
        ?: state.getMaterialStorage().get(material)?.color ?: Color.Pink

    fun getOtherColors() = if (this is FixedColor) {
        Color.entries - color
    } else {
        Color.entries
    }

}

@Serializable
@SerialName("Fixed")
data class FixedColor(
    val color: Color,
) : ColorLookup

@Serializable
@SerialName("Material")
data object LookupMaterial : ColorLookup

@Serializable
@SerialName("0")
data object LookupSchema0 : ColorLookup

@Serializable
@SerialName("1")
data object LookupSchema1 : ColorLookup
