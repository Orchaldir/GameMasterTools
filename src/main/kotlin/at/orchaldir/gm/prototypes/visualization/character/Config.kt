package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.item.equipment.style.FrameType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation.Companion.fromDegree
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RGB
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.SizeConfig.Companion.withFactor
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.appearance.*
import at.orchaldir.gm.visualization.character.appearance.beard.BeardConfig
import at.orchaldir.gm.visualization.character.appearance.horn.HornConfig
import at.orchaldir.gm.visualization.character.equipment.*
import at.orchaldir.gm.visualization.character.equipment.part.NecklineConfig
import at.orchaldir.gm.visualization.character.equipment.part.OpeningConfig

val CHARACTER_CONFIG = CharacterRenderConfig(
    Distance(100),
    LineOptions(Color.Black.toRender(), Distance(1)),
    BodyConfig(
        Factor(0.1f),
        FootConfig(
            Factor(0.09f),
            Factor(0.5f),
            withFactor(0.375f, 0.5f, 0.625f),
        ),
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
        GlassesConfig(
            SizeConfig(
                Factor(0.1f),
                Factor(0.15f),
                Factor(0.2f),
            ),
            Factor(0.02f),
            Factor(0.01f),
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
            Factor(0.45f),
            withFactor(0.2f, 0.3f, 0.4f),
            withFactor(0.3f, 0.45f, 0.5f),
            Factor(0.7f),
            Factor(0.75f),
            Factor(0.5f),
            Factor(0.2f),
        ),
        HairConfig(
            Factor(1.3f),
            Factor(-0.2f),
            Factor(0.3f),
            Factor(-0.2f),
            Factor(0.15f),
        ),
        Factor(0.3f),
        Factor(0.25f),
        HornConfig(
            Factor(0.2f),
            complexHorn(HornPosition.Top, 10.0f, StraightHorn, 0.2f),
            complexHorn(HornPosition.Top, 0.0f, CurvedHorn(fromDegree(270.0f)), 0.15f),
            complexHorn(HornPosition.Top, 20.0f, SpiralHorn(4, Factor(0.1f)), 0.2f),
            complexHorn(HornPosition.Side, 0.0f, CurvedHorn(fromDegree(-120.0f)), 0.2f),
        ),
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

private fun complexHorn(position: HornPosition, degree: Float, shape: HornShape, width: Float) = ComplexHorn(
    Factor(1.0f),
    Factor(width),
    position,
    fromDegree(degree),
    shape,
    Color.Red,
)