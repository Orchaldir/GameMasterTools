package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage

data class SizeConfig<T>(val small: T, val medium: T, val large: T) {

    companion object {

        fun withFactor(small: Float, medium: Float, large: Float) =
            SizeConfig(fromNumber(small), fromNumber(medium), fromNumber(large))

        fun fromPercentages(small: Int, medium: Int, large: Int) =
            SizeConfig(fromPercentage(small), fromPercentage(medium), fromPercentage(large))

    }

    fun convert(size: Size) = when (size) {
        Size.Small -> small
        Size.Medium -> medium
        Size.Large -> large
    }

}
