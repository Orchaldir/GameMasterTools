package at.orchaldir.gm.app.html.race.appearance

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.FeatureColorType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.race.appearance.FeatureColorOptions
import at.orchaldir.gm.core.model.race.appearance.HairOptions
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFeatureColor(
    call: ApplicationCall,
    state: State,
    options: FeatureColorOptions,
) {
    field("Color Type", options.type)

    if (options.type == FeatureColorType.Overwrite) {
        showDetails("Skin") {
            showSkinInternal(call, state, options.skin)
        }
    }
}

// edit

fun HtmlBlockTag.editFeatureColor(
    state: State,
    options: FeatureColorOptions,
    hairOptions: HairOptions,
    param: String,
) {
    selectValue(
        "Color Type",
        combine(param, COLOR),
        if (hairOptions.hairTypes.contains(HairType.Normal)) {
            FeatureColorType.entries
        } else {
            setOf(FeatureColorType.Overwrite, FeatureColorType.Skin)
        },
        options.type,
    )

    if (options.type == FeatureColorType.Overwrite) {
        showDetails("Skin", true) {
            editSkinInternal(state, options.skin, param)
        }
    }
}

// parse

fun parseFeatureColor(
    state: State,
    parameters: Parameters,
    param: String,
): FeatureColorOptions {
    val type = parse<FeatureColorType>(parameters, combine(param, COLOR))

    return if (type != null) {
        FeatureColorOptions(
            type,
            parseSkinOptions(state, parameters, param),
        )
    } else {
        FeatureColorOptions()
    }
}
