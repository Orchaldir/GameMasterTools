package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.OneEye
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.item.equipment.style.NecklaceLength.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPermille
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RGB
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.SizeConfig.Companion.fromPercentages
import at.orchaldir.gm.visualization.SizeConfig.Companion.withFactor
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.appearance.*
import at.orchaldir.gm.visualization.character.appearance.beard.BeardConfig
import at.orchaldir.gm.visualization.character.appearance.hair.HairConfig
import at.orchaldir.gm.visualization.character.appearance.horn.HornConfig
import at.orchaldir.gm.visualization.character.appearance.mouth.MouthConfig
import at.orchaldir.gm.visualization.character.equipment.*
import at.orchaldir.gm.visualization.character.equipment.part.NecklineConfig
import at.orchaldir.gm.visualization.character.equipment.part.OpeningConfig

val CHARACTER_CONFIG = CharacterRenderConfig(
    fromMillimeters(500),
    LineOptions(Color.Black.toRender(), fromMillimeters(5)),
    BodyConfig(
        fromPercentage(10),
        FootConfig(
            fromPercentage(9),
            fromPercentage(50),
            withFactor(0.375f, 0.5f, 0.625f),
        ),
        fromPercentage(7),
        fromPercentage(25),
        fromPercentage(75),
        fromPercentage(80),
        fromPercentage(14),
        fromPercentage(25),
        TailConfig(
            fromPercentages(5, 7, 9),
            fromPercentage(40),
            fromPercentage(55),
        ),
        fromPercentage(42),
        fromPercentage(35),
        fromPermille(255),
        fromPercentage(120),
        withFactor(0.8f, 1.0f, 1.2f),
    ),
    EquipmentConfig(
        BeltConfig(
            fromPercentage(10),
            fromPercentages(12, 16, 20),
            SizeConfig(
                fromPercentage(1),
                fromPercentage(2),
                fromPermille(25),
            ),
            fromPercentage(85),
        ),
        CoatConfig(
            fromPercentage(10),
        ),
        EarringConfig(
            fromPercentages(20, 25, 30),
            fromPercentages(10, 15, 20),
        ),
        EyePatchConfig(
            fromPercentages(2, 4, 6),
            fromPercentage(20),
            fromPercentage(15),
            fromPercentage(20),
        ),
        FootwearConfig(
            fromPercentage(50),
            fromPercentage(70),
            fromPercentage(90),
            fromPermille(25),
            fromPercentage(2),
        ),
        GlassesConfig(
            fromPercentages(25, 35, 40),
            fromPercentage(2),
            fromPercentage(1),
        ),
        HatConfig(
            fromPercentage(10),
            fromPercentage(40),
            fromPercentage(65),
            fromPercentage(85),
            fromPercentage(5),
            fromPercentage(8),
            fromPercentage(130),
            fromPercentage(160),
            fromPercentage(170),
        ),
        NecklaceConfig(
            fromPercentage(20),
            mapOf(
                Collar to fromPercentage(0),
                Choker to fromPercentage(5),
                Princess to fromPercentage(20),
                Matinee to fromPercentage(30),
                Opera to fromPercentage(50),
                Rope to fromPercentage(60),
            ),
            fromPercentages(5, 10, 15),
            fromPercentages(150, 200, 250),
            fromPercentages(2, 3, 4),
            fromPercentages(1, 2, 3),
            3,
        ),
        NecklineConfig(
            fromPercentage(10),
            fromPercentage(20),
            fromPercentage(40),
            fromPercentage(60),
            fromPercentage(30),
            fromPercentage(50),
        ),
        OpeningConfig(
            withFactor(0.01f, 0.015f, 0.02f),
            withFactor(0.2f, 0.3f, 0.4f),
            fromPercentage(1),
        ),
        PantsConfig(
            fromPercentage(50),
            fromPercentage(30),
            fromPercentage(5),
        ),
        SkirtConfig(
            fromPercentage(40),
            fromPercentage(90),
            fromPercentage(140),
            fromPercentage(180),
            fromPercentage(5),
        ),
        TieConfig(
            fromPercentage(10),
            fromPercentages(40, 50, 60),
            fromPercentage(40),
            fromPercentage(15),
            fromPercentage(10),
            fromPercentages(20, 25, 30),
            fromPercentage(85),
        ),
    ),
    HeadConfig(
        BeardConfig(
            fromPercentage(80),
            fromPercentage(5),
            fromPercentage(10),
            fromPercentage(1),
            fromPercentage(150),
        ),
        EarConfig(
            withFactor(0.1f, 0.125f, 0.15f),
            fromPercentage(300),
        ),
        fromPercentage(45),
        EyesConfig(
            fromPercentage(35),
            fromPercentage(40),
            withFactor(0.2f, 0.3f, 0.4f),
            withFactor(0.3f, 0.45f, 0.5f),
            fromPercentage(70),
            fromPercentage(75),
            fromPercentage(50),
            fromPercentage(20),
        ),
        HairConfig(
            fromPercentage(130),
            fromPercentage(-20),
            fromPercentage(30),
            fromPercentage(-20),
            fromPercentage(15),
            fromPercentage(105),
            fromPercentage(10),
            mapOf(
                HairLength.Ankle to fromPercentage(95),
                HairLength.Knee to fromPercentage(80),
                HairLength.Classic to fromPercentage(60),
                HairLength.Waist to fromPercentage(40),
                HairLength.MidBack to fromPercentage(20),
            ),
            fromPercentage(30),
            fromPercentage(30),
            fromPercentage(60),
            fromPercentages(25, 30, 35),
        ),
        fromPercentage(25),
        fromPercentage(20),
        HornConfig(
            fromPercentage(20),
            complexHorn(HornPosition.Top, 10, StraightHorn, 20),
            complexHorn(HornPosition.Top, 0, CurvedHorn(fromDegrees(270)), 15),
            complexHorn(HornPosition.Top, 20, SpiralHorn(4, fromPercentage(10)), 20),
            complexHorn(HornPosition.Side, 0, CurvedHorn(fromDegrees(-120)), 20),
        ),
        MouthConfig(
            withFactor(0.3f, 0.35f, 0.4f),
            fromPercentage(4),
            fromPercentage(10),
            fromPercentage(75),
        ),
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

val EYES: List<Pair<String, Eyes>> = listOf(
    createOneEye(Size.Small),
    createOneEye(Size.Medium),
    createOneEye(Size.Large),
    Pair("Two", TwoEyes()),
)

private fun createOneEye(size: Size) = Pair("One $size", OneEye(size = size))

private fun complexHorn(position: HornPosition, degree: Long, shape: HornShape, width: Int) = ComplexHorn(
    fromPercentage(100),
    fromPercentage(width),
    position,
    fromDegrees(degree),
    shape,
)