package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.rpg.statblock.editStatblock
import at.orchaldir.gm.app.html.rpg.statblock.parseStatblock
import at.orchaldir.gm.app.html.rpg.statblock.showStatblock
import at.orchaldir.gm.app.html.util.fieldReference
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterInstance
import at.orchaldir.gm.core.model.character.CharacterInstanceId
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
    fieldReference(call, state, instance.basedOn, "Based On")
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
    instance: CharacterInstance,
): CharacterInstance {
    val race = state.getRaceStorage().getOrThrow(instance.race)

    return CharacterInstance(
        instance.id,
        parseName(parameters),
        instance.basedOn,
        parseRaceId(parameters, RACE),
        parse(parameters, GENDER, race.genders.getValidValues()),
        instance.appearance,
        parseStatblock(state, parameters),
        parseEquipmentMap(state, parameters),
    )
}
