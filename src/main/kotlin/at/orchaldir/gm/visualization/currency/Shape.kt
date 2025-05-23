package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeShape(
    renderer: LayerRenderer,
    center: Point2d,
    shape: CircularShape,
    radius: Distance,
    options: RenderOptions,
) {
    val polygon = createShapePolygon(shape, center, radius)

    if (shape.isRounded()) {
        renderer.renderRoundedPolygon(polygon, options)
    } else {
        renderer.renderPolygon(polygon, options)
    }
}

fun visualizeHoledShape(
    renderer: LayerRenderer,
    center: Point2d,
    shape: CircularShape,
    radius: Distance,
    holeShape: CircularShape,
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
