package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Polearm
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.Segment
import at.orchaldir.gm.core.model.util.part.SegmentShape
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.HorizontalStripesLookup
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val segments = Segments(
        Segment(
            fromPercentage(10),
            fromPercentage(100),
            shape = SegmentShape.Sphere,
        )
    )
    val heads = listOf(
        Pair("None", NoPolearmHead),
        Pair("Rounded", RoundedPolearmHead),
        Pair("Sharpened", SharpenedPolearmHead),
        Pair("Segments", PolearmHeadWithSegments(segments)),
        Pair("Spear", PolearmHeadWithSpearHead()),
    )
    val stripes = HorizontalStripesLookup(Color.Red, Color.Gold)
    val shafts = listOf(
        Pair("Wood", SimpleShaft(FillLookupItemPart(Color.SaddleBrown))),
        Pair("Stripped", SimpleShaft(FillLookupItemPart(fill = stripes))),
    )

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "polearms.svg",
        CHARACTER_CONFIG,
        shafts,
        heads,
    ) { distance, head, shaft ->
        val polearm = Polearm(
            head,
            shaft
        )
        Pair(createAppearance(distance), from(polearm))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )