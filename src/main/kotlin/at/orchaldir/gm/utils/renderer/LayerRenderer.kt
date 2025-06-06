package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Line2d
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions

interface LayerRenderer {

    fun renderCircle(center: Point2d, radius: Distance, options: RenderOptions): LayerRenderer

    fun renderCircle(aabb: AABB, options: RenderOptions): LayerRenderer =
        renderCircle(aabb.getCenter(), aabb.getInnerRadius(), options)

    fun renderCircleArc(
        center: Point2d,
        radius: Distance,
        offset: Orientation,
        angle: Orientation,
        options: RenderOptions,
    ): LayerRenderer

    fun renderEllipse(
        center: Point2d,
        orientation: Orientation,
        radiusX: Distance,
        radiusY: Distance,
        options: RenderOptions,
    ): LayerRenderer

    fun renderEllipse(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions): LayerRenderer

    fun renderEllipse(aabb: AABB, options: RenderOptions): LayerRenderer =
        renderEllipse(
            aabb.getCenter(),
            aabb.size.width / 2.0f,
            aabb.size.height / 2.0f,
            options,
        )

    fun renderLine(line: Line2d, options: LineOptions) = renderLine(line.points, options)

    fun renderLine(line: List<Point2d>, options: LineOptions): LayerRenderer

    fun renderPointedOval(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions): LayerRenderer

    fun renderPointedOval(aabb: AABB, options: RenderOptions): LayerRenderer =
        renderPointedOval(
            aabb.getCenter(),
            aabb.size.width / 2.0f,
            aabb.size.height / 2.0f,
            options,
        )

    fun renderRoundedPolygon(polygon: Polygon2d, options: RenderOptions): LayerRenderer

    fun renderPolygon(polygon: Polygon2d, options: RenderOptions): LayerRenderer

    fun renderPolygonWithHole(polygon: Polygon2d, hole: Polygon2d, options: RenderOptions): LayerRenderer
    fun renderRoundedPolygonWithHole(polygon: Polygon2d, hole: Polygon2d, options: RenderOptions): LayerRenderer
    fun renderPolygonWithRoundedHole(polygon: Polygon2d, hole: Polygon2d, options: RenderOptions): LayerRenderer
    fun renderRoundedPolygonWithRoundedHole(polygon: Polygon2d, hole: Polygon2d, options: RenderOptions): LayerRenderer
    fun renderRoundedPolygonWithRoundedHoles(
        polygon: Polygon2d,
        holes: List<Polygon2d>,
        options: RenderOptions,
    ): LayerRenderer

    fun renderRectangle(aabb: AABB, options: RenderOptions): LayerRenderer

    fun renderHollowRectangle(
        center: Point2d,
        width: Distance,
        height: Distance,
        thickness: Distance,
        options: RenderOptions,
    ): LayerRenderer

    fun renderRing(center: Point2d, outerRadius: Distance, innerRadius: Distance, options: RenderOptions): LayerRenderer

    fun renderString(
        text: String,
        position: Point2d,
        orientation: Orientation,
        options: RenderStringOptions,
    ): LayerRenderer

    fun renderString(
        text: String,
        start: Point2d,
        width: Distance,
        options: RenderStringOptions,
    ): LayerRenderer
}