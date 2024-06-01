package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d

interface Renderer {

    fun renderCircle(center: Point2d, radius: Distance, options: RenderOptions)

    fun renderCircle(aabb: AABB, options: RenderOptions) =
        renderCircle(aabb.getCenter(), aabb.getInnerRadius(), options)

    fun renderEllipse(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions)

    fun renderEllipse(aabb: AABB, options: RenderOptions) =
        renderEllipse(aabb.getCenter(), Distance(aabb.size.width / 2.0f), Distance(aabb.size.height / 2.0f), options)

    fun renderLine(line: List<Point2d>, options: LineOptions)

    fun renderPointedOval(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions)

    fun renderPointedOval(aabb: AABB, options: RenderOptions) =
        renderPointedOval(
            aabb.getCenter(),
            Distance(aabb.size.width / 2.0f),
            Distance(aabb.size.height / 2.0f),
            options
        )

    fun renderPolygon(polygon: Polygon2d, options: RenderOptions)

    fun renderRectangle(aabb: AABB, options: RenderOptions)

}