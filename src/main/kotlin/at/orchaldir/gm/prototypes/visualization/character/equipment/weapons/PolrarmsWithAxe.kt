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
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeShape.HalfCircle
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeShape.Octagon
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeShape.QuarterCircle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderEquipmentDataTable
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val table = listOf(
        listOf(
            create(SingleBitAxeHead(CrescentAxeBlade(QuarterCircle), Size.Small)),
            create(SingleBitAxeHead(CrescentAxeBlade(HalfCircle), Size.Small)),
            create(SingleBitAxeHead(CrescentAxeBlade(Octagon), Size.Small)),
        ),
        listOf(
            create(DoubleBitAxeHead(CrescentAxeBlade(QuarterCircle), Size.Medium)),
            create(DoubleBitAxeHead(CrescentAxeBlade(HalfCircle), Size.Medium)),
            create(DoubleBitAxeHead(CrescentAxeBlade(Octagon), Size.Medium)),
        ),
        listOf(
            create(DoubleBitAxeHead(CrescentAxeBlade(QuarterCircle), Size.Large)),
            create(DoubleBitAxeHead(CrescentAxeBlade(HalfCircle), Size.Large)),
            create(DoubleBitAxeHead(CrescentAxeBlade(Octagon), Size.Large)),
        ),
        listOf(
            create(SingleBitAxeHead(DaggerAxeBlade(), Size.Small)),
            create(SingleBitAxeHead(DaggerAxeBlade(), Size.Medium)),
            create(SingleBitAxeHead(DaggerAxeBlade(), Size.Large)),
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
