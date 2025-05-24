package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

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

fun visualizeHoledComplexShape(
    renderer: LayerRenderer,
    aabb: AABB,
    shape: ComplexShape,
    holeAabb: AABB,
    holeShape: ComplexShape,
    options: RenderOptions,
) {
    val coinPolygon = createComplexShapePolygon(shape, aabb)
    val holePolygon = createComplexShapePolygon(holeShape, holeAabb)

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
    CircularShape.Circle -> createRegularPolygon(center, radius, 120)
    CircularShape.Teardrop -> createTeardrop(center, radius)
    CircularShape.ReverseTeardrop -> createReverseTeardrop(center, radius)
    CircularShape.Triangle -> createTriangle(center, radius)
    CircularShape.CutoffTriangle -> createCutoffTriangle(center, radius)
    CircularShape.RoundedTriangle -> createRoundedTriangle(center, radius)
    CircularShape.Square -> createSquare(center, radius)
    CircularShape.CutoffSquare -> createCutoffSquare(center, radius)
    CircularShape.RoundedSquare -> createRoundedSquare(center, radius)
    CircularShape.Diamond -> createDiamond(center, radius)
    CircularShape.CutoffDiamond -> createCutoffDiamond(center, radius)
    CircularShape.RoundedDiamond -> createRoundedDiamond(center, radius)
    CircularShape.ScallopedOctagon -> createScallopedRegularPolygon(center, radius, 8)
    CircularShape.ScallopedDodecagonal -> createScallopedRegularPolygon(center, radius, 12)
    else -> createRegularPolygon(center, radius, shape.getSides())
}

private fun createRectangularShapePolygon(
    shape: RectangularShape,
    aabb: AABB,
) = when (shape) {
    RectangularShape.Rectangle -> Polygon2d(aabb.getCorners())
    RectangularShape.Teardrop -> createTeardrop(aabb)
    RectangularShape.ReverseTeardrop -> createReverseTeardrop(aabb)
}
