package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.QUARTER_CIRCLE
import at.orchaldir.gm.utils.math.createRegularPolygon
import at.orchaldir.gm.utils.math.createRoundedSquare
import at.orchaldir.gm.utils.math.createRoundedTriangle
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

private val TRIANGLE_ORIENTATION = -QUARTER_CIRCLE

fun visualizeShape(
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    shape: Shape,
    options: RenderOptions,
) {
    when (shape) {
        Shape.Circle -> renderer.renderCircle(center, radius, options)
        Shape.Triangle -> visualizeRegularPolygon(renderer, options, center, radius, 3)

        Shape.RoundedTriangle -> {
            val polygon = createRoundedTriangle(center, radius, TRIANGLE_ORIENTATION)
            renderer.renderRoundedPolygon(polygon, options)
        }

        Shape.Square -> visualizeRegularPolygon(renderer, options, center, radius, 4, QUARTER_CIRCLE / 2.0f)

        Shape.RoundedSquare -> {
            val polygon = createRoundedSquare(center, radius)
            renderer.renderRoundedPolygon(polygon, options)
        }
        Shape.Diamond -> visualizeRegularPolygon(renderer, options, center, radius, 4)
        Shape.Pentagon -> visualizeRegularPolygon(renderer, options, center, radius, 5)
        Shape.Hexagon -> visualizeRegularPolygon(renderer, options, center, radius, 6)
        Shape.Heptagon -> visualizeRegularPolygon(renderer, options, center, radius, 7)
        Shape.Octagon -> visualizeRegularPolygon(renderer, options, center, radius, 8)
    }
}

private fun visualizeRegularPolygon(
    renderer: LayerRenderer,
    options: RenderOptions,
    center: Point2d,
    radius: Distance,
    sides: Int,
    orientation: Orientation = TRIANGLE_ORIENTATION,
) {
    val polygon = createRegularPolygon(center, radius, sides, orientation)
    renderer.renderPolygon(polygon, options)
}
