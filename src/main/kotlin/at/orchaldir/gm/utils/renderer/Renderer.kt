package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.utils.renderer.model.TextOptions

interface Renderer {

    fun renderCircle(center: Point2d, radius: Distance, options: RenderOptions): Renderer

    fun renderCircle(aabb: AABB, options: RenderOptions): Renderer =
        renderCircle(aabb.getCenter(), aabb.getInnerRadius(), options)

    fun renderCircleArc(
        center: Point2d,
        radius: Distance,
        offset: Orientation,
        angle: Orientation,
        options: RenderOptions,
    ): Renderer

    fun renderEllipse(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions): Renderer

    fun renderEllipse(aabb: AABB, options: RenderOptions): Renderer =
        renderEllipse(
            aabb.getCenter(),
            Distance(aabb.size.width / 2.0f),
            Distance(aabb.size.height / 2.0f),
            options,
        )

    fun renderLine(line: List<Point2d>, options: LineOptions): Renderer

    fun renderPointedOval(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions): Renderer

    fun renderPointedOval(aabb: AABB, options: RenderOptions): Renderer =
        renderPointedOval(
            aabb.getCenter(),
            Distance(aabb.size.width / 2.0f),
            Distance(aabb.size.height / 2.0f),
            options,
        )

    fun renderRoundedPolygon(polygon: Polygon2d, options: RenderOptions): Renderer

    fun renderPolygon(polygon: Polygon2d, options: RenderOptions): Renderer

    fun renderRectangle(aabb: AABB, options: RenderOptions): Renderer

    fun renderText(text: String, center: Point2d, orientation: Orientation, options: TextOptions): Renderer

}