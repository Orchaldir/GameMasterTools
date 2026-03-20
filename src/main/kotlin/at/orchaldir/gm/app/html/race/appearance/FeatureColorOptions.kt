package at.orchaldir.gm.app.html.race.appearance

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.appearance.selectCrownLength
import at.orchaldir.gm.app.html.character.appearance.selectHornLength
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.race.appearance.editHair
import at.orchaldir.gm.app.html.race.appearance.editSkin
import at.orchaldir.gm.app.html.race.appearance.editSkinInternal
import at.orchaldir.gm.app.html.race.appearance.parseHairOptions
import at.orchaldir.gm.app.html.race.appearance.parseSkinOptions
import at.orchaldir.gm.app.html.race.appearance.showHair
import at.orchaldir.gm.app.html.race.appearance.showSkin
import at.orchaldir.gm.app.html.race.appearance.showSkinInternal
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.EyeType
import at.orchaldir.gm.core.model.character.appearance.eye.EyesLayout
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape
import at.orchaldir.gm.core.model.character.appearance.hair.HairColorType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColorEnum
import at.orchaldir.gm.core.model.character.appearance.horn.HornsLayout
import at.orchaldir.gm.core.model.character.appearance.horn.SimpleHornType
import at.orchaldir.gm.core.model.character.appearance.horn.VALID_CROWN_HORNS
import at.orchaldir.gm.core.model.character.appearance.mouth.BeakShape
import at.orchaldir.gm.core.model.character.appearance.mouth.MouthType
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.TailsLayout
import at.orchaldir.gm.core.model.character.appearance.wing.DEFAULT_BIRD_COLOR
import at.orchaldir.gm.core.model.character.appearance.wing.DEFAULT_BUTTERFLY_COLOR
import at.orchaldir.gm.core.model.character.appearance.wing.WingType
import at.orchaldir.gm.core.model.character.appearance.wing.WingsLayout
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.selector.race.getRaces
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.h3

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
