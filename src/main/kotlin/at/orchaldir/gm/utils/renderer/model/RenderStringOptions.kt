package at.orchaldir.gm.utils.renderer.model

data class RenderStringOptions(
    val renderOptions: RenderOptions,
    val size: Float,
    val verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
) {

    constructor(
        color: RenderColor,
        size: Float,
        verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
    ) : this(NoBorder(RenderSolid(color)), size, verticalAlignment)
}
