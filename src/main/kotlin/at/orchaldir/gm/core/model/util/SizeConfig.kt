package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.math.Factor

data class SizeConfig<T>(val small: T, val medium: T, val large: T) {

    companion object {

        fun withFactor(small: Float, medium: Float, large: Float) =
            SizeConfig(
                Factor.Companion.fromNumber(small),
                Factor.Companion.fromNumber(medium),
                Factor.Companion.fromNumber(large)
            )

        fun fromPercentages(small: Int, medium: Int, large: Int) =
            SizeConfig(
                Factor.Companion.fromPercentage(small),
                Factor.Companion.fromPercentage(medium),
                Factor.Companion.fromPercentage(large)
            )

        fun fromPermilles(small: Int, medium: Int, large: Int) =
            SizeConfig(
                Factor.Companion.fromPermille(small),
                Factor.Companion.fromPermille(medium),
                Factor.Companion.fromPermille(large)
            )

    }

    fun convert(size: Size) = when (size) {
        Size.Small -> small
        Size.Medium -> medium
        Size.Large -> large
    }

}