package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.HairOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseRaceAppearanceId(parameters: Parameters, param: String) = RaceAppearanceId(parseInt(parameters, param))

fun parseRaceAppearance(id: RaceAppearanceId, parameters: Parameters): RaceAppearance {
    val name = parameters.getOrFail("name")
    return RaceAppearance(
        id,
        name,
        parseOneOf(parameters, APPEARANCE, AppearanceType::valueOf),
        parseOneOf(parameters, SKIN_TYPE, SkinType::valueOf),
        parseOneOf(parameters, SCALE_COLOR, Color::valueOf, Color.entries),
        parseOneOf(parameters, NORMAL_SKIN_COLOR, SkinColor::valueOf, SkinColor.entries),
        parseOneOf(parameters, EXOTIC_SKIN_COLOR, Color::valueOf, Color.entries),
        parseOneOf(parameters, EARS_LAYOUT, EarsLayout::valueOf),
        parseOneOf(parameters, EAR_SHAPE, EarShape::valueOf, EarShape.entries),
        parseOneOf(parameters, EYES_LAYOUT, EyesLayout::valueOf),
        parseEyeOptions(parameters),
        parseHairOptions(parameters),
        parseOneOf(parameters, MOUTH_TYPE, MouthType::valueOf),
    )
}

private fun parseEyeOptions(parameters: Parameters): EyeOptions {
    val eyeShapes = parseOneOf(parameters, EYE_SHAPE, EyeShape::valueOf, EyeShape.entries)
    val pupilShapes = parseOneOf(parameters, PUPIL_SHAPE, PupilShape::valueOf, PupilShape.entries)
    val pupilColors = parseOneOf(parameters, PUPIL_COLOR, Color::valueOf, Color.entries)
    val scleraColors = parseOneOf(parameters, SCLERA_COLOR, Color::valueOf, Color.entries)

    return EyeOptions(eyeShapes, pupilShapes, pupilColors, scleraColors)
}

private fun parseHairOptions(parameters: Parameters) = HairOptions(
    parseOneOf(parameters, BEARD, BeardType::valueOf),
    parseOneOf(parameters, HAIR_TYPE, HairType::valueOf),
    parseOneOf(parameters, HAIR_COLOR, Color::valueOf, Color.entries),
)