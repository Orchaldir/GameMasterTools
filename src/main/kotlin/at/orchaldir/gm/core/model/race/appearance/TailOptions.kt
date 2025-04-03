package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.TailColorType
import at.orchaldir.gm.core.model.character.appearance.tail.TailsLayout
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

val DEFAULT_SIMPLE_TAIL_COLOR = Color.SaddleBrown

@Serializable
data class SimpleTailOptions(
    val colorType: TailColorType = TailColorType.Overwrite,
    val colors: OneOf<Color> = OneOf(DEFAULT_SIMPLE_TAIL_COLOR),
)

@Serializable
data class TailOptions(
    val layouts: OneOf<TailsLayout> = OneOf(TailsLayout.None),
    val simpleShapes: OneOf<SimpleTailShape> = OneOf(SimpleTailShape.Cat),
    val simpleOptions: Map<SimpleTailShape, SimpleTailOptions> = emptyMap(),
) {

    fun getSimpleTailOptions(shape: SimpleTailShape) = simpleOptions[shape] ?: error("Unsupported shape $shape!")
}
