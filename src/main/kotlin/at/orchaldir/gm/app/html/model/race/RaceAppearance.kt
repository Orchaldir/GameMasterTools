package at.orchaldir.gm.app.html.model.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.character.selectCrownLength
import at.orchaldir.gm.app.html.model.character.selectHornLength
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseFactor
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOneOf
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.EyeType
import at.orchaldir.gm.core.model.character.appearance.eye.EyesLayout
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.character.appearance.horn.HornsLayout
import at.orchaldir.gm.core.model.character.appearance.horn.SimpleHornType
import at.orchaldir.gm.core.model.character.appearance.horn.VALID_CROWN_HORNS
import at.orchaldir.gm.core.model.character.appearance.mouth.BeakShape
import at.orchaldir.gm.core.model.character.appearance.mouth.MouthType
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.util.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h3

private fun requiresHairColor(appearance: RaceAppearance) =
    appearance.hairOptions.beardTypes.isAvailable(BeardType.Normal) ||
            appearance.hairOptions.hairTypes.isAvailable(HairType.Normal)

private fun requiresNormalHorns(appearance: RaceAppearance) =
    appearance.hornOptions.layouts.isAvailable(HornsLayout.Two) ||
            appearance.hornOptions.layouts.isAvailable(HornsLayout.Different)

// show

fun HtmlBlockTag.showRaceAppearance(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    showRarityMap("Type", appearance.appearanceTypes)

    showEars(appearance)
    showEyes(appearance, eyeOptions)
    showFeet(appearance)
    showHair(appearance)
    showHorns(appearance)
    showMouth(appearance.mouthOptions)
    showSkin(appearance)
    showWings(appearance)
}

private fun HtmlBlockTag.showEars(appearance: RaceAppearance) {
    h3 { +"Ears" }

    showRarityMap("Layout", appearance.earsLayout)

    if (appearance.earsLayout.isAvailable(EarsLayout.NormalEars)) {
        showRarityMap("Ear Shapes", appearance.earShapes)
    }
}

private fun HtmlBlockTag.showEyes(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    h3 { +"Eyes" }

    showRarityMap("Layout", appearance.eyesLayout)

    if (!appearance.eyesLayout.isAvailable(EyesLayout.NoEyes)) {
        showRarityMap("Eye Types", eyeOptions.eyeTypes)
        showRarityMap("Eye Shapes", eyeOptions.eyeShapes)
        showRarityMap("Eye Colors", eyeOptions.eyeColors)

        if (eyeOptions.eyeTypes.isAvailable(EyeType.Normal)) {
            showRarityMap("Pupil Shape", eyeOptions.pupilShapes)
            showRarityMap("Sclera Colors", eyeOptions.scleraColors)
        }
    }
}

private fun HtmlBlockTag.showFeet(appearance: RaceAppearance) {
    h3 { +"Feet" }

    showRarityMap("Type", appearance.footOptions.footTypes)

    if (appearance.footOptions.footTypes.isAvailable(FootType.Clawed)) {
        field("Number of Claws", appearance.footOptions.clawNumber)
        showRarityMap("Claw Color", appearance.footOptions.clawColors)
        showRarityMap("Claw Size", appearance.footOptions.clawSizes)
    }
}

private fun HtmlBlockTag.showHair(appearance: RaceAppearance) {
    h3 { +"Hair" }

    showRarityMap("Beard", appearance.hairOptions.beardTypes)
    showRarityMap("Hair", appearance.hairOptions.hairTypes)

    if (requiresHairColor(appearance)) {
        showRarityMap("Colors", appearance.hairOptions.colors)
    }
}

private fun HtmlBlockTag.showHorns(appearance: RaceAppearance) {
    h3 { +"Horns" }

    showRarityMap("Layouts", appearance.hornOptions.layouts)

    val requiresNormalHorns = requiresNormalHorns(appearance)
    val requiresCrown = appearance.hornOptions.layouts.isAvailable(HornsLayout.Crown)

    if (requiresNormalHorns) {
        showDetails("Simple Horns") {
            showRarityMap("Horn Types", appearance.hornOptions.simpleTypes)
            field("Horn Length", appearance.hornOptions.simpleLength)
        }
    }

    if (requiresCrown) {
        showDetails("Crown") {
            field("Horn Length", appearance.hornOptions.crownLength)
            showRarityMap("Horns in Front", appearance.hornOptions.crownFront) {
                +it.toString()
            }
            showRarityMap("Horns in Back", appearance.hornOptions.crownFront) {
                +it.toString()
            }
        }

    }

    if (requiresNormalHorns || requiresCrown) {
        showRarityMap("Colors", appearance.hornOptions.colors)
    }
}

