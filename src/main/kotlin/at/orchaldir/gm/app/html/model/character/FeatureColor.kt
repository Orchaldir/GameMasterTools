package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.race.appearance.FeatureColorOptions
import io.ktor.http.*
import kotlinx.html.FORM

// edit

fun FORM.selectFeatureColor(
    options: FeatureColorOptions,
    color: FeatureColor,
    param: String,
) {
    if (options.colorType == FeatureColorType.Overwrite && color is OverwriteFeatureColor) {
        selectColor("Color", combine(param, COLOR), options.colors, color.color)
    }
}

// parse

fun parseFeatureColor(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    options: FeatureColorOptions,
    param: String,
) = when (options.colorType) {
    FeatureColorType.Hair -> ReuseHairColor
    FeatureColorType.Overwrite -> OverwriteFeatureColor(
        parseAppearanceColor(
            parameters,
            param,
            config,
            options.colors
        )
    )

    FeatureColorType.Skin -> ReuseSkinColor
}
