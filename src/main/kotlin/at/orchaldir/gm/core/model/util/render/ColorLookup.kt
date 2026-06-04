package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.selector.economy.getMaterialColor
import at.orchaldir.gm.utils.COLOR_INDEX
import at.orchaldir.gm.utils.RepeatableNumberGenerator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ColorLookupType {
    Fixed,
    Random,
    Material,
    Schema0,
    Schema1,
}

@Serializable
sealed interface ColorLookup {

    fun type() = when (this) {
        is FixedColor -> ColorLookupType.Fixed
        is RandomColor -> ColorLookupType.Random
        LookupMaterial -> ColorLookupType.Material
        LookupSchema0 -> ColorLookupType.Schema0
        LookupSchema1 -> ColorLookupType.Schema1
    }

    fun requiredSchemaColors() = when (this) {
        is FixedColor, is RandomColor, LookupMaterial -> 0
        LookupSchema0 -> 1
        LookupSchema1 -> 2
    }

    fun lookup(colors: Colors) = when (this) {
        is FixedColor -> color
        is RandomColor -> this.colors.getMostCommon()
        LookupMaterial -> null
        LookupSchema0 -> colors.color0()
        LookupSchema1 -> colors.color0()
    }

    fun lookup(
        state: State,
        numberGenerator: RepeatableNumberGenerator,
        colors: Colors,
        material: MaterialId,
    ) = when (this) {
        is RandomColor -> state.rarityGenerator.generate(this.colors, numberGenerator, COLOR_INDEX)
        else -> lookup(colors)
    } ?: state.getMaterialColor(numberGenerator, material)

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
@SerialName("Random")
data class RandomColor(
    val colors: OneOf<Color>,
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
