package at.orchaldir.gm.visualization

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.*
import at.orchaldir.gm.utils.math.shape.CircularShape.*
import at.orchaldir.gm.utils.math.shape.RectangularShape.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeComplexShape(
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    shape: ComplexShape,
    options: RenderOptions,
) = visualizeComplexShape(
    renderer,
    shape.calculateAabb(center, radius),
    shape,
    options,
)

fun visualizeComplexShape(
    renderer: LayerRenderer,
    aabb: AABB,
    shape: ComplexShape,
    options: RenderOptions,
) {
    val polygon = createComplexShapePolygon(shape, aabb)

    if (shape.isRounded()) {
        renderer.renderRoundedPolygon(polygon, options)
    } else {
        renderer.renderPolygon(polygon, options)
    }
}

fun visualizeCircularShape(
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    shape: CircularShape,
    options: RenderOptions,
) {
    val polygon = createCircularShapePolygon(shape, center, radius)

    if (shape.isRounded()) {
        renderer.renderRoundedPolygon(polygon, options)
    } else {
        renderer.renderPolygon(polygon, options)
    }
}

fun visualizeHoledComplexShape(
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    holeRadius: Distance,
    shape: ComplexShape,
    holeShape: ComplexShape,
    options: RenderOptions,
) = visualizeHoledComplexShape(
    renderer,
    shape.calculateAabb(center, radius),
    shape,
    shape.calculateAabb(center, holeRadius),
    holeShape,
    options,
)

fun visualizeHoledComplexShape(
    renderer: LayerRenderer,
    aabb: AABB,
    shape: ComplexShape,
    holeAabb: AABB,
    holeShape: ComplexShape,
    options: RenderOptions,
) {
    val shapePolygon = createComplexShapePolygon(shape, aabb)
    val holePolygon = createComplexShapePolygon(holeShape, holeAabb)

    if (shape.isRounded()) {
        if (holeShape.isRounded()) {
            renderer.renderRoundedPolygonWithRoundedHole(shapePolygon, holePolygon, options)
        } else {
            renderer.renderRoundedPolygonWithHole(shapePolygon, holePolygon, options)
        }
    } else if (holeShape.isRounded()) {
        renderer.renderPolygonWithRoundedHole(shapePolygon, holePolygon, options)
    } else {
        renderer.renderPolygonWithHole(shapePolygon, holePolygon, options)
    }
}

private fun createComplexShapePolygon(
    shape: ComplexShape,
    aabb: AABB,
) = when (shape) {
    is UsingCircularShape -> createCircularShapePolygon(shape.shape, aabb.getCenter(), aabb.getInnerRadius())
    is UsingRectangularShape -> createRectangularShapePolygon(shape.shape, aabb)
}

private fun createCircularShapePolygon(
    shape: CircularShape,
    center: Point2d,
    radius: Distance,
) = when (shape) {
    Circle -> createRegularPolygon(center, radius, 120)
    Triangle -> createTriangle(center, radius)
    CutoffTriangle -> createCutoffTriangle(center, radius)
    RoundedTriangle -> createRoundedTriangle(center, radius)
    CircularShape.Heater -> createHeater(center, radius)
    CircularShape.RoundedHeater -> createRoundedHeater(center, radius)
    Square -> createSquare(center, radius)
    CutoffSquare -> createCutoffSquare(center, radius)
    RoundedSquare -> createRoundedSquare(center, radius)
    Diamond -> createDiamond(center, radius)
    CutoffDiamond -> createCutoffDiamond(center, radius)
    RoundedDiamond -> createRoundedDiamond(center, radius)
    ScallopedOctagon -> createScallopedRegularPolygon(center, radius, 8)
    ScallopedDodecagonal -> createScallopedRegularPolygon(center, radius, 12)
    else -> createRegularPolygon(center, radius, shape.getSides())
}

private fun createRectangularShapePolygon(
    shape: RectangularShape,
    aabb: AABB,
) = when (shape) {
    RectangularShape.Heater -> createHeater(aabb)
    RectangularShape.RoundedHeater -> createRoundedHeater(aabb)
    Rectangle, Ellipse -> Polygon2d(aabb.getCorners())
    RoundedRectangle -> Polygon2d(subdividePolygon(aabb.getCorners(), 1, ::halfSegment))
    Cross -> createCross(aabb)
    Teardrop -> createTeardrop(aabb)
    ReverseTeardrop -> createReverseTeardrop(aabb)
}
