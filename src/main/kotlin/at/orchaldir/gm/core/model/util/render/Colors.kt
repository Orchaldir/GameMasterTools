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

    fun count() = when (this) {
        is OneColor -> 1
        is TwoColors -> 2
        UndefinedColors -> 0
    }

    fun color0(): Color? = null
    fun color1(): Color? = null

}

@Serializable
@SerialName("1")
data class OneColor(
    val color: Color,
) : Colors {

    override fun name() = color.name
    override fun color0() = color

}

@Serializable
@SerialName("2")
data class TwoColors private constructor(
    val color0: Color,
    val color1: Color,
) : Colors {

    companion object {

        fun init(color0: Color, color1: Color) = if (color0 <= color1) {
            TwoColors(color0, color1)
        } else {
            TwoColors(color1, color0)
        }

    }

    override fun name() = "$color0 & $color1"
    override fun color0() = color0
    override fun color1() = color1

}

@Serializable
@SerialName("Undefined")
data object UndefinedColors : Colors {

    override fun name() = "Undefined"

}
