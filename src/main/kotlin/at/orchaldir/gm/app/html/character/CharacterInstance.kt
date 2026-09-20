package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.rpg.statblock.editStatblock
import at.orchaldir.gm.app.html.rpg.statblock.parseStatblock
import at.orchaldir.gm.app.html.rpg.statblock.showStatblock
import at.orchaldir.gm.app.html.util.fieldReference
import at.orchaldir.gm.app.html.util.parseReference
import at.orchaldir.gm.app.html.util.selectReference
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.ALLOWED_BASED_ON_TYPES
import at.orchaldir.gm.core.model.character.CharacterInstance
import at.orchaldir.gm.core.model.character.CharacterInstanceId
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCharacterInstance(
    call: ApplicationCall,
    state: State,
    instance: CharacterInstance,
) {
    fieldReference(call, state, instance.basedOn, "Based On")
    fieldLink(call, state, instance.race)
    field("Gender", instance.gender)
    showStatblock(call, state, instance.statblock)
    showEquipmentMap(call, state, "Equipment", instance.equipped)
}

// edit

fun HtmlBlockTag.editCharacterInstance(
    call: ApplicationCall,
    state: State,
    instance: CharacterInstance,
) {
    val races = state.getRaceStorage().getAll()
    val race = state.getRaceStorage().getOrThrow(instance.race)

    selectName(instance.name)
    selectReference(
        state,
        "Based On",
        instance.basedOn,
        null,
        REFERENCE,
        ALLOWED_BASED_ON_TYPES,
    )
    selectElement(state, RACE, races, instance.race)
    selectFromOneOf("Gender", GENDER, race.genders, instance.gender)
    editStatblock(call, state, instance.statblock)
    editEquipmentMap(state, instance.equipped)
}

// parse

fun parseCharacterInstanceId(parameters: Parameters, param: String) = CharacterInstanceId(parseInt(parameters, param))

fun parseCharacterInstance(
    state: State,
    parameters: Parameters,
    id: CharacterInstanceId,
): CharacterInstance {
    val raceId = parseRaceId(parameters, RACE)
    val race = state.getRaceStorage().getOrThrow(raceId)

    return CharacterInstance(
        id,
        parseName(parameters),
        parseReference(parameters, REFERENCE, ALLOWED_BASED_ON_TYPES),
        raceId,
        parse(parameters, GENDER, race.genders.getValidValues()),
        UndefinedAppearance,
        parseStatblock(state, parameters),
        parseEquipmentMap(state, parameters),
    )
}
