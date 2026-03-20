package at.orchaldir.gm.app.html.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.appearance.selectCrownLength
import at.orchaldir.gm.app.html.character.appearance.selectHornLength
import at.orchaldir.gm.app.html.race.appearance.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.AppearanceType
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.EarsLayout
import at.orchaldir.gm.core.model.character.appearance.FootType
import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.EyeType
import at.orchaldir.gm.core.model.character.appearance.eye.EyesLayout
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape
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
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.selector.race.getRaces
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.h3

private fun requiresNormalHorns(appearance: RaceAppearance) =
    appearance.horn.layouts.isAvailable(HornsLayout.Two) ||
            appearance.horn.layouts.isAvailable(HornsLayout.Different)

// show

fun HtmlBlockTag.showRaceAppearance(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    fieldElements(call, state, "Used by", state.getRaces(appearance.id))

    showRarityMap("Type", appearance.appearanceTypes)

    showEars(appearance)
    showEyes(appearance, appearance.eye)
    showFeet(appearance)
    showHair(appearance)
    showHorns(call, state, appearance)
    showMouth(appearance.mouth)
    showSkin(call, state, appearance)
    showTails(call, state, appearance)
    showWings(call, state, appearance)
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
        showColorRarityMap("Eye Colors", eyeOptions.eyeColors)

        if (eyeOptions.eyeTypes.isAvailable(EyeType.Normal)) {
            showRarityMap("Pupil Shape", eyeOptions.pupilShapes)
            showColorRarityMap("Sclera Colors", eyeOptions.scleraColors)
        }
    }
}

private fun HtmlBlockTag.showFeet(appearance: RaceAppearance) {
    h3 { +"Feet" }

    showRarityMap("Type", appearance.foot.footTypes)

    if (appearance.foot.footTypes.isAvailable(FootType.Clawed)) {
        field("Number of Claws", appearance.foot.clawNumber)
        showColorRarityMap("Claw Color", appearance.foot.clawColors)
        showRarityMap("Claw Size", appearance.foot.clawSizes)
    }
}

private fun HtmlBlockTag.showHorns(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    h3 { +"Horns" }

    showRarityMap("Layouts", appearance.horn.layouts)

    val requiresNormalHorns = requiresNormalHorns(appearance)
    val requiresCrown = appearance.horn.layouts.isAvailable(HornsLayout.Crown)

    if (requiresNormalHorns) {
        showDetails("Simple Horns") {
            showRarityMap("Horn Types", appearance.horn.simpleTypes)
            fieldFactor("Horn Length", appearance.horn.simpleLength)
        }
    }

    if (requiresCrown) {
        showDetails("Crown") {
            fieldFactor("Horn Length", appearance.horn.crownLength)
            showRarityMap("Horns in Front", appearance.horn.crownFront) {
                +it.toString()
            }
            showRarityMap("Horns in Back", appearance.horn.crownFront) {
                +it.toString()
            }
        }

    }

    if (requiresNormalHorns || requiresCrown) {
        showFeatureColor(call, state, appearance.horn.colors)
    }
}

private fun HtmlBlockTag.showMouth(mouthOptions: MouthOptions) {
    h3 { +"Mouth" }

    showRarityMap("Types", mouthOptions.mouthTypes)

    if (mouthOptions.mouthTypes.isAvailable(MouthType.Beak)) {
        showRarityMap("Beak Shapes", mouthOptions.beakShapes)
        showColorRarityMap("Beak Colors", mouthOptions.beakColors)
    }

    if (mouthOptions.mouthTypes.isAvailable(MouthType.Snout)) {
        showRarityMap("Snout Shapes", mouthOptions.snoutShapes)
        showColorRarityMap("Snout Colors", mouthOptions.snoutColors)
    }
}

private fun HtmlBlockTag.showTails(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    h3 { +"Tails" }

    val options = appearance.tail

    showRarityMap("Layout", options.layouts)

    if (options.layouts.isAvailable(TailsLayout.Simple)) {
        showRarityMap("Simple Shape", options.simpleShapes)
        options.simpleOptions.forEach { (shape, simpleOptions) ->
            showDetails("$shape Tail") {
                showFeatureColor(call, state, simpleOptions)
            }
        }
    }
}

