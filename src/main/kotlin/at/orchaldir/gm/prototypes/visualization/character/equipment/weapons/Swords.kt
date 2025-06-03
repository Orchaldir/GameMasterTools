package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.OneHandedSword
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.item.equipment.style.SwordGripShape.*
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val straightHilt = SimpleSwordHilt(
        NoSwordGuard,
        createGrip(Straight),
        NoPommel,
    )
    val boundHilt = SimpleSwordHilt(
        SimpleSwordGuard(part = FillLookupItemPart(Color.Gold)),
        BoundSwordGrip(6),
    )
    val hilts = listOf(
        Pair("Straight", straightHilt),
        Pair("Oval", SimpleSwordHilt(grip = createGrip(Oval))),
        Pair("Waisted", SimpleSwordHilt(grip = createGrip(Waisted))),
        Pair("Bound", boundHilt),
    )

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "swords.svg",
        CHARACTER_CONFIG,
        hilts,
        addNames(BladeShape.entries),
    ) { distance, shape, hilt ->
        val sword = OneHandedSword(
            SimpleBlade(DEFAULT_1H_BLADE_LENGTH, shape = shape),
            hilt,
        )
        Pair(createAppearance(distance), from(sword))
    }
}

private fun createGrip(shape: SwordGripShape) = SimpleSwordGrip(shape, FillLookupItemPart(Color.Black))

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )