package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.style.SocksStyle
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.utils.math.unit.ZERO_VOLUME
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.BEHIND_LAYER
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.visualizeFeet

data class SockConfig(
    val thickness: Factor,
) {

    fun getHeightFactor(
        config: ICharacterConfig<Body>,
        style: SocksStyle,
    ): Factor? {
        val shoeHeight = config.body().getShoeHeight(config)
        val footwear = config.equipment().footwear

        return when (style) {
            SocksStyle.TightHigh -> footwear.heightTight
            SocksStyle.KneeHigh -> footwear.heightKnee
            SocksStyle.Quarter -> footwear.heightAnkle
            SocksStyle.Ankle -> shoeHeight
            SocksStyle.ToeTopper -> null
        }
    }

    fun getHeight(
        config: ICharacterConfig<Body>,
        style: SocksStyle,
    ): Distance? {
        val height = getHeightFactor(config, style) ?: return null

        return config.body().getLegHeight(config) * height
    }

    fun getVolume(
        config: ICharacterConfig<Body>,
        style: SocksStyle,
    ): Volume {
        val height = getHeight(config, style) ?: return ZERO_VOLUME

        return config.equipment().getPantlegVolume(config, height, thickness)
    }

}

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
    val height = state.config.equipment.sock.getHeightFactor(state, socks.style) ?: return

    visualizeBootShaft(state, options, height, ZERO)
}
