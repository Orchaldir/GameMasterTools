package at.orchaldir.gm.prototypes.visualization

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.LineOptions
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.*
import at.orchaldir.gm.visualization.character.beard.BeardConfig

val RENDER_CONFIG = RenderConfig(
    Distance(0.1f), LineOptions(Color.Black.toRender(), Distance(0.005f)),
    BodyConfig(
        Factor(0.1f),
        Factor(0.25f),
        Factor(0.09f),
        Factor(0.14f),
        Factor(0.42f),
        Factor(0.35f),
        Factor(0.255f),
        SizeConfig(0.8f, 1.0f, 1.2f),
    ),
    HeadConfig(
        BeardConfig(Factor(0.8f), Factor(0.05f), Factor(0.1f), Factor(0.01f)),
        EarConfig(SizeConfig(0.1f, 0.125f, 0.15f), Factor(3.0f)),
        Factor(0.45f),
        EyesConfig(
            SizeConfig(0.2f, 0.3f, 0.4f),
            SizeConfig(0.3f, 0.45f, 0.5f),
            Factor(0.7f),
            Factor(0.75f),
            Factor(0.5f),
            Factor(0.2f),
        ),
        Factor(0.45f),
        HairConfig(
            Factor(1.3f),
            Factor(-0.2f),
            Factor(0.3f),
            Factor(-0.2f),
            Factor(0.15f),
        ),
        Factor(0.25f),
        MouthConfig(SizeConfig(0.3f, 0.35f, 0.4f), Factor(0.04f), Factor(0.1f)),
        Factor(0.75f),
    )
)