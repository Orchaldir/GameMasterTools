package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.LeatherBindingType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.text.TextRenderConfig
import at.orchaldir.gm.visualization.text.LeatherBindingConfig

val TEXT_CONFIG = TextRenderConfig(
    Distance(100),
    LineOptions(Color.Black.toRender(), Distance(1)),
    mapOf(
        LeatherBindingType.ThreeQuarter to LeatherBindingConfig(Factor(0.4f), Factor(0.4f)),
        LeatherBindingType.Half to LeatherBindingConfig(Factor(0.3f), Factor(0.3f)),
        LeatherBindingType.Quarter to LeatherBindingConfig(Factor(0.2f), Factor(0.2f)),
    ),
    SizeConfig(Factor(0.015f), Factor(0.02f), Factor(0.025f)),
    SizeConfig(Factor(0.1f), Factor(0.15f), Factor(0.2f)),
)