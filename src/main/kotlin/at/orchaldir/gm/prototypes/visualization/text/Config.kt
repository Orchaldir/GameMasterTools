package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.book.LeatherBindingStyle
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig.Companion.fromPercentages
import at.orchaldir.gm.visualization.SizeConfig.Companion.withFactor
import at.orchaldir.gm.visualization.text.LeatherBindingConfig
import at.orchaldir.gm.visualization.text.TextRenderConfig

val TEXT_CONFIG = TextRenderConfig(
    fromMillimeters(50),
    LineOptions(Color.Black.toRender(), fromMillimeters(1)),
    mapOf(
        LeatherBindingStyle.ThreeQuarter to createConfig(40, 40),
        LeatherBindingStyle.Half to createConfig(30, 30),
        LeatherBindingStyle.Quarter to createConfig(20, 20),
    ),
    fromPercentages(2, 3, 4),
    withFactor(0.015f, 0.02f, 0.025f),
    fromPercentages(10, 15, 20),
)

private fun createConfig(spine: Int, corner: Int) = LeatherBindingConfig(fromPercentage(spine), fromPercentage(corner))