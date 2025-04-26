package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.QUARTER_CIRCLE
import at.orchaldir.gm.utils.math.createRegularPolygon
import at.orchaldir.gm.utils.math.createRoundedRegularPolygon
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

private val TRIANGLE_ORIENTATION = -QUARTER_CIRCLE
private val SQUARE_ORIENTATION = QUARTER_CIRCLE / 2.0f

fun visualizeShape(
    renderer: LayerRenderer,
    center: Point2d,
    shape: Shape,
    radius: Distance,
    options: RenderOptions,
) {
    when (shape) {
        Shape.Circle -> renderer.renderCircle(center, radius, options)
        Shape.Triangle -> visualizeRegularPolygon(renderer, options, center, radius, 3)
        Shape.RoundedTriangle -> visualizeRoundedRegularPolygon(renderer, options, center, radius, 3)
        Shape.Square -> visualizeRegularPolygon(renderer, options, center, radius, 4, SQUARE_ORIENTATION)
        Shape.RoundedSquare -> visualizeRoundedRegularPolygon(renderer, options, center, radius, 4, SQUARE_ORIENTATION)
        Shape.Diamond -> visualizeRegularPolygon(renderer, options, center, radius, 4)
        Shape.Pentagon -> visualizeRegularPolygon(renderer, options, center, radius, 5)
        Shape.Hexagon -> visualizeRegularPolygon(renderer, options, center, radius, 6)
        Shape.Heptagon -> visualizeRegularPolygon(renderer, options, center, radius, 7)
        Shape.Octagon -> visualizeRegularPolygon(renderer, options, center, radius, 8)
        Shape.Dodecagonal -> visualizeRegularPolygon(renderer, options, center, radius, 12)
    }
}

fun visualizeHoledShape(
    renderer: LayerRenderer,
    center: Point2d,
    shape: Shape,
    radius: Distance,
    holeShape: Shape,
    holeRadius: Distance,
    options: RenderOptions,
) {
    val coinPolygon = createShapePolygon(shape, center, radius)
    val holePolygon = createShapePolygon(holeShape, center, holeRadius)

    if (shape.isRounded()) {
        if (holeShape.isRounded()) {
            renderer.renderRoundedPolygonWithRoundedHole(coinPolygon, holePolygon, options)
        } else {
            renderer.renderRoundedPolygonWithHole(coinPolygon, holePolygon, options)
        }
    } else if (holeShape.isRounded()) {
        renderer.renderPolygonWithRoundedHole(coinPolygon, holePolygon, options)
    } else {
        renderer.renderPolygonWithHole(coinPolygon, holePolygon, options)
    }
}

private fun createShapePolygon(
    shape: Shape,
    center: Point2d,
    radius: Distance,
) = when (shape) {
    Shape.Circle -> createRegularPolygon(center, radius, 120)
    Shape.Triangle -> createRegularPolygon(center, radius, 3)
    Shape.RoundedTriangle -> createRoundedRegularPolygon(center, radius, 3)
    Shape.Square -> createRoundedRegularPolygon(center, radius, 4, SQUARE_ORIENTATION)
    Shape.RoundedSquare -> createRoundedRegularPolygon(center, radius, 4, SQUARE_ORIENTATION)
    Shape.Diamond -> createRegularPolygon(center, radius, 4)
    Shape.Pentagon -> createRegularPolygon(center, radius, 5)
    Shape.Hexagon -> createRegularPolygon(center, radius, 6)
    Shape.Heptagon -> createRegularPolygon(center, radius, 7)
    Shape.Octagon -> createRegularPolygon(center, radius, 8)
    Shape.Dodecagonal -> createRegularPolygon(center, radius, 12)
}

private fun visualizeRoundedRegularPolygon(
    renderer: LayerRenderer,
    options: RenderOptions,
    center: Point2d,
    radius: Distance,
    sides: Int,
    orientation: Orientation = TRIANGLE_ORIENTATION,
) {
    val polygon = createRoundedRegularPolygon(center, radius, sides, orientation)
    renderer.renderRoundedPolygon(polygon, options)
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