private fun HtmlBlockTag.showWings(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    h3 { +"Wings" }

    val options = appearance.wing

    showRarityMap("Layout", options.layouts)

    if (!options.hasWings()) {
        return
    }

    showRarityMap("Type", options.types)

    if (options.types.isAvailable(WingType.Bat)) {
        showFeatureColor(call, state, options.batColors)
    }

    if (options.types.isAvailable(WingType.Bird)) {
        showColorRarityMap("Bird Wing Color", options.birdColors)
    }

    if (options.types.isAvailable(WingType.Butterfly)) {
        showColorRarityMap("Butterfly Wing Color", options.butterflyColors)
    }
}

// edit

fun HtmlBlockTag.editRaceAppearance(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    selectName(appearance.name)

    h2 { +"Options" }

    selectRarityMap("Type", APPEARANCE, appearance.appearanceTypes)

    editEars(appearance)
    editEyes(appearance, appearance.eye)
    editFeet(appearance)
    editHair(appearance)
    editHorns(state, appearance)
    editMouth(appearance.mouth)
    editSkin(state, appearance)
    editTails(state, appearance)
    editWings(state, appearance)
}

private fun HtmlBlockTag.editEars(appearance: RaceAppearance) {
    h3 { +"Ears" }

    selectRarityMap("Layout", combine(EARS, LAYOUT), appearance.earsLayout)

    if (appearance.earsLayout.isAvailable(EarsLayout.NormalEars)) {
        selectRarityMap("Ear Shapes", combine(EAR, SHAPE), appearance.earShapes)
    }
}

private fun HtmlBlockTag.editEyes(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    h3 { +"Eyes" }

    selectRarityMap("Layout", combine(EYE, LAYOUT), appearance.eyesLayout)

    if (!appearance.eyesLayout.isAvailable(EyesLayout.NoEyes)) {
        selectRarityMap("Eye Types", combine(EYE, TYPE), eyeOptions.eyeTypes)
        selectRarityMap("Eye Shapes", combine(EYE, SHAPE), eyeOptions.eyeShapes)
        selectColorRarityMap("Eye Colors", combine(PUPIL, COLOR), eyeOptions.eyeColors)

        if (eyeOptions.eyeTypes.isAvailable(EyeType.Normal)) {
            selectRarityMap("Pupil Shape", combine(PUPIL, SHAPE), eyeOptions.pupilShapes)
            selectColorRarityMap("Sclera Colors", combine(SCLERA, COLOR), eyeOptions.scleraColors)
        }
    }
}

private fun HtmlBlockTag.editFeet(appearance: RaceAppearance) {
    h3 { +"Feet" }

    selectRarityMap("Type", FOOT, appearance.foot.footTypes)

    if (appearance.foot.footTypes.isAvailable(FootType.Clawed)) {
        selectInt(
            "Number of Claws",
            appearance.foot.clawNumber,
            1,
            5,
            1,
            combine(FOOT, CLAWS, NUMBER),
        )
        selectRarityMap("Claw Size", combine(FOOT, CLAWS, SIZE), appearance.foot.clawSizes)
        selectColorRarityMap("Claw Color", combine(FOOT, CLAWS, COLOR), appearance.foot.clawColors)
    }
}

private fun HtmlBlockTag.editHorns(state: State, appearance: RaceAppearance) {
    h3 { +"Horns" }

    val options = appearance.horn
    val requiresNormalHorns = requiresNormalHorns(appearance)
    val requiresCrown = options.layouts.isAvailable(HornsLayout.Crown)

    selectRarityMap("Layouts", HORN, options.layouts)

    if (requiresNormalHorns) {
        showDetails("Simple Horns", true) {
            selectRarityMap("Simple Horn Types", combine(HORN, SHAPE), options.simpleTypes)
            selectHornLength(HORN, options.simpleLength)
        }
    }

    if (requiresCrown) {
        val values = VALID_CROWN_HORNS.toSet()
        showDetails("Crown", true) {
            selectCrownLength(options.crownLength)
            selectRarityMap("Horns in Crown (Front)", combine(CROWN, FRONT), options.crownFront, values)
            selectRarityMap("Horns in Crown (Back)", combine(CROWN, BACK), options.crownFront, values)
        }
    }

    if (requiresNormalHorns || requiresCrown) {
        showDetails("Horn Color", true) {
            editFeatureColor(state, options.colors, appearance.hair, combine(HORN, COLOR))
        }
    }
}


