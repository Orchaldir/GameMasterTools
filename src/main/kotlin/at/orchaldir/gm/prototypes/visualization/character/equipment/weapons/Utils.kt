package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.style.BoundHeadHead
import at.orchaldir.gm.core.model.item.equipment.style.Langets
import at.orchaldir.gm.core.model.item.equipment.style.NoHeadFixation
import at.orchaldir.gm.core.model.item.equipment.style.SocketedHeadHead
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.unit.Distance

val FIXATION = listOf(
    Pair("None", NoHeadFixation),
    Pair("Bound", BoundHeadHead(part = ColorSchemeItemPart(Color.Red))),
    Pair("Langets", Langets()),
    Pair("Socketed", SocketedHeadHead()),
)

fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )