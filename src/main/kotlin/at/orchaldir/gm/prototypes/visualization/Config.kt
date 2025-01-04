package at.orchaldir.gm.prototypes.visualization

import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RGB
import at.orchaldir.gm.visualization.character.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig.Companion.withFactor
import at.orchaldir.gm.visualization.character.appearance.*
import at.orchaldir.gm.visualization.character.appearance.beard.BeardConfig
import at.orchaldir.gm.visualization.character.equipment.*
import at.orchaldir.gm.visualization.character.equipment.part.NecklineConfig
import at.orchaldir.gm.visualization.character.equipment.part.OpeningConfig

val RENDER_CONFIG = RenderConfig(
    Distance(100),
    LineOptions(Color.Black.toRender(), Distance(1)),
    BodyConfig(
        Factor(0.1f),
        Factor(0.09f),
        Factor(0.07f),
        Factor(0.25f),
        Factor(0.75f),
        Factor(0.8f),
        Factor(0.14f),
        Factor(0.25f),
        Factor(0.42f),
        Factor(0.35f),
        Factor(0.255f),
        Factor(1.2f),
        withFactor(0.8f, 1.0f, 1.2f),
    ),
    EquipmentConfig(
        CoatConfig(
            Factor(0.1f),
        ),
        FootwearConfig(
            Factor(0.5f),
            Factor(0.7f),
            Factor(0.025f),
            Factor(0.02f),
        ),
        HatConfig(
            Factor(0.1f),
            Factor(0.4f),
            Factor(0.65f),
            Factor(0.85f),
            Factor(0.05f),
            Factor(0.08f),
            Factor(1.3f),
            Factor(1.6f),
            Factor(1.7f),
        ),
        NecklineConfig(
            Factor(0.1f),
            Factor(0.2f),
            Factor(0.4f),
            Factor(0.6f),
            Factor(0.3f),
            Factor(0.5f),
        ),
        OpeningConfig(
            withFactor(0.01f, 0.015f, 0.02f),
            withFactor(0.2f, 0.3f, 0.4f),
            Factor(0.01f),
        ),
        PantsConfig(
            Factor(0.5f),
            Factor(0.3f),
            Factor(0.05f),
        ),
        SkirtConfig(
            Factor(0.4f),
            Factor(0.9f),
            Factor(1.4f),
            Factor(1.8f),
            Factor(0.05f),
        )
    ),
    HeadConfig(
        BeardConfig(Factor(0.8f), Factor(0.05f), Factor(0.1f), Factor(0.01f)),
        EarConfig(
            withFactor(0.1f, 0.125f, 0.15f),
            Factor(3.0f),
        ),
        Factor(0.45f),
        EyesConfig(
            withFactor(0.2f, 0.3f, 0.4f),
            withFactor(0.3f, 0.45f, 0.5f),
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
        Factor(0.3f),
        Factor(0.25f),
        MouthConfig(
            withFactor(0.3f, 0.35f, 0.4f),
            Factor(0.04f),
            Factor(0.1f),
        ),
        Factor(0.75f),
    ),
    mapOf(
        SkinColor.Fair to RGB(254, 228, 208),
        SkinColor.Light to RGB(232, 198, 175),
        SkinColor.Medium to RGB(175, 118, 88),
        SkinColor.Tan to RGB(156, 89, 60),
        SkinColor.Dark to RGB(122, 68, 44),
        SkinColor.VeryDark to RGB(58, 26, 13),
    )
)