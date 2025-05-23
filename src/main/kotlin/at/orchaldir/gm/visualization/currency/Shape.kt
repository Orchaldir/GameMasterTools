package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.Shape
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeShape(
    renderer: LayerRenderer,
    center: Point2d,
    shape: Shape,
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
    Shape.Teardrop -> createTeardrop(center, radius)
    Shape.ReverseTeardrop -> createReverseTeardrop(center, radius)
    Shape.Triangle -> createTriangle(center, radius)
    Shape.CutoffTriangle -> createCutoffTriangle(center, radius)
    Shape.RoundedTriangle -> createRoundedTriangle(center, radius)
    Shape.Square -> createSquare(center, radius)
    Shape.CutoffSquare -> createCutoffSquare(center, radius)
    Shape.RoundedSquare -> createRoundedSquare(center, radius)
    Shape.Diamond -> createDiamond(center, radius)
    Shape.CutoffDiamond -> createCutoffDiamond(center, radius)
    Shape.RoundedDiamond -> createRoundedDiamond(center, radius)
    Shape.ScallopedOctagon -> createScallopedRegularPolygon(center, radius, 8)
    Shape.ScallopedDodecagonal -> createScallopedRegularPolygon(center, radius, 12)
    else -> createRegularPolygon(center, radius, shape.getSides())
}
