package at.orchaldir.gm.app.html.race.appearance

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
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
import at.orchaldir.gm.core.model.character.appearance.mouth.BeakShape
import at.orchaldir.gm.core.model.character.appearance.mouth.MouthType
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.TailsLayout
import at.orchaldir.gm.core.model.character.appearance.wing.DEFAULT_BIRD_COLOR
import at.orchaldir.gm.core.model.character.appearance.wing.DEFAULT_BUTTERFLY_COLOR
import at.orchaldir.gm.core.model.character.appearance.wing.WingType
import at.orchaldir.gm.core.model.character.appearance.wing.WingsLayout
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h3

private fun requiresHairColor(appearance: RaceAppearance) =
    appearance.hair.beardTypes.isAvailable(BeardType.Normal) ||
            appearance.hair.hairTypes.isAvailable(HairType.Normal)

// show

fun HtmlBlockTag.showHair(appearance: RaceAppearance) {
    h3 { +"Hair" }

    showRarityMap("Beard", appearance.hair.beardTypes)
    showRarityMap("Hair", appearance.hair.hairTypes)

    if (requiresHairColor(appearance)) {
        showHairColorOptions(appearance.hair.colors, "Hair Colors")
    }
}

fun HtmlBlockTag.showHairColorOptions(options: HairColorOptions, label: String) {
    showDetails(label) {
        showRarityMap("Types", options.types)

        if (options.types.contains(HairColorType.Normal)) {
            showHairColorRarityMap(CHARACTER_CONFIG, "Normal Colors", options.normal)
        }
        if (options.types.contains(HairColorType.Exotic) || options.types.contains(HairColorType.Stripped)) {
            showColorRarityMap("Exotics Colors", options.exotic)
        }
    }
}

// edit

fun HtmlBlockTag.editHair(appearance: RaceAppearance) {
    h3 { +"Hair" }

    selectRarityMap("Beard", BEARD, appearance.hair.beardTypes)
    selectRarityMap("Hair", HAIR, appearance.hair.hairTypes)

    if (requiresHairColor(appearance)) {
        editHairColorOptions(
            appearance.hair.colors,
            HAIR,
            "Hair Colors",
            ALLOWED_HAIR_COLOR_TYPES,
        )
    }
}

fun HtmlBlockTag.editHairColorOptions(
    options: HairColorOptions,
    param: String,
    label: String,
    allowedTypes: Set<HairColorType>,
) {
    showDetails(label, true) {
        selectRarityMap(
            "Types",
            combine(param, COLOR, TYPE),
            options.types,
            allowedTypes,
        )

        if (options.types.contains(HairColorType.Normal)) {
            selectHairColorRarityMap(
                CHARACTER_CONFIG,
                "Normal Colors",
                combine(param, COLOR),
                options.normal,
            )
        }
        if (options.types.contains(HairColorType.Exotic) || options.types.contains(HairColorType.Stripped)) {
            selectColorRarityMap(
                "Exotic Colors",
                combine(param, EXOTIC, COLOR),
                options.exotic,
            )
        }
    }
}

// parse

fun parseHairOptions(parameters: Parameters) = HairOptions(
    parseOneOf(parameters, BEARD, BeardType::valueOf),
    parseOneOf(parameters, HAIR, HairType::valueOf),
    parseHairColorOptions(parameters, HAIR),
)

fun parseHairColorOptions(parameters: Parameters, param: String) = HairColorOptions(
    parseOneOf(
        parameters,
        combine(param, COLOR, TYPE),
        HairColorType::valueOf,
        setOf(HairColorType.Normal),
    ),
    parseOneOf(
        parameters,
        combine(param, COLOR),
        NormalHairColorEnum::valueOf,
        DEFAULT_NORMAL_HAIR_COLORS,
    ),
    parseColorOneOf(
        parameters,
        combine(param, EXOTIC, COLOR),
        DEFAULT_EXOTIC_HAIR_COLORS,
    ),
)