private fun HtmlBlockTag.showMouth(mouthOptions: MouthOptions) {
    h3 { +"Mouth" }

    showRarityMap("Types", mouthOptions.mouthTypes)

    if (mouthOptions.mouthTypes.isAvailable(MouthType.Beak)) {
        showRarityMap("Beak Shapes", mouthOptions.beakShapes)
        showRarityMap("Beak Colors", mouthOptions.beakColors)
    }

    if (mouthOptions.mouthTypes.isAvailable(MouthType.Snout)) {
        showRarityMap("Snout Shapes", mouthOptions.snoutShapes)
        showRarityMap("Snout Colors", mouthOptions.snoutColors)
    }
}

private fun HtmlBlockTag.showSkin(appearance: RaceAppearance) {
    h3 { +"Skin" }

    showRarityMap("Type", appearance.skinTypes)

    if (appearance.skinTypes.isAvailable(SkinType.Fur)) {
        showRarityMap("Fur Colors", appearance.furColors)
    }

    if (appearance.skinTypes.isAvailable(SkinType.Scales)) {
        showRarityMap("Scale Colors", appearance.scalesColors)
    }

    if (appearance.skinTypes.isAvailable(SkinType.Normal)) {
        showRarityMap("Normal Skin Colors", appearance.normalSkinColors)
    }

    if (appearance.skinTypes.isAvailable(SkinType.Exotic)) {
        showRarityMap("Exotic Skin Colors", appearance.exoticSkinColors)
    }
}

private fun HtmlBlockTag.showWings(appearance: RaceAppearance) {
    h3 { +"Wings" }

    showRarityMap("Layout", appearance.wingOptions.layouts)
    showRarityMap("Type", appearance.wingOptions.types)

    if (appearance.wingOptions.types.isAvailable(WingType.Bat)) {
        showRarityMap("Bat Wing Color", appearance.wingOptions.batColors)
    }

    if (appearance.wingOptions.types.isAvailable(WingType.Bird)) {
        showRarityMap("Bird Wing Color", appearance.wingOptions.birdColors)
    }

    if (appearance.wingOptions.types.isAvailable(WingType.Butterfly)) {
        showRarityMap("Butterfly Wing Color", appearance.wingOptions.butterflyColors)
    }
}

// edit

fun FORM.editRaceAppearance(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    selectRarityMap("Type", APPEARANCE, appearance.appearanceTypes, true)

    editEars(appearance)
    editEyes(appearance, eyeOptions)
    editFeet(appearance)
    editHair(appearance)
    editHorns(appearance)
    editMouth(appearance.mouthOptions)
    editSkin(appearance)
    editWings(appearance)
}

private fun FORM.editEars(appearance: RaceAppearance) {
    h3 { +"Ears" }

    selectRarityMap("Layout", combine(EARS, LAYOUT), appearance.earsLayout, true)

    if (appearance.earsLayout.isAvailable(EarsLayout.NormalEars)) {
        selectRarityMap("Ear Shapes", combine(EAR, SHAPE), appearance.earShapes, true)
    }
}

private fun FORM.editEyes(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    h3 { +"Eyes" }

    selectRarityMap("Layout", combine(EYE, LAYOUT), appearance.eyesLayout, true)

    if (!appearance.eyesLayout.isAvailable(EyesLayout.NoEyes)) {
        selectRarityMap("Eye Types", combine(EYE, TYPE), eyeOptions.eyeTypes, true)
        selectRarityMap("Eye Shapes", combine(EYE, SHAPE), eyeOptions.eyeShapes, true)
        selectRarityMap("Eye Colors", combine(PUPIL, COLOR), eyeOptions.eyeColors, true)

        if (eyeOptions.eyeTypes.isAvailable(EyeType.Normal)) {
            selectRarityMap("Pupil Shape", combine(PUPIL, SHAPE), eyeOptions.pupilShapes, true)
            selectRarityMap("Sclera Colors", combine(SCLERA, COLOR), eyeOptions.scleraColors, true)
        }
    }
}

private fun FORM.editFeet(appearance: RaceAppearance) {
    h3 { +"Feet" }

    selectRarityMap("Type", FOOT, appearance.footOptions.footTypes, true)

    if (appearance.footOptions.footTypes.isAvailable(FootType.Clawed)) {
        selectInt(
            "Number of Claws",
            appearance.footOptions.clawNumber,
            1,
            5,
            1,
            combine(FOOT, CLAWS, NUMBER),
            true,
        )
        selectRarityMap("Claw Size", combine(FOOT, CLAWS, SIZE), appearance.footOptions.clawSizes, true)
        selectRarityMap("Claw Color", combine(FOOT, CLAWS, COLOR), appearance.footOptions.clawColors, true)
    }
}

