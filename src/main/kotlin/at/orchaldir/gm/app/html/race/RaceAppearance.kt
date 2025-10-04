package at.orchaldir.gm.app.html.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.appearance.selectCrownLength
import at.orchaldir.gm.app.html.character.appearance.selectHornLength
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseOneOf
import at.orchaldir.gm.app.parse.parseOneOrNone
import at.orchaldir.gm.core.model.State
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
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h3

private fun requiresHairColor(appearance: RaceAppearance) =
    appearance.hair.beardTypes.isAvailable(BeardType.Normal) ||
            appearance.hair.hairTypes.isAvailable(HairType.Normal)

private fun requiresNormalHorns(appearance: RaceAppearance) =
    appearance.horn.layouts.isAvailable(HornsLayout.Two) ||
            appearance.horn.layouts.isAvailable(HornsLayout.Different)

// show

fun HtmlBlockTag.showRaceAppearance(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
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

    showElements(call, state, state.getRaces(appearance.id))
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

private fun HtmlBlockTag.showHair(appearance: RaceAppearance) {
    h3 { +"Hair" }

    showRarityMap("Beard", appearance.hair.beardTypes)
    showRarityMap("Hair", appearance.hair.hairTypes)

    if (requiresHairColor(appearance)) {
        showColorRarityMap("Colors", appearance.hair.colors)
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

private fun HtmlBlockTag.showSkin(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    h3 { +"Skin" }

    val options = appearance.skin

    showSkinInternal(call, state, options)
}

private fun HtmlBlockTag.showSkinInternal(
    call: ApplicationCall,
    state: State,
    options: SkinOptions,
) {
    showRarityMap("Type", options.skinTypes)

    if (options.skinTypes.isAvailable(SkinType.Exotic)) {
        showColorRarityMap("Exotic Skin Colors", options.exoticColors)
    }

    if (options.skinTypes.isAvailable(SkinType.Normal)) {
        showRarityMap("Normal Skin Colors", options.normalColors)
    }

    if (options.skinTypes.isAvailable(SkinType.Material)) {
        showRarityMap("Materials", options.materials) { id ->
            link(call, state, id)
        }
    }

    if (options.skinTypes.isAvailable(SkinType.Fur)) {
        showColorRarityMap("Fur Colors", options.furColors)
    }

    if (options.skinTypes.isAvailable(SkinType.Scales)) {
        showColorRarityMap("Scale Colors", options.scalesColors)
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

private fun HtmlBlockTag.showFeatureColor(
    call: ApplicationCall,
    state: State,
    options: FeatureColorOptions,
) {
    field("Color Type", options.types)

    if (options.types == FeatureColorType.Overwrite) {
        showDetails("Skin") {
            showSkinInternal(call, state, options.skin)
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

fun FORM.editRaceAppearance(
    state: State,
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    selectRarityMap("Type", APPEARANCE, appearance.appearanceTypes)

    editEars(appearance)
    editEyes(appearance, eyeOptions)
    editFeet(appearance)
    editHair(appearance)
    editHorns(state, appearance)
    editMouth(appearance.mouth)
    editSkin(state, appearance)
    editTails(state, appearance)
    editWings(state, appearance)
}

private fun FORM.editEars(appearance: RaceAppearance) {
    h3 { +"Ears" }

    selectRarityMap("Layout", combine(EARS, LAYOUT), appearance.earsLayout)

    if (appearance.earsLayout.isAvailable(EarsLayout.NormalEars)) {
        selectRarityMap("Ear Shapes", combine(EAR, SHAPE), appearance.earShapes)
    }
}

private fun FORM.editEyes(
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

private fun FORM.editFeet(appearance: RaceAppearance) {
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

private fun FORM.editHair(appearance: RaceAppearance) {
    h3 { +"Hair" }

    selectRarityMap("Beard", BEARD, appearance.hair.beardTypes)
    selectRarityMap("Hair", HAIR, appearance.hair.hairTypes)

    if (requiresHairColor(appearance)) {
        selectColorRarityMap("Colors", combine(HAIR, COLOR), appearance.hair.colors)
    }
}

private fun FORM.editHorns(state: State, appearance: RaceAppearance) {
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
        editFeatureColor(state, options.colors, appearance.hair, combine(HORN, COLOR))
    }
}


private fun FORM.editMouth(mouthOptions: MouthOptions) {
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

private fun FORM.editSkin(state: State, appearance: RaceAppearance) {
    h3 { +"Skin" }

    editSkinInternal(state, appearance.skin, SKIN)
}

private fun HtmlBlockTag.editSkinInternal(state: State, options: SkinOptions, param: String) {
    selectRarityMap("Type", combine(param, TYPE), options.skinTypes)

    if (options.skinTypes.isAvailable(SkinType.Exotic)) {
        selectColorRarityMap(
            "Exotic Skin Colors",
            combine(param, EXOTIC, COLOR),
            options.exoticColors,
        )
    }

    if (options.skinTypes.isAvailable(SkinType.Fur)) {
        selectColorRarityMap("Fur Colors", combine(param, FUR, COLOR), options.furColors)
    }

    if (options.skinTypes.isAvailable(SkinType.Material)) {
        selectRarityMap(
            "Materials",
            combine(param, MATERIAL),
            state.getMaterialStorage(),
            options.materials,
        ) { element -> element.name.text }
    }

    if (options.skinTypes.isAvailable(SkinType.Normal)) {
        selectRarityMap(
            "Normal Skin Colors",
            combine(param, NORMAL, COLOR),
            options.normalColors,
        )
    }

    if (options.skinTypes.isAvailable(SkinType.Scales)) {
        selectColorRarityMap("Scale Colors", combine(param, SCALE, COLOR), options.scalesColors)
    }
}

private fun FORM.editTails(state: State, appearance: RaceAppearance) {
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

private fun HtmlBlockTag.editFeatureColor(
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
        options.types,
    )

    if (options.types == FeatureColorType.Overwrite) {
        showDetails("Skin", true) {
            editSkinInternal(state, options.skin, param)
        }
    }
}

private fun FORM.editWings(state: State, appearance: RaceAppearance) {
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
    parseHornOptions(parameters),
    parseMouthOptions(parameters),
    parseSkinOptions(parameters, SKIN),
    parseTailOptions(parameters),
    parseWingOptions(parameters),
)

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
    parseFeatureColor(parameters, combine(HORN, COLOR)),
    parseFactor(parameters, combine(CROWN, LENGTH), DEFAULT_CROWN_LENGTH),
    parseOneOf(parameters, combine(CROWN, FRONT), String::toInt, setOf(DEFAULT_CROWN_HORNS)),
    parseOneOf(parameters, combine(CROWN, BACK), String::toInt, setOf(DEFAULT_CROWN_HORNS)),
)

private fun parseMouthOptions(parameters: Parameters) = MouthOptions(
    parseOneOf(parameters, combine(BEAK, COLOR), Color::valueOf, setOf(DEFAULT_BEAK_COLOR)),
    parseOneOf(parameters, combine(BEAK, SHAPE), BeakShape::valueOf, BeakShape.entries),
    parseOneOf(parameters, combine(MOUTH, TYPE), MouthType::valueOf),
    parseOneOf(parameters, combine(SNOUT, COLOR), Color::valueOf, setOf(DEFAULT_SNOUT_COLOR)),
    parseOneOf(parameters, combine(SNOUT, SHAPE), SnoutShape::valueOf, SnoutShape.entries),
)

private fun parseSkinOptions(parameters: Parameters, param: String) = SkinOptions(
    parseOneOf(parameters, combine(param, TYPE), SkinType::valueOf, setOf(SkinType.Normal)),
    parseOneOf(parameters, combine(param, EXOTIC, COLOR), Color::valueOf, setOf(DEFAULT_EXOTIC_COLOR)),
    parseOneOf(parameters, combine(param, FUR, COLOR), Color::valueOf, setOf(DEFAULT_FUR_COLOR)),
    parseOneOf(parameters, combine(param, MATERIAL), ::parseMaterialId, setOf(MaterialId(0))),
    parseOneOf(parameters, combine(param, NORMAL, COLOR), SkinColor::valueOf, SkinColor.entries),
    parseOneOf(parameters, combine(param, SCALE, COLOR), Color::valueOf, setOf(DEFAULT_SCALE_COLOR)),
)

private fun parseTailOptions(parameters: Parameters): TailOptions {
    val simpleShapes =
        parseOneOf(parameters, combine(TAIL, SHAPE), SimpleTailShape::valueOf, setOf(SimpleTailShape.Cat))

    return TailOptions(
        parseOneOf(parameters, combine(TAIL, LAYOUT), TailsLayout::valueOf),
        simpleShapes,
        simpleShapes.getValidValues()
            .associateWith { shape -> parseFeatureColor(parameters, combine(TAIL, shape.name)) },
    )
}

private fun parseFeatureColor(parameters: Parameters, param: String): FeatureColorOptions {
    val type = parse<FeatureColorType>(parameters, combine(param, COLOR))

    return if (type != null) {
        FeatureColorOptions(
            type,
            parseSkinOptions(parameters, param),
        )
    } else {
        FeatureColorOptions()
    }
}

private fun parseWingOptions(parameters: Parameters) = WingOptions(
    parseOneOf(parameters, combine(WING, LAYOUT), WingsLayout::valueOf),
    parseOneOrNone(parameters, combine(WING, TYPE), WingType::valueOf, listOf(WingType.Bird)),
    parseFeatureColor(parameters, combine(WING, BAT)),
    parseOneOf(parameters, combine(WING, BIRD, COLOR), Color::valueOf, setOf(DEFAULT_BIRD_COLOR)),
    parseOneOf(parameters, combine(WING, BUTTERFLY, COLOR), Color::valueOf, setOf(DEFAULT_BUTTERFLY_COLOR)),
)
