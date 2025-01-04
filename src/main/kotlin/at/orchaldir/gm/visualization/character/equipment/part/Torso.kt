package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.style.NecklineStyle
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.createTorso
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeTorso(
    state: CharacterRenderState,
    options: RenderOptions,
    body: Body,
    style: NecklineStyle,
    layerIndex: Int = EQUIPMENT_LAYER,
) {
    val builder = createTorso(state, body, style.addTop())
    addNeckline(state, body, builder, style)

    renderBuilder(state, builder, options, layerIndex)
}