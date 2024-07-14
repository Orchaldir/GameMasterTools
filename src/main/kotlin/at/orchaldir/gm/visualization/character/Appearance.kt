package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Color.Black
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.BorderOnly
import at.orchaldir.gm.utils.renderer.TextOptions
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.RenderState

const val ABOVE_EQUIPMENT_LAYER = 2
const val BEARD_BG_LAYER = 2
const val EQUIPMENT_LAYER = 1
const val MAIN_LAYER = 0
const val BEHIND_LAYER = -1

fun visualizeCharacter(
    config: RenderConfig,
    appearance: Appearance,
    equipment: List<Equipment>,
    renderFront: Boolean = true,
): Svg {
    val size = calculateSize(config, appearance)
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val state = RenderState(aabb, config, builder, renderFront)

    visualizeAppearance(state, appearance, equipment)

    return builder.finish()
}

fun visualizeAppearance(
    state: RenderState,
    appearance: Appearance,
    equipment: List<Equipment>,
) {
    val inner = state.aabb.shrink(state.config.padding)
    val innerState = state.copy(aabb = inner)

    state.renderer.renderRectangle(state.aabb, BorderOnly(state.config.line))

    when (appearance) {
        is HeadOnly -> visualizeHead(innerState, appearance.head)
        is HumanoidBody -> {
            val headAabb = state.config.body.getHeadAabb(inner)
            val headState = state.copy(aabb = headAabb)

            visualizeBody(innerState, appearance.body, equipment)
            visualizeHead(headState, appearance.head)
        }

        UndefinedAppearance -> {
            val height = state.config.padding.value * 1.5f
            val options = TextOptions(Black.toRender(), 2.0f * height)
            val center = state.aabb.getCenter() + Point2d(0.0f, height * 0.5f)
            state.renderer.renderText("?", center, Orientation.zero(), options)
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