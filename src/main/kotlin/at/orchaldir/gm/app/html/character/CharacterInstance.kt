package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.rpg.statblock.editStatblock
import at.orchaldir.gm.app.html.rpg.statblock.parseStatblock
import at.orchaldir.gm.app.html.rpg.statblock.showStatblock
import at.orchaldir.gm.app.html.selectFromOneOf
import at.orchaldir.gm.app.html.util.fieldReference
import at.orchaldir.gm.app.html.util.parseReference
import at.orchaldir.gm.app.html.util.selectReference
import at.orchaldir.gm.app.routes.race.generateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.ALLOWED_BASED_ON_TYPES
import at.orchaldir.gm.core.model.character.CharacterInstance
import at.orchaldir.gm.core.model.character.CharacterInstanceId
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.UseRace
import at.orchaldir.gm.core.model.race.UseRaceRarityMap
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.CharacterTemplateReference
import at.orchaldir.gm.core.model.util.NoReference
import at.orchaldir.gm.core.selector.culture.getAppearanceFashion
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentIdMap
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
import at.orchaldir.gm.utils.doNothing
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
    showEquipped(call, state, instance.statblock, instance.equipped)
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

    when (instance.basedOn) {
        is CharacterReference -> doNothing()
        is CharacterTemplateReference -> {
            val template = state.getCharacterTemplateStorage().getOrThrow(instance.basedOn.template)
            val race = state.getRaceStorage().getOrThrow(instance.race)

            when (template.race) {
                is UseRace -> doNothing()
                is UseRaceRarityMap -> selectFromOneOf(
                    RACE,
                    state.getRaceStorage(),
                    template.race.map,
                    instance.race,
                )
            }

            if (template.gender == null) {
                selectGender(race, instance)
            }
        }
        NoReference -> {
            selectElement(state, RACE, races, instance.race)
            selectGender(race, instance)
            editStatblock(call, state, instance.statblock)
            editEquipped(call, state, instance.statblock, instance.equipped)
        }
        else -> error("Unsupported type for base of instance!")
    }

}

private fun HtmlBlockTag.selectGender(
    race: Race,
    instance: CharacterInstance,
) {
    selectFromOneOf("Gender", GENDER, race.genders, instance.gender)
}

// parse

fun parseCharacterInstanceId(parameters: Parameters, param: String) = CharacterInstanceId(parseInt(parameters, param))

fun parseCharacterInstance(
    state: State,
    parameters: Parameters,
    id: CharacterInstanceId,
): CharacterInstance {
    val basedOn = parseReference(parameters, REFERENCE, ALLOWED_BASED_ON_TYPES)

    return when (basedOn) {
        is CharacterReference -> {
            val character = state.getCharacterStorage().getOrThrow(basedOn.character)

            CharacterInstance(
                id,
                parseName(parameters),
                basedOn,
                character.race,
                character.gender,
                character.appearance,
                state.getStatblock(character),
                state.getEquipmentIdMap(character),
            )
        }
        is CharacterTemplateReference -> {
            val template = state.getCharacterTemplateStorage().getOrThrow(basedOn.template)
            val raceId = when (template.race) {
                is UseRace -> template.race.race
                is UseRaceRarityMap -> parseRaceId(parameters, RACE)
            }
            val race = state.getRaceStorage().getOrThrow(raceId)
            val gender = template.gender ?: parseGender(parameters, race)
            val appearance = generateAppearance(
                state,
                race,
                gender,
                state.getAppearanceFashion(gender, template.culture),
            )

            CharacterInstance(
                id,
                parseName(parameters),
                basedOn,
                raceId,
                gender,
                appearance,
                state.getStatblock(race, template.statblock),
                state.getEquipmentIdMap(template),
            )
        }
        NoReference -> {
            val raceId = parseRaceId(parameters, RACE)
            val race = state.getRaceStorage().getOrThrow(raceId)

            CharacterInstance(
                id,
                parseName(parameters),
                basedOn,
                raceId,
                parseGender(parameters, race),
                UndefinedAppearance,
                parseStatblock(state, parameters),
                parseEquipmentMap(state, parameters),
            )
        }
        else -> error("Unsupported type for base of instance!")
    }
}

private fun parseGender(
    parameters: Parameters,
    race: Race,
) = parse(parameters, GENDER, race.genders.getValidValues())
