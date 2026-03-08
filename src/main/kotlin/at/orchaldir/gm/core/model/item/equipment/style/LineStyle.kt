package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromCord
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val WITHOUT_ORNAMENT_LINE = LineStyleType.entries - LineStyleType.Ornament

enum class LineStyleType {
    Chain,
    Cord,
    Ornament,
    Wire,
}

@Serializable
sealed class LineStyle : MadeFromParts {

    fun getType() = when (this) {
        is Chain -> LineStyleType.Chain
        is Cord -> LineStyleType.Cord
        is OrnamentLine -> LineStyleType.Ornament
        is Wire -> LineStyleType.Wire
    }

    fun getSizeOfSub() = when (this) {
        is Chain -> thickness
        is Cord -> thickness
        is OrnamentLine -> size
        is Wire -> thickness
    }

    override fun parts() = when (this) {
        is Chain -> listOf(main)
        is Cord -> listOf(main)
        is OrnamentLine -> ornament.parts()
        is Wire -> listOf(main)
    }
}

@Serializable
@SerialName("Chain")
data class Chain(
    val thickness: Size = Size.Medium,
    val main: MadeFromMetal = MadeFromMetal(),
) : LineStyle()

@Serializable
@SerialName("Cord")
data class Cord(
    val main: MadeFromCord,
    val thickness: Size = Size.Medium,
) : LineStyle()

@Serializable
@SerialName("Ornament")
data class OrnamentLine(
    val ornament: Ornament,
    val size: Size = Size.Medium,
) : LineStyle()

@Serializable
@SerialName("Wire")
data class Wire(
    val thickness: Size = Size.Medium,
    val main: MadeFromMetal = MadeFromMetal(),
) : LineStyle()

