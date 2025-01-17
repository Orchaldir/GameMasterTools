package at.orchaldir.gm.utils.renderer.model

data class RenderTextOptions(
    val renderOptions: RenderOptions,
    val size: Float,
) {

    constructor(color: RenderColor, size: Float) : this(NoBorder(RenderSolid(color)), size)
}
