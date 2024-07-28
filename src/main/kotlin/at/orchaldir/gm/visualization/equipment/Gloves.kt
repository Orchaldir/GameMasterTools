package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Gloves
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.visualizeHands

fun visualizeGloves(
    state: RenderState,
    body: Body,
    gloves: Gloves,
) {
    val options = FillAndBorder(gloves.fill.toRender(), state.config.line)

    /*
    when (gloves.style) {
        GloveStyle.Hand -> doNothing()
        GloveStyle.Half -> TODO()
        GloveStyle.Full -> TODO()
    }

     */

    visualizeHands(state, body, options)
}
