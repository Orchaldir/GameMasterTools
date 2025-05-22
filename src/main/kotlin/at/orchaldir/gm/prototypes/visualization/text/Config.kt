package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.book.LeatherBindingStyle
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig.Companion.fromPercentages
import at.orchaldir.gm.visualization.SizeConfig.Companion.withFactor
import at.orchaldir.gm.visualization.text.LeatherBindingConfig
import at.orchaldir.gm.visualization.text.TextRenderConfig

val TEXT_CONFIG = TextRenderConfig(
    fromMillimeters(20),
    LineOptions(Color.Black.toRender(), fromMillimeters(1)),
    mapOf(
        LeatherBindingStyle.ThreeQuarter to createConfig(40, 40),
        LeatherBindingStyle.Half to createConfig(30, 30),
        LeatherBindingStyle.Quarter to createConfig(20, 20),
    ),
    fromPercentages(2, 3, 4),
    withFactor(0.015f, 0.02f, 0.025f),
    fromPercentages(10, 15, 20),
    listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        "Nulla dapibus mauris vitae metus gravida sodales.",
        "Fusce vitae dapibus sapien, nec eleifend quam.",
        "Vestibulum ac malesuada nisi.",
        "Integer tempor, libero et pretium accumsan, elit risus accumsan risus, eget tempus orci ligula eu lectus.",
        "Suspendisse ut aliquet libero.",
        "Nam nec metus eu magna scelerisque iaculis et id tortor.",
        "Maecenas non scelerisque eros.",
        "Nunc quis lacinia purus.",
        "Aenean porta et sapien non aliquam.",
        "Etiam vestibulum tempus quam sit amet cursus.",
        "Proin convallis tincidunt dolor vitae lacinia.",
        "Pellentesque eu elit blandit, porta orci vel, vulputate purus.",
        "Sed maximus venenatis velit.",
        "Maecenas pellentesque leo sed nisl ultricies porta.",
    ),
    fromPercentage(33),
)

private fun createConfig(spine: Int, corner: Int) = LeatherBindingConfig(fromPercentage(spine), fromPercentage(corner))