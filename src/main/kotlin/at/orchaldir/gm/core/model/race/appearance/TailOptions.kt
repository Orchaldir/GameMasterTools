package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape.Cat
import at.orchaldir.gm.core.model.character.appearance.tail.TailsLayout
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class TailOptions(
    val layouts: OneOf<TailsLayout> = OneOf(TailsLayout.None),
    val simpleShapes: OneOf<SimpleTailShape> = OneOf(Cat),
    val simpleOptions: Map<SimpleTailShape, FeatureColorOptions> = mapOf(Cat to FeatureColorOptions()),
) {
    fun contains(material: MaterialId) = simpleOptions.any { it.value.contains(material) }

    fun getFeatureColorOptions(shape: SimpleTailShape) = simpleOptions[shape] ?: error("Unsupported shape $shape!")
}
