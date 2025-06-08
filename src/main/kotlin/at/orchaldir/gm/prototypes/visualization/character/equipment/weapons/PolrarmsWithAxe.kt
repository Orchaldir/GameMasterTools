package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.Polearm
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderEquipmentDataTable
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val table = listOf(
        listOf(
            create(SingleBitAxeHead(CrescentAxeBlade(CrescentAxeShape.QuarterCircle))),
            create(SingleBitAxeHead(CrescentAxeBlade(CrescentAxeShape.HalfCircle))),
            create(SingleBitAxeHead(CrescentAxeBlade(CrescentAxeShape.Octagon))),
        )
    )
    val appearance = HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        Distance.fromCentimeters(180),
    )

    renderEquipmentDataTable(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "polearms-axe.svg",
        CHARACTER_CONFIG,
        appearance,
        table,
    )
}

fun create(axeHead: AxeHead) = Polearm(
    PolearmHeadWithAxeHead(axeHead, Langets()),
    SimpleShaft(FillLookupItemPart(Color.SaddleBrown))
)
