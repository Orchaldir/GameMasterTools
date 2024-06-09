package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Color.Black
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.BorderOnly
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.utils.renderer.TextOptions
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.RenderConfig

const val MAIN_LAYER = 0
const val BEHIND_LAYER = -1

fun visualizeCharacter(config: RenderConfig, appearance: Appearance): Svg {
    val size = calculateSize(config, appearance)
    val aabb = AABB(size)
    val builder = SvgBuilder(size)

    visualizeAppearance(builder, config, aabb, appearance)

    return builder.finish()
}

fun visualizeAppearance(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    appearance: Appearance,
) {
    val inner = aabb.shrink(config.padding)

    renderer.renderRectangle(aabb, BorderOnly(config.line))

    when (appearance) {
        is HeadOnly -> visualizeHead(renderer, config, inner, appearance.head)
        UndefinedAppearance -> {
            val geight = config.padding.value * 1.5f
            val options = TextOptions(Black.toRender(), 2.0f * geight)
            val center = aabb.getCenter() + Point2d(0.0f, geight * 0.5f)
            renderer.renderText("?", center, options)
        }
    }
}

fun calculateSize(config: RenderConfig, appearance: Appearance) = when (appearance) {
    is HeadOnly -> calculateSizeFromHeight(config, appearance.height)
    UndefinedAppearance -> square(config.padding * 4.0f)
}

fun calculateSizeFromHeight(
    config: RenderConfig,
    height: Distance,
) = square(height + config.padding * 2.0f)