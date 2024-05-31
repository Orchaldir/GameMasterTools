package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.appearance.Size

data class SizeConfig(val small: Float, val medium: Float, val large: Float) {

    fun convert(size: Size) = when (size) {
        Size.Small -> small
        Size.Medium -> medium
        Size.Large -> large
    }

}
