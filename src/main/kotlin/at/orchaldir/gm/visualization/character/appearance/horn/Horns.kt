package at.orchaldir.gm.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.WING_LAYER

data class HornConfig(
    val y: Factor,
    val gemsbok: ComplexHorn,
    val mouflon: ComplexHorn,
    val saiga: ComplexHorn,
    val waterBuffalo: ComplexHorn,
) {
    fun getLayer(renderFront: Boolean) = if (renderFront) {
        -WING_LAYER
    } else {
        WING_LAYER
    }
}

fun visualizeHorns(
    state: CharacterRenderState<Head>,
    horns: Horns,
    skin: Skin,
    hair: Hair,
) = when (horns) {
    NoHorns -> doNothing()
    is TwoHorns -> {
        visualizeHorn(state, horns.horn, Side.Left, skin, hair)
        visualizeHorn(state, horns.horn, Side.Right, skin, hair)
    }

    is DifferentHorns -> {
        visualizeHorn(state, horns.left, Side.Left, skin, hair)
        visualizeHorn(state, horns.right, Side.Right, skin, hair)
    }

    is CrownOfHorns -> visualizeCrownOfHorns(state, horns, skin, hair)
}
