package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.QUARTER_CIRCLE
import at.orchaldir.gm.utils.math.createRoundedSquare
import at.orchaldir.gm.utils.math.createRoundedTriangle
import at.orchaldir.gm.utils.math.createSquare
import at.orchaldir.gm.utils.math.createTriangle
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
        Shape.Triangle -> {
            val polygon = createTriangle(center, radius, TRIANGLE_ORIENTATION)
            renderer.renderPolygon(polygon, options)
        }

        Shape.RoundedTriangle -> {
            val polygon = createRoundedTriangle(center, radius, TRIANGLE_ORIENTATION)
            renderer.renderRoundedPolygon(polygon, options)
        }

        Shape.Square -> {
            val polygon = createSquare(center, radius)
            renderer.renderPolygon(polygon, options)
        }

        Shape.RoundedSquare -> {
            val polygon = createRoundedSquare(center, radius)
            renderer.renderRoundedPolygon(polygon, options)
        }
        Shape.Diamond -> doNothing()
        Shape.Pentagon -> doNothing()
        Shape.Hexagon -> doNothing()
        Shape.Heptagon -> doNothing()
        Shape.Octagon -> doNothing()
    }
}
