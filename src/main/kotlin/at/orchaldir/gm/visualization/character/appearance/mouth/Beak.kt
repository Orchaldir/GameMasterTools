package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.beard.visualizeBeard

fun visualizeBeak(state: CharacterRenderState, beak: Beak) = when (beak.shape) {
    BeakShape.Crow -> visualizeCrowBeak(state, beak)
    BeakShape.Duck -> doNothing()
    BeakShape.Hawk -> doNothing()
    BeakShape.Owl -> doNothing()
    BeakShape.Parrot -> doNothing()
}

private fun visualizeCrowBeak(state: CharacterRenderState, beak: Beak) {

}