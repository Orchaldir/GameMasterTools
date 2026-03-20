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
import at.orchaldir.gm.core.selector.character.getAppearanceOfCharacter
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.convert

fun visualizeCharacter(
    config: CharacterRenderConfig,
    state: State,
    character: Character,
    equipped: EquipmentElementMap = EquipmentMap(),
    renderFront: Boolean = true,
): Svg {
    val appearance = state.getAppearanceOfCharacter(character)

    return visualizeCharacter(state, config, appearance, equipped, renderFront)
}

fun visualizeCharacter(
    state: State,
    config: CharacterRenderConfig,
    appearance: Appearance,
    equipped: EquipmentElementMap = EquipmentMap(),
    renderFront: Boolean = true,
) = visualizeAppearance(
    state,
    config,
    calculatePaddedSize(config, appearance, equipped),
    appearance,
    equipped,
    renderFront,
)

fun visualizeAppearance(
    state: State,
    config: CharacterRenderConfig,
    paddedSize: PaddedSize,
    appearance: Appearance,
    equipped: EquipmentElementMap = EquipmentMap(),
    renderFront: Boolean = true,
): Svg {
    val aabb = paddedSize.getInnerAABB()
    val renderSize = paddedSize.getFullSize()
    val builder = SvgBuilder(renderSize)
    val renderState = CharacterRenderState(state, appearance, aabb, config, builder, renderFront, equipped)

    renderState.renderer.getLayer().renderRectangle(AABB(renderSize), renderState.config.colors.getBorderOnly())

    visualizeAppearance(renderState)

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
    val fullAABB = paddedSize.getInnerAABB(renderSize)
    val renderState = CharacterRenderState(state, appearance, fullAABB, config, builder, renderFront, equipped)

    renderState.renderer.getLayer().renderRectangle(aabb, renderState.config.colors.getBorderOnly())

    visualizeAppearance(renderState)

    return builder.finish()
}

fun visualizeAppearance(
    state: CharacterRenderState<Appearance>,
) {
    when (val appearance = state.get()) {
        is HeadOnly -> {
            val headState = state.convert(appearance.head, state.fullAABB)
            visualizeHead(headState, appearance.head, appearance.skin)
        }

        is HumanoidBody -> {
            val torsoAabb = state.config.body.getTorsoAabb(state.fullAABB, appearance.body)
            val bodyState = state.convert(appearance.body, torsoAabb)
            val headAabb = state.config.body.getHeadAabb(state.fullAABB)
            val headState = state.convert(appearance.head, headAabb)

            visualizeBody(bodyState, appearance.skin)
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
