package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.QUARTER_CIRCLE
import at.orchaldir.gm.utils.math.createRoundedTriangle
import at.orchaldir.gm.utils.math.createTriangle
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeShape(
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    shape: Shape,
    options: RenderOptions,
) {
    when (shape) {
        is Circle -> renderer.renderCircle(center, radius, options)
        is Triangle -> {
            val orientation = if (shape.cornerTop) {
                -QUARTER_CIRCLE
            } else {
                QUARTER_CIRCLE
            }

            if (shape.rounded) {
                val polygon = createRoundedTriangle(center, radius, orientation)
                renderer.renderRoundedPolygon(polygon, options)
            } else {
                val polygon = createTriangle(center, radius, orientation)
                renderer.renderPolygon(polygon, options)
            }
        }
        is Square -> doNothing()
        is RegularPolygon -> doNothing()
    }
}
