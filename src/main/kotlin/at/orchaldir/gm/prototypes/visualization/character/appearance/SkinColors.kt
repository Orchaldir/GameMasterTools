package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.renderTableWithNames
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.NoBorder

fun main() {
    val colors = listOf(
        SkinColor.entries.map { color ->
            Pair(color.name, color)
        }.toList(),
    )

    renderTableWithNames("skin_colors.svg", Size2d.fromMeters(1.0f), colors) { aabb, renderer, color ->
        renderer.getLayer().renderRectangle(aabb, NoBorder(CHARACTER_CONFIG.getSkinColor(color)))
    }
}
