package at.orchaldir.gm.visualization.grammar

data class Borders(
    val bottom: Boolean = true,
    val left: Boolean = true,
    val right: Boolean = true,
    val top: Boolean = true,
) {
    constructor(isBorder: Boolean): this(isBorder, isBorder, isBorder, isBorder)
}
