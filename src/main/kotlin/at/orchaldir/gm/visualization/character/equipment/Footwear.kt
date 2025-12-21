package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.style.FootwearStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.utils.math.unit.ZERO_VOLUME
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.BEHIND_LAYER
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.visualizeFeet

data class FootwearConfig(
    val heightAnkle: Factor,
    val heightKnee: Factor,
    val heightTight: Factor,
    val heightSole: Factor,
    val shaftPadding: Factor,
    val shaftThickness: Factor,
) {

    // shaft

    fun getShaftHeightFactor(
        config: ICharacterConfig<Body>,
        style: FootwearStyle,
        isFront: Boolean = true,
    ): Factor? {
        val shoeHeight = config.body().getShoeHeight(config)

        return when (style) {
            FootwearStyle.Boots -> heightAnkle
            FootwearStyle.KneeHighBoots -> heightKnee
            FootwearStyle.Pumps -> if (isFront) {
                null
            } else {
                shoeHeight
            }
            FootwearStyle.Shoes -> shoeHeight
            FootwearStyle.Sandals -> null
            FootwearStyle.Slippers -> null
        }
    }

    fun getShaftHeight(
        config: ICharacterConfig<Body>,
        style: FootwearStyle,
        isFront: Boolean = true,
    ): Distance? {
        val height = getShaftHeightFactor(config, style, isFront) ?: return null

        return config.fullAABB().convertHeight(config.body().getLegHeight() * height)
    }

    fun getShaftVolume(
        config: ICharacterConfig<Body>,
        style: FootwearStyle,
    ): Volume {
        var volume = ZERO_VOLUME

        if (style.hasShaft()) {
            val height = getShaftHeight(config, style, false) ?: error("Style $style should have a shaft height")

            volume += config.equipment().getPantlegVolume(config, height, shaftThickness)
        }

        return volume * 2.0f
    }

    // sole

    fun getSoleFrontSize(
        config: ICharacterConfig<Body>,
    ): Size2d {
        val width = config.body().getFootRadius(config) * 2.0f
        val height = config.fullAABB().convertHeight(heightSole)

        return Size2d(width, height)
    }

    fun getSoleVolume(
        config: ICharacterConfig<Body>,
        style: FootwearStyle,
    ) = if (style.hasSole()) {
        val soleLength = config.body().getFootLength(config)
        getSoleFrontSize(config).calculateVolumeOfPrism(soleLength) * 2.0f
    } else {
        ZERO_VOLUME
    }
}

fun visualizeFootwear(
    state: CharacterRenderState<Body>,
    footwear: Footwear,
) {
    val fill = footwear.shaft.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeBootShaft(state, footwear, options)

    if (footwear.style.isFootVisible(state.renderFront)) {
        val layer = if (state.renderFront) {
            EQUIPMENT_LAYER
        } else {
            BEHIND_LAYER
        }
        visualizeFeet(state, options, layer)
    }

    if (footwear.style.hasSole()) {
        visualizeSoles(state, footwear)
    }
}

private fun visualizeBootShaft(
    state: CharacterRenderState<Body>,
    footwear: Footwear,
    options: RenderOptions,
) {
    val height = state.equipment().footwear.getShaftHeightFactor(state, footwear.style, state.renderFront) ?: return

    visualizeBootShaft(state, options, height, state.config.equipment.footwear.shaftPadding)
}

fun visualizeBootShaft(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    scale: Factor,
    padding: Factor,
) {
    val config = state.config.body
    val width = config.getLegWidth(state) + padding
    val height = config.getLegHeight() * scale
    val size = state.fullAABB.size.scale(width, height)
    val (left, right) = config.getMirroredLegPoint(state, FULL - scale * 0.5f)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)
    val layer = state.renderer.getLayer(EQUIPMENT_LAYER)

    layer.renderRectangle(leftAabb, options)
    layer.renderRectangle(rightAabb, options)
}

fun visualizeSoles(
    state: CharacterRenderState<Body>,
    footwear: Footwear,
) {
    val config = state.config
    val color = footwear.sole.getColor(state.state)
    val options = FillAndBorder(color.toRender(), config.line)
    val (left, right) = config.body.getMirroredLegPoint(state, END)
    val size = config.equipment.footwear.getSoleFrontSize(state)
    val offset = Point2d.yAxis(size.height / 2.0f)
    val leftAABB = AABB.fromCenter(left + offset, size)
    val rightAABB = AABB.fromCenter(right + offset, size)
    val layer = state.renderer.getLayer(EQUIPMENT_LAYER)

    layer.renderRectangle(leftAABB, options)
    layer.renderRectangle(rightAABB, options)
}