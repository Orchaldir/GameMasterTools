package at.orchaldir.gm.core.model.util.render

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ColorsType {
    One,
    Two,
    Undefined,
}

@Serializable
sealed interface Colors {

    fun name(): String

    fun type() = when (this) {
        is OneColor -> ColorsType.One
        is TwoColors -> ColorsType.Two
        UndefinedColors -> ColorsType.Undefined
    }

}

@Serializable
@SerialName("1")
data class OneColor(
    val color: Color,
) : Colors {

    override fun name() = color.name

}

@Serializable
@SerialName("2")
data class TwoColors(
    val color0: Color,
    val color1: Color,
) : Colors {

    override fun name() = "$color0 & $color1"

}

@Serializable
@SerialName("Undefined")
data object UndefinedColors : Colors {

    override fun name() = "Undefined"

}
