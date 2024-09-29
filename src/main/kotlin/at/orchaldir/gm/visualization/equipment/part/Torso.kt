package at.orchaldir.gm.visualization.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.style.NecklineStyle
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.createTorso
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeTorso(
    state: RenderState,
    options: RenderOptions,
    body: Body,
    style: NecklineStyle,
) {
    val builder = createTorso(state, body, style.addTop())
    addNeckline(state, body, builder, style)

    renderBuilder(state, builder, options, EQUIPMENT_LAYER)
}