private fun HtmlBlockTag.editMouth(mouthOptions: MouthOptions) {
    h3 { +"Mouth" }

    selectRarityMap("Types", combine(MOUTH, TYPE), mouthOptions.mouthTypes)

    if (mouthOptions.mouthTypes.isAvailable(MouthType.Beak)) {
        selectRarityMap("Beak Shapes", combine(BEAK, SHAPE), mouthOptions.beakShapes)
        selectColorRarityMap("Beak Colors", combine(BEAK, COLOR), mouthOptions.beakColors)
    }

    if (mouthOptions.mouthTypes.isAvailable(MouthType.Snout)) {
        selectRarityMap("Snout Shapes", combine(SNOUT, SHAPE), mouthOptions.snoutShapes)
        selectColorRarityMap("Snout Colors", combine(SNOUT, COLOR), mouthOptions.snoutColors)
    }
}

private fun HtmlBlockTag.editTails(state: State, appearance: RaceAppearance) {
    h3 { +"Tails" }

    val options = appearance.tail

    selectRarityMap("Layout", combine(TAIL, LAYOUT), options.layouts)

    if (options.layouts.isAvailable(TailsLayout.Simple)) {
        selectRarityMap("Simple Shape", combine(TAIL, SHAPE), options.simpleShapes)

        options.simpleOptions.forEach { (shape, simpleOptions) ->
            showDetails("$shape Tail", true) {
                editFeatureColor(state, simpleOptions, appearance.hair, combine(TAIL, shape.name))
            }
        }
    }
}

private fun HtmlBlockTag.editWings(state: State, appearance: RaceAppearance) {
    h3 { +"Wings" }

    val options = appearance.wing

    selectRarityMap("Layout", combine(WING, LAYOUT), options.layouts)

    if (!options.hasWings()) {
        return
    }

    selectRarityMap("Types", combine(WING, TYPE), options.types)

    if (options.types.isAvailable(WingType.Bat)) {
        editFeatureColor(state, options.batColors, appearance.hair, combine(WING, BAT))
    }

    if (options.types.isAvailable(WingType.Bird)) {
        selectColorRarityMap("Bird Wing Colors", combine(WING, BIRD, COLOR), options.birdColors)
    }

    if (options.types.isAvailable(WingType.Butterfly)) {
        selectColorRarityMap(
            "Butterfly Wing Colors",
            combine(WING, BUTTERFLY, COLOR),
            options.butterflyColors,
        )
    }
}

// parse

fun parseRaceAppearanceId(parameters: Parameters, param: String) = RaceAppearanceId(parseInt(parameters, param))

fun parseRaceAppearance(
    state: State,
    parameters: Parameters,
    id: RaceAppearanceId,
) = RaceAppearance(
    id,
    parseName(parameters),
    parseOneOf(parameters, APPEARANCE, AppearanceType::valueOf),
    parseOneOf(parameters, combine(EARS, LAYOUT), EarsLayout::valueOf),
    parseOneOf(parameters, combine(EAR, SHAPE), EarShape::valueOf, EarShape.entries),
    parseOneOf(parameters, combine(EYE, LAYOUT), EyesLayout::valueOf),
    parseEyeOptions(parameters),
    parseFootOptions(parameters),
    parseHairOptions(parameters),
    parseHornOptions(state, parameters),
    parseMouthOptions(parameters),
    parseSkinOptions(state, parameters, SKIN),
    parseTailOptions(state, parameters),
    parseWingOptions(state, parameters),
)

