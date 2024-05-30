package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.Point2d

interface Renderer {

    fun renderCircle(center: Point2d, options: RenderOptions)

}