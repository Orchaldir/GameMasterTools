package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.createTorso
import at.orchaldir.gm.visualization.renderBuilder

fun visualizeTorso(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    style: NecklineStyle,
    layerIndex: Int = EQUIPMENT_LAYER,
) {
    val builder = createTorso(state, style.addTop())
    addNeckline(state, builder, style)

    renderBuilder(state.renderer, builder, options, layerIndex)
}