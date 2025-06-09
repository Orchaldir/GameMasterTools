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
import at.orchaldir.gm.core.model.item.equipment.style.BroadAxeBladeShape.Angular
import at.orchaldir.gm.core.model.item.equipment.style.BroadAxeBladeShape.Curved
import at.orchaldir.gm.core.model.item.equipment.style.BroadAxeBladeShape.Straight
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeShape.HalfCircle
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeShape.Octagon
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeShape.QuarterCircle
import at.orchaldir.gm.core.model.util.Size.Large
import at.orchaldir.gm.core.model.util.Size.Medium
import at.orchaldir.gm.core.model.util.Size.Small
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderEquipmentDataTable
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val table = listOf(
        listOf(
            create(SingleBitAxeHead(BroadAxeBlade(Straight, Small, Large))),
            create(SingleBitAxeHead(BroadAxeBlade(Curved, Medium, Medium))),
            create(SingleBitAxeHead(BroadAxeBlade(Angular, Large, Small))),
        ),
        listOf(
            create(DoubleBitAxeHead(SymmetricAxeBlade(QuarterCircle, Small))),
            create(DoubleBitAxeHead(SymmetricAxeBlade(HalfCircle, Medium))),
            create(DoubleBitAxeHead(SymmetricAxeBlade(Octagon, Large))),
        ),
        listOf(
            create(SingleBitAxeHead(DaggerAxeBlade(Small))),
            create(SingleBitAxeHead(DaggerAxeBlade(Medium))),
            create(SingleBitAxeHead(DaggerAxeBlade(Large))),
        ),
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
