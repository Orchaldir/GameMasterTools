package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.style.SocksStyle
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BEHIND_LAYER
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.visualizeFeet

fun visualizeSocks(
    state: CharacterRenderState<Body>,
    socks: Socks,
) {
    val fill = socks.main.getFill(state.state, state.colors)
    val options = state.config.getLineOptions(fill)

    visualizeSocksShaft(state, socks, options)

    val layer = if (state.renderFront) {
        EQUIPMENT_LAYER
    } else {
        BEHIND_LAYER
    }
    visualizeFeet(state, options, layer)
}

private fun visualizeSocksShaft(
    state: CharacterRenderState<Body>,
    socks: Socks,
    options: RenderOptions,
) {
    val shoeHeight = state.config.body.getShoeHeight(state)
    val height = when (socks.style) {
        SocksStyle.TightHigh -> state.config.equipment.footwear.heightTight
        SocksStyle.KneeHigh -> state.config.equipment.footwear.heightKnee
        SocksStyle.Quarter -> state.config.equipment.footwear.heightAnkle
        SocksStyle.Ankle -> shoeHeight
        else -> return
    }

    visualizeBootShaft(state, options, height, ZERO)
}
