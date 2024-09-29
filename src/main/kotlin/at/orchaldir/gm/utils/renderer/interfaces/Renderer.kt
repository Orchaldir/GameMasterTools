package at.orchaldir.gm.utils.renderer.interfaces

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LineOptions
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.TextOptions

interface Renderer {

    fun renderCircle(center: Point2d, radius: Distance, options: RenderOptions, layer: Int = 0)

    fun renderCircle(aabb: AABB, options: RenderOptions, layer: Int = 0) =
        renderCircle(aabb.getCenter(), aabb.getInnerRadius(), options, layer)

    fun renderCircleArc(
        center: Point2d,
        radius: Distance,
        offset: Orientation,
        angle: Orientation,
        options: RenderOptions,
        layer: Int = 0,
    )

    fun renderEllipse(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions, layer: Int = 0)

    fun renderEllipse(aabb: AABB, options: RenderOptions, layer: Int = 0) =
        renderEllipse(
            aabb.getCenter(),
            Distance(aabb.size.width / 2.0f),
            Distance(aabb.size.height / 2.0f),
            options,
            layer
        )

    fun renderLine(line: List<Point2d>, options: LineOptions, layer: Int = 0)

    fun renderPointedOval(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions, layer: Int = 0)

    fun renderPointedOval(aabb: AABB, options: RenderOptions, layer: Int = 0) =
        renderPointedOval(
            aabb.getCenter(),
            Distance(aabb.size.width / 2.0f),
            Distance(aabb.size.height / 2.0f),
            options,
            layer,
        )

    fun renderRoundedPolygon(polygon: Polygon2d, options: RenderOptions, layer: Int = 0)

    fun renderPolygon(polygon: Polygon2d, options: RenderOptions, layer: Int = 0)

    fun renderRectangle(aabb: AABB, options: RenderOptions, layer: Int = 0)

    fun renderText(text: String, center: Point2d, orientation: Orientation, options: TextOptions, layer: Int = 0)

}