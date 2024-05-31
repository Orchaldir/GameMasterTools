package at.orchaldir.gm.visualization

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.renderer.LineOptions

data class RenderConfig(
    val padding: Distance,
    val line: LineOptions,
)
