package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.EyeType
import at.orchaldir.gm.core.model.character.appearance.eye.EyesLayout
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
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
        parseOneOf(parameters, FUR_COLOR, Color::valueOf, Color.entries),
        parseOneOf(parameters, SCALE_COLOR, Color::valueOf, Color.entries),
        parseOneOf(parameters, NORMAL_SKIN_COLOR, SkinColor::valueOf, SkinColor.entries),
        parseOneOf(parameters, EXOTIC_SKIN_COLOR, Color::valueOf, Color.entries),
        parseOneOf(parameters, combine(EARS, LAYOUT), EarsLayout::valueOf),
        parseOneOf(parameters, EAR_SHAPE, EarShape::valueOf, EarShape.entries),
        parseOneOf(parameters, combine(EYES, LAYOUT), EyesLayout::valueOf),
        parseEyeOptions(parameters),
        parseFootOptions(parameters),
        parseHairOptions(parameters),
        parseOneOf(parameters, MOUTH_TYPE, MouthType::valueOf),
        parseWingOptions(parameters),
    )
}

private fun parseEyeOptions(parameters: Parameters): EyeOptions {
    val eyeTypes = parseOneOf(parameters, combine(EYES, TYPE), EyeType::valueOf, EyeType.entries)
    val eyeShapes = parseOneOf(parameters, EYE_SHAPE, EyeShape::valueOf, EyeShape.entries)
    val pupilShapes = parseOneOf(parameters, PUPIL_SHAPE, PupilShape::valueOf, PupilShape.entries)
    val eyeColors = parseOneOf(parameters, PUPIL_COLOR, Color::valueOf, Color.entries)
    val scleraColors = parseOneOf(parameters, SCLERA_COLOR, Color::valueOf, Color.entries)

    return EyeOptions(eyeTypes, eyeShapes, eyeColors, pupilShapes, scleraColors)
}

private fun parseFootOptions(parameters: Parameters): FootOptions {
    val footTypes = parseOneOf(parameters, FOOT, FootType::valueOf)
    val clawNumber = parseInt(parameters, combine(FOOT, CLAWS, NUMBER), DEFAULT_CLAW_NUMBER)
    val clawColors = parseOneOf(parameters, combine(FOOT, CLAWS, COLOR), Color::valueOf, setOf(DEFAULT_CLAW_COLOR))
    val clawSizes = parseOneOf(parameters, combine(FOOT, CLAWS, SIZE), Size::valueOf, setOf(DEFAULT_CLAW_SIZE))

    return FootOptions(
        footTypes,
        clawNumber,
        clawColors,
        clawSizes,
    )
}

private fun parseHairOptions(parameters: Parameters) = HairOptions(
    parseOneOf(parameters, BEARD, BeardType::valueOf),
    parseOneOf(parameters, HAIR_TYPE, HairType::valueOf),
    parseOneOf(parameters, HAIR_COLOR, Color::valueOf, Color.entries),
)

private fun parseWingOptions(parameters: Parameters) = WingOptions(
    parseOneOf(parameters, combine(WING, LAYOUT), WingsLayout::valueOf),
    parseOneOf(parameters, combine(WING, TYPE), WingType::valueOf),
    parseOneOf(parameters, combine(WING, BAT, COLOR), Color::valueOf, setOf(DEFAULT_BAT_COLOR)),
    parseOneOf(parameters, combine(WING, BIRD, COLOR), Color::valueOf, setOf(DEFAULT_BIRD_COLOR)),
    parseOneOf(parameters, combine(WING, BUTTERFLY, COLOR), Color::valueOf, setOf(DEFAULT_BUTTERFLY_COLOR)),
)