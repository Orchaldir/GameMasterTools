package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d

interface Renderer {

    fun renderCircle(center: Point2d, radius: UInt, options: RenderOptions)

    fun renderCircle(aabb: AABB, options: RenderOptions) =
        renderCircle(aabb.getCenter(), aabb.getInnerRadius(), options)

    fun renderRectangle(aabb: AABB, options: RenderOptions)

}