package at.orchaldir.gm.utils.renderer.svg

data class Svg(val lines: List<String>) {

    fun export() = lines.joinToString("\n")

}