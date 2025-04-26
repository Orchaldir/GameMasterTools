package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.Circle
import at.orchaldir.gm.core.model.economy.money.RegularPolygon
import at.orchaldir.gm.core.model.economy.money.Shape
import at.orchaldir.gm.core.model.economy.money.Square
import at.orchaldir.gm.core.model.economy.money.Triangle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
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
        is Triangle -> doNothing()
        is Square -> doNothing()
        is RegularPolygon -> doNothing()
    }
}
