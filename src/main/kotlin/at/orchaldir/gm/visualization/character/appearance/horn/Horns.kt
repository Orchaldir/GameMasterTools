package at.orchaldir.gm.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeHorns(state: CharacterRenderState, horns: Horns) = when (horns) {
    NoHorns -> doNothing()
    is TwoHorns -> {
        visualizeHorn(state, horns.horn, Side.Left)
        visualizeHorn(state, horns.horn, Side.Right)
    }

    is DifferentHorns -> {
        visualizeHorn(state, horns.left, Side.Left)
        visualizeHorn(state, horns.right, Side.Right)
    }

    is CrownOfHorns -> visualizeCrownOfHorns(state, horns)
}
