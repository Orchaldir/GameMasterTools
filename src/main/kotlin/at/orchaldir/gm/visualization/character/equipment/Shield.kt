package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Shield
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_HAND_LAYER
import at.orchaldir.gm.visualization.utils.visualizeCircularShape
import at.orchaldir.gm.visualization.utils.visualizeComplexShape
import at.orchaldir.gm.visualization.utils.visualizeHoledComplexShape

data class ShieldConfig(
    val radius: SizeConfig<Factor>,
    val borderFactor: SizeConfig<Factor>,
    val bossFactor: Factor,
    val bossBorderFactor: Factor,
) {
    fun getRadius(aabb: AABB, shield: Shield): Distance {
        val radius = aabb.convertHeight(radius.convert(shield.size))

        return if (shield.shape is UsingCircularShape) {
            radius
        } else {
            radius * 1.5f
        }
    }

    fun getBossRadius(aabb: AABB) = aabb.convertHeight(bossFactor)
}

fun visualizeShield(
    state: CharacterRenderState,
    body: Body,
    shield: Shield,
    set: Set<BodySlot>,
) {
    val (left, right) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val radius = state.config.equipment.shield.getRadius(state.aabb, shield)
    val renderer = state.getLayer(ABOVE_HAND_LAYER)
    val center = state.getCenter(left, right, set, BodySlot.HeldInLeftHand)

    if (state.renderFront) {
        visualizeShieldBody(state, renderer, center, radius, shield.shape, shield.front)
        visualizeShieldBorder(state, renderer, center, radius, shield)
        visualizeShieldBoss(state, renderer, shield.boss, center)
    } else {
        visualizeShieldBody(state, renderer, center, radius, shield.shape, shield.back)
    }
}

private fun visualizeShieldBody(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    shape: ComplexShape,
    part: FillLookupItemPart,
) {
    val fill = part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeComplexShape(renderer, center, radius, shape, options)
}

private fun visualizeShieldBorder(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    shield: Shield,
) {
    when (val border = shield.border) {
        NoShieldBorder -> doNothing()
        is SimpleShieldBorder -> {
            val fill = border.part.getColor(state.state, state.colors)
            val options = FillAndBorder(fill.toRender(), state.config.line)
            val innerRadius = radius * state.config.equipment.shield.borderFactor.convert(border.size)

            visualizeHoledComplexShape(
                renderer,
                center,
                radius,
                innerRadius,
                shield.shape,
                shield.shape,
                options,
            )
        }
    }
}

private fun visualizeShieldBoss(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    boss: ShieldBoss,
    center: Point2d,
) {
    when (boss) {
        NoShieldBoss -> doNothing()
        is SimpleShieldBoss -> {
            visualizeShieldBoss(state, renderer, center, boss.shape, boss.part)
        }

        is ShieldBossWithBorder -> {
            visualizeShieldBoss(
                state,
                renderer,
                center,
                boss.border,
                boss.borderPart,
                state.config.equipment.shield.bossBorderFactor,
            )
            visualizeShieldBoss(state, renderer, center, boss.shape, boss.part)
        }
    }
}

private fun visualizeShieldBoss(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    shape: CircularShape,
    part: ColorSchemeItemPart,
    factor: Factor = FULL,
) {
    val bossRadius = state.config.equipment.shield.getBossRadius(state.aabb) * factor
    val fill = part.getColor(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeCircularShape(renderer, center, bossRadius, shape, options)
}