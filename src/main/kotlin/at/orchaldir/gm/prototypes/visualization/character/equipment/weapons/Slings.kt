package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Sling
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.HorizontalStripesLookup
import at.orchaldir.gm.core.model.util.render.SolidLookup
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.THREE_QUARTER

fun main() {
    val cords = listOf(
        Color.Black,
        Color.SaddleBrown,
        Color.Gray,
    )
    val cradles = listOf(
        Pair("Blue", SolidLookup(Color.Blue)),
        Pair("Green", SolidLookup(Color.Green)),
        Pair("Stripped", HorizontalStripesLookup(Color.Red, Color.Yellow)),
    )

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "slings.svg",
        CHARACTER_CONFIG,
        cradles,
        addNames( cords),
    ) { distance, cord, cradle ->
        val polearm = Sling(
            Rope(ColorSchemeItemPart(cord)),
            FillLookupItemPart(MaterialId(0), cradle),
        )
        Pair(createAppearance(distance), from(polearm))
    }
}
