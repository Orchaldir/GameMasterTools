package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Sling
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.HorizontalStripesLookup
import at.orchaldir.gm.core.model.util.render.SolidLookup
import at.orchaldir.gm.core.model.util.render.VerticalStripes
import at.orchaldir.gm.core.model.util.render.VerticalStripesLookup
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.ONE_CM
import at.orchaldir.gm.utils.math.unit.ONE_DM
import at.orchaldir.gm.utils.math.unit.ONE_MM

fun main() {
    val cradles = listOf(
        Pair("Blue", SolidLookup(Color.Blue)),
        Pair("Green", SolidLookup(Color.Green)),
        Pair("Stripped", VerticalStripesLookup(Color.Red, Color.Yellow, fromCentimeters(3))),
    )

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "slings.svg",
        CHARACTER_CONFIG,
        cradles,
        addNames( Size.entries),
    ) { distance, size, cradle ->
        val polearm = Sling(
            size,
            Rope(ColorSchemeItemPart(Color.SaddleBrown)),
            FillLookupItemPart(MaterialId(0), cradle),
        )
        Pair(createAppearance(distance), from(polearm))
    }
}