private fun FORM.editHair(appearance: RaceAppearance) {
    h3 { +"Hair" }

    selectRarityMap("Beard", BEARD, appearance.hairOptions.beardTypes, true)
    selectRarityMap("Hair", HAIR, appearance.hairOptions.hairTypes, true)

    if (requiresHairColor(appearance)) {
        selectRarityMap("Colors", combine(HAIR, COLOR), appearance.hairOptions.colors, true)
    }
}

private fun FORM.editHorns(appearance: RaceAppearance) {
    h3 { +"Horns" }

    val options = appearance.hornOptions
    val requiresNormalHorns = requiresNormalHorns(appearance)
    val requiresCrown = options.layouts.isAvailable(HornsLayout.Crown)

    selectRarityMap("Layouts", HORN, options.layouts, true)

    if (requiresNormalHorns) {
        showDetails("Simple Horns", true) {
            selectRarityMap("Simple Horn Types", combine(HORN, SHAPE), options.simpleTypes, true)
            selectHornLength(HORN, options.simpleLength)
        }
    }

    if (requiresCrown) {
        val values = VALID_CROWN_HORNS.toSet()
        showDetails("Crown", true) {
            selectCrownLength(options.crownLength)
            selectRarityMap("Horns in Crown (Front)", combine(CROWN, FRONT), options.crownFront, values, true)
            selectRarityMap("Horns in Crown (Back)", combine(CROWN, BACK), options.crownFront, values, true)
        }
    }

    if (requiresNormalHorns || requiresCrown) {
        selectRarityMap("Colors", combine(HORN, COLOR), options.colors, true)
    }
}


private fun FORM.editMouth(mouthOptions: MouthOptions) {
    h3 { +"Mouth" }

    selectRarityMap("Types", combine(MOUTH, TYPE), mouthOptions.mouthTypes, true)

    if (mouthOptions.mouthTypes.isAvailable(MouthType.Beak)) {
        selectRarityMap("Beak Shapes", combine(BEAK, SHAPE), mouthOptions.beakShapes, true)
        selectRarityMap("Beak Colors", combine(BEAK, COLOR), mouthOptions.beakColors, true)
    }

    if (mouthOptions.mouthTypes.isAvailable(MouthType.Snout)) {
        selectRarityMap("Snout Shapes", combine(SNOUT, SHAPE), mouthOptions.snoutShapes, true)
        selectRarityMap("Snout Colors", combine(SNOUT, COLOR), mouthOptions.snoutColors, true)
    }
}

private fun FORM.editSkin(appearance: RaceAppearance) {
    h3 { +"Skin" }

    selectRarityMap("Type", combine(SKIN, TYPE), appearance.skinTypes, true)

    if (appearance.skinTypes.isAvailable(SkinType.Fur)) {
        selectRarityMap("Fur Colors", combine(FUR, COLOR), appearance.furColors, true)
    }

    if (appearance.skinTypes.isAvailable(SkinType.Scales)) {
        selectRarityMap("Scale Colors", combine(SCALE, COLOR), appearance.scalesColors, true)
    }

    if (appearance.skinTypes.isAvailable(SkinType.Normal)) {
        selectRarityMap(
            "Normal Skin Colors",
            combine(NORMAL, SKIN, COLOR),
            appearance.normalSkinColors,
            true,
        )
    }

    if (appearance.skinTypes.isAvailable(SkinType.Exotic)) {
        selectRarityMap(
            "Exotic Skin Colors",
            combine(EXOTIC, SKIN, COLOR),
            appearance.exoticSkinColors,
            true,
        )
    }
}

private fun FORM.editWings(appearance: RaceAppearance) {
    h3 { +"Wings" }

    selectRarityMap("Layout", combine(WING, LAYOUT), appearance.wingOptions.layouts, true)
    selectRarityMap("Types", combine(WING, TYPE), appearance.wingOptions.types, true)
    selectRarityMap("Bat Wing Colors", combine(WING, BAT, COLOR), appearance.wingOptions.batColors, true)
    selectRarityMap("Bird Wing Colors", combine(WING, BIRD, COLOR), appearance.wingOptions.birdColors, true)
    selectRarityMap(
        "Butterfly Wing Colors",
        combine(WING, BUTTERFLY, COLOR),
        appearance.wingOptions.butterflyColors,
        true
    )
}

// parse

