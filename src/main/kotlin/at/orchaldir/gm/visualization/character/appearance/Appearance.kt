package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.item.equipment.EquipmentElementMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.Color.Black
import at.orchaldir.gm.core.selector.character.getAppearanceForAge
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeCharacter(
    config: CharacterRenderConfig,
    state: State,
    character: Character,
    equipped: EquipmentElementMap = EquipmentMap(),
    renderFront: Boolean = true,
): Svg {
    val appearance = state.getAppearanceForAge(character)

    return visualizeCharacter(state, config, appearance, equipped, renderFront)
}

fun visualizeCharacter(
    state: State,
    config: CharacterRenderConfig,
    appearance: Appearance,
    equipped: EquipmentElementMap = EquipmentMap(),
    renderFront: Boolean = true,
) = visualizeAppearance(state, config, calculatePaddedSize(config, appearance), appearance, equipped, renderFront)

fun visualizeAppearance(
    state: State,
    config: CharacterRenderConfig,
    paddedSize: PaddedSize,
    appearance: Appearance,
    equipped: EquipmentElementMap = EquipmentMap(),
    renderFront: Boolean = true,
): Svg {
    val aabb = paddedSize.getFullAABB()
    val builder = SvgBuilder(paddedSize.getFullSize())
    val renderState = CharacterRenderState(state, aabb, config, builder, renderFront, equipped)

    visualizeAppearance(renderState, appearance, paddedSize)

    return builder.finish()
}

fun visualizeAppearance(
    state: State,
    config: CharacterRenderConfig,
    renderSize: Size2d,
    appearance: Appearance,
    paddedSize: PaddedSize,
    equipped: EquipmentElementMap = EquipmentMap(),
    renderFront: Boolean = true,
): Svg {
    val aabb = AABB(renderSize)
    val builder = SvgBuilder(renderSize)
    val state = CharacterRenderState(state, aabb, config, builder, renderFront, equipped)

    visualizeAppearance(state, appearance, paddedSize)

    return builder.finish()
}

fun visualizeAppearance(
    state: CharacterRenderState,
    appearance: Appearance,
    paddedSize: PaddedSize,
) {
    val offset = Point2d(paddedSize.left + paddedSize.universial, paddedSize.top + paddedSize.universial)
    val full = AABB.fromCenter(state.fullAABB.getCenter(), paddedSize.getFullSize())
    val inner = AABB(full.start + offset, appearance.getSize2d())
    val innerState = state.copy(fullAABB = inner)

    state.renderer.getLayer().renderRectangle(state.fullAABB, BorderOnly(state.config.line))

    when (appearance) {
        is HeadOnly -> {
            val headState = state.copy(headAABB = state.fullAABB)
            visualizeHead(headState, appearance.head, appearance.skin)
        }
        is HumanoidBody -> {
            val torsoAabb = state.config.body.getTorsoAabb(state, appearance.body)
            val bodyState = state.copy(torsoAABB = torsoAabb)
            val headAabb = state.config.body.getHeadAabb(inner)
            val headState = state.copy(headAABB = headAabb)

            visualizeBody(bodyState, appearance.body, appearance.skin)
            visualizeHead(headState, appearance.head, appearance.skin)
            visualizeTails(bodyState, appearance.tails, appearance.skin, appearance.head.hair)
            visualizeWings(bodyState, appearance.wings, appearance.skin, appearance.head.hair)
        }

        UndefinedAppearance -> {
            val height = state.config.padding
            val options = RenderStringOptions(Black.toRender(), height * 2.0f)
            val center = state.fullAABB.getCenter() + Point2d.yAxis(height * 0.25f)
            state.renderer.getLayer().renderString("?", center, Orientation.zero(), options)
        }

    }
}
