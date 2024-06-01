package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.LineOptions
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.EarConfig
import at.orchaldir.gm.visualization.character.EyesConfig
import at.orchaldir.gm.visualization.character.HeadConfig

val RENDER_CONFIG = RenderConfig(
    Distance(0.1f), LineOptions(Color.Black.toRender(), Distance(0.005f)),
    HeadConfig(
        EarConfig(SizeConfig(0.1f, 0.125f, 0.15f), Factor(3.0f)),
        EyesConfig(
            SizeConfig(0.2f, 0.3f, 0.4f),
            SizeConfig(0.3f, 0.45f, 0.5f),
            Factor(0.7f),
            Factor(0.75f),
            Factor(0.5f),
            Factor(0.2f),
        ),
        Factor(0.4f),
        Factor(0.4f),
    )
)