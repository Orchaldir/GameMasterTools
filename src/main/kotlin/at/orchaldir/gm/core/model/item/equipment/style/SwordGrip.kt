package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

@Serializable
data class SwordGrip(
    val part: FillLookupItemPart = FillLookupItemPart(),
    val shape: SwordGripShape = SwordGripShape.Straight,
    val size: Size = Size.Medium,
) : MadeFromParts {

    override fun parts() = listOf(part)

}
