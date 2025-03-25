package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.style.SocksStyle
import at.orchaldir.gm.utils.renderer.model.RenderOptions
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

    visualizeSocksShaft(state, body, socks, options)

    val layer = if (state.renderFront) {
        EQUIPMENT_LAYER
    } else {
        BEHIND_LAYER
    }
    visualizeFeet(state, body, options, layer)
}

private fun visualizeSocksShaft(
    state: CharacterRenderState,
    body: Body,
    socks: Socks,
    options: RenderOptions,
) {
    val shoeHeight = state.config.body.getShoeHeight(body)
    val height = when (socks.style) {
        SocksStyle.TightHigh -> state.config.equipment.footwear.heightTight
        SocksStyle.KneeHigh -> state.config.equipment.footwear.heightKnee
        SocksStyle.Quarter -> state.config.equipment.footwear.heightAnkle
        SocksStyle.Ankle -> if (state.renderFront) {
            return
        } else {
            shoeHeight
        }
        else -> return
    }

    visualizeBootShaft(state, body, options, height)
}
