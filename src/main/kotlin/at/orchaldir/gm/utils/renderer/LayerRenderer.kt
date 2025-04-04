package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.unit.Distance
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

    fun renderDiamond(aabb: AABB, options: RenderOptions): LayerRenderer

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
            Distance.fromMeters(aabb.size.width / 2.0f),
            Distance.fromMeters(aabb.size.height / 2.0f),
            options,
        )

    fun renderLine(line: List<Point2d>, options: LineOptions): LayerRenderer

    fun renderPointedOval(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions): LayerRenderer

    fun renderPointedOval(aabb: AABB, options: RenderOptions): LayerRenderer =
        renderPointedOval(
            aabb.getCenter(),
            Distance.fromMeters(aabb.size.width / 2.0f),
            Distance.fromMeters(aabb.size.height / 2.0f),
            options,
        )

    fun renderRoundedPolygon(polygon: Polygon2d, options: RenderOptions): LayerRenderer

    fun renderPolygon(polygon: Polygon2d, options: RenderOptions): LayerRenderer

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

    fun renderTeardrop(aabb: AABB, options: RenderOptions): LayerRenderer
}