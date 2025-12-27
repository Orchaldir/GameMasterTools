package at.orchaldir.gm.app.html.character.appearance

import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.race.appearance.FeatureColorOptions
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show


// edit

fun HtmlBlockTag.selectFeatureColor(
    state: State,
    options: FeatureColorOptions,
    color: FeatureColor,
    param: String,
) {
    if (options.type == FeatureColorType.Overwrite && color is OverwriteFeatureColor) {
        editSkin(state, options.skin, color.skin, param)
    }
}

// parse

fun parseFeatureColor(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    options: FeatureColorOptions,
    param: String,
) = when (options.type) {
    FeatureColorType.Hair -> ReuseHairColor
    FeatureColorType.Overwrite -> OverwriteFeatureColor(
        parseSkin(parameters, config, options.skin, param)
    )

    FeatureColorType.Skin -> ReuseSkinColor
}
