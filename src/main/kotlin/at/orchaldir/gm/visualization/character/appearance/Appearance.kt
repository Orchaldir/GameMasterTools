package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.util.Color.Black
import at.orchaldir.gm.core.selector.getAppearanceForAge
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
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
    equipped: List<EquipmentData> = emptyList(),
    renderFront: Boolean = true,
): Svg {
    val appearance = state.getAppearanceForAge(character)

    return visualizeCharacter(config, appearance, equipped, renderFront)
}

fun visualizeCharacter(
    config: CharacterRenderConfig,
    appearance: Appearance,
    equipped: List<EquipmentData> = emptyList(),
    renderFront: Boolean = true,
) = visualizeAppearance(config, calculateSize(config, appearance), appearance, equipped, renderFront)

fun visualizeAppearance(
    config: CharacterRenderConfig,
    paddedSize: PaddedSize,
    appearance: Appearance,
    equipped: List<EquipmentData> = emptyList(),
    renderFront: Boolean = true,
): Svg {
    val aabb = paddedSize.getFullAABB()
    val builder = SvgBuilder(paddedSize.getFullSize())
    val state = CharacterRenderState(aabb, config, builder, renderFront, equipped)

    visualizeAppearance(state, appearance)

    return builder.finish()
}

fun visualizeAppearance(
    config: CharacterRenderConfig,
    size: Size2d,
    appearance: Appearance,
    equipped: List<EquipmentData> = emptyList(),
    renderFront: Boolean = true,
): Svg {
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val state = CharacterRenderState(aabb, config, builder, renderFront, equipped)

    visualizeAppearance(state, appearance)

    return builder.finish()
}

fun visualizeAppearance(
    state: CharacterRenderState,
    appearance: Appearance,
) {
    val inner = AABB.fromCenter(state.aabb.getCenter(), appearance.getSize2d())
    val innerState = state.copy(aabb = inner)

    state.renderer.getLayer().renderRectangle(state.aabb, BorderOnly(state.config.line))

    when (appearance) {
        is HeadOnly -> visualizeHead(innerState, appearance.head)
        is HumanoidBody -> {
            val headAabb = state.config.body.getHeadAabb(inner)
            val headState = state.copy(aabb = headAabb)

            visualizeBody(innerState, appearance.body)
            visualizeHead(headState, appearance.head)
            visualizeWings(innerState, appearance.wings)
        }

        UndefinedAppearance -> {
            val height = state.config.padding.toMeters()
            val options = RenderStringOptions(Black.toRender(), 2.0f * height)
            val center = state.aabb.getCenter() + Point2d(0.0f, height * 0.25f)
            state.renderer.getLayer().renderString("?", center, Orientation.zero(), options)
        }

    }
}
