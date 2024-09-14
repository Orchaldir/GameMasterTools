package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Factor

data class SizeConfig<T>(val small: T, val medium: T, val large: T) {

    companion object {

        fun withFactor(small: Float, medium: Float, large: Float) =
            SizeConfig(Factor(small), Factor(medium), Factor(large))

    }

    fun convert(size: Size) = when (size) {
        Size.Small -> small
        Size.Medium -> medium
        Size.Large -> large
    }

}
