package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.style.Boot
import at.orchaldir.gm.core.model.item.equipment.style.FootwearStyle
import at.orchaldir.gm.core.model.item.equipment.style.KneeHighBoot
import at.orchaldir.gm.core.model.item.equipment.style.Pumps
import at.orchaldir.gm.core.model.item.equipment.style.Sandal
import at.orchaldir.gm.core.model.item.equipment.style.Shoe
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShoe
import at.orchaldir.gm.core.model.item.equipment.style.Slipper
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.utils.math.unit.ZERO_VOLUME
import at.orchaldir.gm.utils.renderer.model.RenderOptions
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
            is Boot -> heightAnkle
            is KneeHighBoot -> heightKnee
            is Pumps -> if (isFront) {
                null
            } else {
                shoeHeight
            }
            is Sandal -> null
            is Shoe, is SimpleShoe -> shoeHeight
            is Slipper -> null
        }
    }

    fun getShaftHeight(
        config: ICharacterConfig<Body>,
        style: FootwearStyle,
        isFront: Boolean = true,
    ): Distance? {
        val height = getShaftHeightFactor(config, style, isFront) ?: return null

        return config.body().getLegHeight(config) * height
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
        padding: Factor = ZERO,
        isFront: Boolean = false,
    ): Size2d {
        val width = if (isFront) {
            config.body().getFootRadius(config)  * 2.0f
        } else {
            config.fullAABB().convertWidth(config.body().getLegWidth(config) + padding)
        }
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
    when (val style = footwear.style) {
        is Boot -> {
            visualizeBootShaft(state, style, style.shaft)
            visualizeBootFoot(state, style.shaft)
            visualizeSoles(state, style.sole)
        }
        is KneeHighBoot -> {
            visualizeBootShaft(state, style, style.shaft)
            visualizeBootFoot(state, style.shaft)
            visualizeSoles(state, style.sole)
        }
        is Pumps -> {
            visualizeBootShaft(state, style, style.main)
            visualizeBootFoot(state, style.main)
        }
        is Sandal -> {
            visualizeBootShaft(state, style, style.shaft)
            visualizeSoles(state, style.sole)
        }
        is Shoe -> {
            visualizeBootShaft(state, style, style.shaft)
            visualizeBootFoot(state, style.shaft)
            visualizeSoles(state, style.sole)
        }
        is SimpleShoe -> {
            visualizeBootShaft(state, style, style.main)
            visualizeBootFoot(state, style.main)
        }
        is Slipper -> {
            visualizeBootShaft(state, style, style.shaft)

            if (state.renderFront) {
                visualizeBootFoot(state, style.shaft)
            }

            visualizeSoles(state, style.sole)
        }
    }
}

private fun visualizeBootFoot(
    state: CharacterRenderState<Body>,
    main: ItemPart,
) {
    val options = state.getFillAndBorder(main)

    visualizeFeet(state, options, EQUIPMENT_LAYER)
}

private fun visualizeBootShaft(
    state: CharacterRenderState<Body>,
    style: FootwearStyle,
    shaft: ItemPart,
) {
    val options = state.getFillAndBorder(shaft)
    val height = state.equipment().footwear.getShaftHeightFactor(state, style, state.renderFront) ?: return

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
    val height = config.getLegHeightFactor() * scale
    val size = state.fullAABB.size.scale(width, height)
    val (left, right) = config.getMirroredLegPoint(state, FULL - scale * 0.5f)
    val leftAabb = AABB.fromCenter(left, size)
    val rightAabb = AABB.fromCenter(right, size)
    val layer = state.renderer.getLayer(EQUIPMENT_LAYER)

    layer.renderRectangle(leftAabb, options)
    layer.renderRectangle(rightAabb, options)
}

private fun visualizeSoles(
    state: CharacterRenderState<Body>,
    sole: ItemPart,
) {
    val config = state.config
    val options = state.getFillAndBorder(sole)
    val (left, right) = config.body.getMirroredLegPoint(state, END)
    val size = config.equipment.footwear.getSoleFrontSize(
        state,
        state.config.equipment.footwear.shaftPadding,
        state.renderFront,
    )
    val offset = Point2d.yAxis(size.height / 2.0f)
    val leftAABB = AABB.fromCenter(left + offset, size)
    val rightAABB = AABB.fromCenter(right + offset, size)
    val layer = state.renderer.getLayer(EQUIPMENT_LAYER)

    layer.renderRectangle(leftAABB, options)
    layer.renderRectangle(rightAABB, options)
}