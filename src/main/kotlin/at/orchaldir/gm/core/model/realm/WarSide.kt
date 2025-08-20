package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.titlecaseFirstChar
import kotlinx.serialization.Serializable

@Serializable
data class WarSide(
    val color: Color,
    val name: Name? = null,
) {

    companion object {
        fun init(color: Color, name: String) = WarSide(color, Name.init(name))
    }

}