private fun parseEyeOptions(parameters: Parameters): EyeOptions {
    val eyeTypes = parseOneOf(parameters, combine(EYE, TYPE), EyeType::valueOf, EyeType.entries)
    val eyeShapes = parseOneOf(parameters, combine(EYE, SHAPE), EyeShape::valueOf, EyeShape.entries)
    val pupilShapes = parseOneOf(parameters, combine(PUPIL, SHAPE), PupilShape::valueOf, PupilShape.entries)
    val eyeColors = parseColorOneOf(parameters, combine(PUPIL, COLOR))
    val scleraColors = parseColorOneOf(parameters, combine(SCLERA, COLOR))

    return EyeOptions(eyeTypes, eyeShapes, eyeColors, pupilShapes, scleraColors)
}

private fun parseFootOptions(parameters: Parameters): FootOptions {
    val footTypes = parseOneOf(parameters, FOOT, FootType::valueOf)
    val clawNumber = parseInt(parameters, combine(FOOT, CLAWS, NUMBER), DEFAULT_CLAW_NUMBER)
    val clawColors = parseColorOneOf(parameters, combine(FOOT, CLAWS, COLOR), setOf(DEFAULT_CLAW_COLOR))
    val clawSizes = parseOneOf(parameters, combine(FOOT, CLAWS, SIZE), Size::valueOf, setOf(DEFAULT_CLAW_SIZE))

    return FootOptions(
        footTypes,
        clawNumber,
        clawColors,
        clawSizes,
    )
}


private fun parseHornOptions(
    state: State,
    parameters: Parameters,
) = HornOptions(
    parseOneOf(parameters, HORN, HornsLayout::valueOf),
    parseOneOf(parameters, combine(HORN, SHAPE), SimpleHornType::valueOf, setOf(SimpleHornType.Mouflon)),
    parseFactor(parameters, combine(HORN, LENGTH), DEFAULT_SIMPLE_LENGTH),
    parseFeatureColor(state, parameters, combine(HORN, COLOR)),
    parseFactor(parameters, combine(CROWN, LENGTH), DEFAULT_CROWN_LENGTH),
    parseOneOf(parameters, combine(CROWN, FRONT), String::toInt, setOf(DEFAULT_CROWN_HORNS)),
    parseOneOf(parameters, combine(CROWN, BACK), String::toInt, setOf(DEFAULT_CROWN_HORNS)),
)

private fun parseMouthOptions(parameters: Parameters) = MouthOptions(
    parseColorOneOf(parameters, combine(BEAK, COLOR), setOf(DEFAULT_BEAK_COLOR)),
    parseOneOf(parameters, combine(BEAK, SHAPE), BeakShape::valueOf, BeakShape.entries),
    parseOneOf(parameters, combine(MOUTH, TYPE), MouthType::valueOf),
    parseColorOneOf(parameters, combine(SNOUT, COLOR), setOf(DEFAULT_SNOUT_COLOR)),
    parseOneOf(parameters, combine(SNOUT, SHAPE), SnoutShape::valueOf, SnoutShape.entries),
)

private fun parseTailOptions(
    state: State,
    parameters: Parameters,
): TailOptions {
    val simpleShapes =
        parseOneOf(parameters, combine(TAIL, SHAPE), SimpleTailShape::valueOf, setOf(SimpleTailShape.Cat))

    return TailOptions(
        parseOneOf(parameters, combine(TAIL, LAYOUT), TailsLayout::valueOf),
        simpleShapes,
        simpleShapes.getValidValues()
            .associateWith { shape -> parseFeatureColor(state, parameters, combine(TAIL, shape.name)) },
    )
}

private fun parseWingOptions(
    state: State,
    parameters: Parameters,
) = WingOptions(
    parseOneOf(parameters, combine(WING, LAYOUT), WingsLayout::valueOf),
    parseOneOrNone(parameters, combine(WING, TYPE), WingType::valueOf, emptySet()),
    parseFeatureColor(state, parameters, combine(WING, BAT)),
    parseColorOneOf(parameters, combine(WING, BIRD, COLOR), setOf(DEFAULT_BIRD_COLOR)),
    parseColorOneOf(parameters, combine(WING, BUTTERFLY, COLOR), setOf(DEFAULT_BUTTERFLY_COLOR)),
)
