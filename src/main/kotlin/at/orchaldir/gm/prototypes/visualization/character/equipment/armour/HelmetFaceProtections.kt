package at.orchaldir.gm.prototypes.visualization.character.equipment.armour

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.EyeHoleShape
import at.orchaldir.gm.core.model.item.equipment.style.FaceProtection
import at.orchaldir.gm.core.model.item.equipment.style.FaceProtectionShape
import at.orchaldir.gm.core.model.item.equipment.style.SkullCap
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Silver))),
        "helmet-face-protections.svg",
        CHARACTER_CONFIG,
        addNames(EyeHoleShape.entries),
        addNames(FaceProtectionShape.entries),
    ) { distance, shape, hole ->
        val protection = FaceProtection(shape, hole, ColorSchemeItemPart())
        val helmet = Helmet(SkullCap(front = protection))
        Pair(createAppearance(distance), from(helmet))
    }
}

private fun createAppearance(distance: Distance) =
    HeadOnly(
        Head(ears = NormalEars(), eyes = TwoEyes(), mouth = NormalMouth()),
        distance,
    )