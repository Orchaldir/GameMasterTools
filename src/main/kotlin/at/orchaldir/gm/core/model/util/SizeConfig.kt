package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.math.Factor

data class SizeConfig<T>(val small: T, val medium: T, val large: T) {

    companion object {

        fun withFactor(small: Float, medium: Float, large: Float) =
            SizeConfig(
                Factor.fromNumber(small),
                Factor.fromNumber(medium),
                Factor.fromNumber(large)
            )

        fun fromPercentages(small: Int, medium: Int, large: Int) =
            SizeConfig(
                Factor.fromPercentage(small),
                Factor.fromPercentage(medium),
                Factor.fromPercentage(large)
            )

        fun fromPermilles(small: Int, medium: Int, large: Int) =
            SizeConfig(
                Factor.fromPermille(small),
                Factor.fromPermille(medium),
                Factor.fromPermille(large)
            )

    }

    fun convert(size: Size) = when (size) {
        Size.Small -> small
        Size.Medium -> medium
        Size.Large -> large
    }

}