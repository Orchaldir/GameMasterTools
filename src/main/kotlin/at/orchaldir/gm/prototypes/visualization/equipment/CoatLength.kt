package at.orchaldir.gm.prototypes.visualization.equipment

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Color.Blue
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.appearance.Solid
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.Coat
import at.orchaldir.gm.core.model.item.style.Button
import at.orchaldir.gm.core.model.item.style.ButtonColumn
import at.orchaldir.gm.core.model.item.style.DoubleBreasted
import at.orchaldir.gm.core.model.item.style.OuterwearLength
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.addNames
import at.orchaldir.gm.prototypes.visualization.character.renderTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderTable(
        "coat-length.svg",
        RENDER_CONFIG,
        addNames(OuterwearLength.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, length ->
        Pair(
            createAppearance(distance, shape),
            listOf(
                Coat(
                    length,
                    openingStyle = DoubleBreasted(ButtonColumn(Button(Size.Medium, Color.Gold), 5u)),
                    fill = Solid(Blue)
                )
            )
        )
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, Size.Medium),
        Head(),
        distance,
    )