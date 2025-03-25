package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.style.FootwearStyle
import at.orchaldir.gm.core.model.item.equipment.style.SocksStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BEHIND_LAYER
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.visualizeFeet

fun visualizeSocks(
    state: CharacterRenderState,
    body: Body,
    socks: Socks,
) {
    val options = state.config.getLineOptions(socks.fill)

    visualizeSockShaft(state, body, socks, options)

    val layer = if (state.renderFront) {
        EQUIPMENT_LAYER
    } else {
        BEHIND_LAYER
    }
    visualizeFeet(state, body, options, layer)
}

private fun visualizeSockShaft(
    state: CharacterRenderState,
    body: Body,
    socks: Socks,
    options: RenderOptions,
) {
    val height = when (socks.style) {
        SocksStyle.TightHigh -> state.config.equipment.footwear.heightKnee
        SocksStyle.KneeHigh -> state.config.equipment.footwear.heightKnee
        SocksStyle.Quarter -> state.config.equipment.footwear.heightAnkle
        else -> return
    }

    visualizeBootShaft(state, body, options, height)
}
