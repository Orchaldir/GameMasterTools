package at.orchaldir.gm.prototypes.visualization.character.equipment.armour

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.ChainmailHood
import at.orchaldir.gm.core.model.item.equipment.style.HelmetShape
import at.orchaldir.gm.core.model.item.equipment.style.HelmetStyle
import at.orchaldir.gm.core.model.item.equipment.style.SkullCap
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Color.Yellow
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.prototypes.visualization.mockMaterial
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val style = mutableListOf<Pair<String, HelmetStyle>>(
        Pair("Hood", ChainmailHood()),
    )
    HelmetShape.entries.forEach { shape ->
        style.add(Pair(shape.name, SkullCap(shape)))
    }


    renderCharacterTableWithoutColorScheme(
        State(Storage(mockMaterial(Color.Silver))),
        "helmets-with-hair.svg",
        CHARACTER_CONFIG,
        addNames(ShortHairStyle.entries),
        style,
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), from(Helmet(style)))
    }
}

private fun createAppearance(distance: Distance, style: ShortHairStyle) =
    HeadOnly(
        Head(ears = NormalEars(), eyes = TwoEyes(), hair = NormalHair(ShortHairCut(style), Yellow)),
        distance,
    )