fun parseRaceAppearanceId(parameters: Parameters, param: String) = RaceAppearanceId(parseInt(parameters, param))

fun parseRaceAppearance(id: RaceAppearanceId, parameters: Parameters): RaceAppearance {
    val name = parameters.getOrFail("name")
    return RaceAppearance(
        id,
        name,
        parseOneOf(parameters, APPEARANCE, AppearanceType::valueOf),
        parseOneOf(parameters, combine(SKIN, TYPE), SkinType::valueOf),
        parseOneOf(parameters, combine(FUR, COLOR), Color::valueOf, Color.entries),
        parseOneOf(parameters, combine(SCALE, COLOR), Color::valueOf, Color.entries),
        parseOneOf(parameters, combine(NORMAL, SKIN, COLOR), SkinColor::valueOf, SkinColor.entries),
        parseOneOf(parameters, combine(EXOTIC, SKIN, COLOR), Color::valueOf, Color.entries),
        parseOneOf(parameters, combine(EARS, LAYOUT), EarsLayout::valueOf),
        parseOneOf(parameters, combine(EAR, SHAPE), EarShape::valueOf, EarShape.entries),
        parseOneOf(parameters, combine(EYE, LAYOUT), EyesLayout::valueOf),
        parseEyeOptions(parameters),
        parseFootOptions(parameters),
        parseHairOptions(parameters),
        parseHornOptions(parameters),
        parseMouthOptions(parameters),
        parseWingOptions(parameters),
    )
}

private fun parseEyeOptions(parameters: Parameters): EyeOptions {
    val eyeTypes = parseOneOf(parameters, combine(EYE, TYPE), EyeType::valueOf, EyeType.entries)
    val eyeShapes = parseOneOf(parameters, combine(EYE, SHAPE), EyeShape::valueOf, EyeShape.entries)
    val pupilShapes = parseOneOf(parameters, combine(PUPIL, SHAPE), PupilShape::valueOf, PupilShape.entries)
    val eyeColors = parseOneOf(parameters, combine(PUPIL, COLOR), Color::valueOf, Color.entries)
    val scleraColors = parseOneOf(parameters, combine(SCLERA, COLOR), Color::valueOf, Color.entries)

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
    parseOneOf(parameters, HAIR, HairType::valueOf),
    parseOneOf(parameters, combine(HAIR, COLOR), Color::valueOf, Color.entries),
)

private fun parseHornOptions(parameters: Parameters) = HornOptions(
    parseOneOf(parameters, HORN, HornsLayout::valueOf),
    parseOneOf(parameters, combine(HORN, SHAPE), SimpleHornType::valueOf, setOf(SimpleHornType.Mouflon)),
    parseFactor(parameters, combine(HORN, LENGTH), DEFAULT_SIMPLE_LENGTH),
    parseOneOf(parameters, combine(HORN, COLOR), Color::valueOf, Color.entries),
    parseFactor(parameters, combine(CROWN, LENGTH), DEFAULT_CROWN_LENGTH),
    parseOneOf(parameters, combine(CROWN, FRONT), String::toInt, setOf(DEFAULT_CROWN_HORNS)),
    parseOneOf(parameters, combine(CROWN, BACK), String::toInt, setOf(DEFAULT_CROWN_HORNS)),
)

private fun parseMouthOptions(parameters: Parameters) = MouthOptions(
    parseOneOf(parameters, combine(BEAK, COLOR), Color::valueOf, setOf(Color.Yellow)),
    parseOneOf(parameters, combine(BEAK, SHAPE), BeakShape::valueOf, BeakShape.entries),
    parseOneOf(parameters, combine(MOUTH, TYPE), MouthType::valueOf),
    parseOneOf(parameters, combine(SNOUT, COLOR), Color::valueOf, setOf(Color.Yellow)),
    parseOneOf(parameters, combine(SNOUT, SHAPE), SnoutShape::valueOf, SnoutShape.entries),
)

private fun parseWingOptions(parameters: Parameters) = WingOptions(
    parseOneOf(parameters, combine(WING, LAYOUT), WingsLayout::valueOf),
    parseOneOf(parameters, combine(WING, TYPE), WingType::valueOf),
    parseOneOf(parameters, combine(WING, BAT, COLOR), Color::valueOf, setOf(DEFAULT_BAT_COLOR)),
    parseOneOf(parameters, combine(WING, BIRD, COLOR), Color::valueOf, setOf(DEFAULT_BIRD_COLOR)),
    parseOneOf(parameters, combine(WING, BUTTERFLY, COLOR), Color::valueOf, setOf(DEFAULT_BUTTERFLY_COLOR)),
)