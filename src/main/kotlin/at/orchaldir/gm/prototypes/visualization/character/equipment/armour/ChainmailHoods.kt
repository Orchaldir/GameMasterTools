package at.orchaldir.gm.prototypes.visualization.character.equipment.armour

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.ChainmailHood
import at.orchaldir.gm.core.model.item.equipment.style.HoodBodyShape
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.prototypes.visualization.mockMaterial
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(Storage(mockMaterial(Color.Silver))),
        "chainmail-hoods.svg",
        CHARACTER_CONFIG,
        addNames(HoodBodyShape.entries + null),
        addNames(BodyShape.entries),
    ) { distance, bodyShape, hoodShape ->
        val armour = Helmet(ChainmailHood(hoodShape))

        Pair(createAppearance(distance, bodyShape), from(armour))
    }
}

private fun createAppearance(height: Distance, bodyShape: BodyShape) =
    HumanoidBody(
        Body(bodyShape),
        Head(ears = NormalEars(), eyes = TwoEyes(), mouth = NormalMouth()),
        height,
    )