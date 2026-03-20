package at.orchaldir.gm.prototypes.visualization

import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColorEnum
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RGB
import at.orchaldir.gm.visualization.ColorRenderConfig

val COLORS_CONFIG = ColorRenderConfig(
    LineOptions(Color.Black.toRender(), fromMillimeters(5)),
    mapOf(
        NormalHairColorEnum.LightestBlond to RGB(243, 234, 170),
        NormalHairColorEnum.VeryLightBlond to RGB(233, 228, 142),
        NormalHairColorEnum.LightBlond to RGB(219, 188, 104),
        NormalHairColorEnum.Blond to RGB(206, 147, 79),
        NormalHairColorEnum.DarkBlond to RGB(194, 117, 62),
        NormalHairColorEnum.LightBrown to RGB(186, 93, 58),
        NormalHairColorEnum.MediumBrown to RGB(138, 77, 35),
        NormalHairColorEnum.DarkBrown to RGB(77, 51, 48),
        NormalHairColorEnum.Black to RGB(10, 10, 10),
        NormalHairColorEnum.Orange to RGB(255, 125, 0),
        NormalHairColorEnum.Red to RGB(255, 0, 0),
        NormalHairColorEnum.Auburn to RGB(126, 37, 31),
    ),
    mapOf(
        SkinColor.Fair to RGB(254, 228, 208),
        SkinColor.Light to RGB(232, 198, 175),
        SkinColor.Medium to RGB(200, 158, 134),
        SkinColor.Tan to RGB(175, 118, 88),
        SkinColor.Dark to RGB(156, 89, 60),
        SkinColor.VeryDark to RGB(122, 68, 44),
    ),
)
