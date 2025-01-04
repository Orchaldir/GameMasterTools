package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.core.model.util.Color.Black
import at.orchaldir.gm.core.selector.getAppearanceForAge
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.model.TextOptions
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.RenderConfig
import at.orchaldir.gm.visualization.character.RenderState

fun visualizeCharacter(
    config: RenderConfig,
    state: State,
    character: Character,
    equipped: List<Equipment> = emptyList(),
    renderFront: Boolean = true,
): Svg {
    val appearance = state.getAppearanceForAge(character)

    return visualizeCharacter(config, appearance, equipped, renderFront)
}

fun visualizeCharacter(
    config: RenderConfig,
    appearance: Appearance,
    equipped: List<Equipment> = emptyList(),
    renderFront: Boolean = true,
): Svg {
    val size = calculateSize(config, appearance)
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val state = RenderState(aabb, config, builder, renderFront, equipped)

    visualizeAppearance(state, appearance)

    return builder.finish()
}

fun visualizeAppearance(
    state: RenderState,
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
        }

        UndefinedAppearance -> {
            val height = state.config.padding.toMeters() * 1.5f
            val options = TextOptions(Black.toRender(), 2.0f * height)
            val center = state.aabb.getCenter() + Point2d(0.0f, height * 0.5f)
            state.renderer.getLayer().renderText("?", center, Orientation.zero(), options)
        }

    }
}

fun calculateSize(config: RenderConfig, appearance: Appearance) = when (appearance) {
    is HeadOnly -> calculateSizeFromHeight(config, appearance.height)
    is HumanoidBody -> calculateSizeFromHeight(config, appearance.height)
    UndefinedAppearance -> square(config.padding * 4.0f)
}

fun calculateSizeFromHeight(
    config: RenderConfig,
    height: Distance,
) = square(height + config.padding * 2.0f)