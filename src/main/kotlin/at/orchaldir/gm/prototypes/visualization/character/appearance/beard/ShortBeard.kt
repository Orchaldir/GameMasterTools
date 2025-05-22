package at.orchaldir.gm.prototypes.visualization.character.appearance.beard

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.Color.SaddleBrown
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "beard-short.svg",
        CHARACTER_CONFIG,
        addNames(MoustacheStyle.entries + null),
        addNames(GoateeStyle.entries + null),
    ) { distance, goatee, moustache ->
        Pair(createAppearance(distance, goatee, moustache), EquipmentMap())
    }
}

private fun createAppearance(height: Distance, goatee: GoateeStyle?, moustache: MoustacheStyle?): HeadOnly {
    val beard = if (goatee != null && moustache != null) {
        GoateeAndMoustache(moustache, goatee)
    } else if (goatee != null) {
        Goatee(goatee)
    } else if (moustache != null) {
        Moustache(moustache)
    } else {
        ShavedBeard
    }

    return HeadOnly(
        Head(
            NormalEars(),
            TwoEyes(),
            NoHair,
            NoHorns,
            NormalMouth(NormalBeard(beard, SaddleBrown))
        ),
        height,
    )
}