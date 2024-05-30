package at.orchaldir.gm.utils.math

import kotlin.math.roundToInt

data class Size2d(val width: Int, val height: Int) {

    operator fun div(value: Float) = Size2d((width / value).roundToInt(), (height / value).roundToInt())

}
