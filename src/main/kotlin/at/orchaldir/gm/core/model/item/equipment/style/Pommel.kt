package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.Serializable

@Serializable
data class Pommel(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : MadeFromParts {

    override fun parts() = ornament.parts()

}
