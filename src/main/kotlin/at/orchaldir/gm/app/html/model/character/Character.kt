package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.character.showEquipmentMap
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseCharacter
import at.orchaldir.gm.app.routes.character.CharacterRoutes
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.generator.DateGenerator
import at.orchaldir.gm.core.generator.NameGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.SimpleAging
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.SortCharacter
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.appearance.calculatePaddedSize
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

// show

fun BODY.showData(
    character: Character,
    call: ApplicationCall,
    state: State,
) {
    val race = state.getRaceStorage().getOrThrow(character.race)
    val deleteLink = call.application.href(CharacterRoutes.Delete(character.id))
    val editLink = call.application.href(CharacterRoutes.Edit(character.id))
    val generateNameLink = call.application.href(CharacterRoutes.Name.Generate(character.id))
    val generateBirthdayLink = call.application.href(CharacterRoutes.Birthday.Generate(character.id))

    h2 { +"Data" }

    field("Race") {
        link(call, race)
    }
    field("Gender", character.gender)
    when (character.origin) {
        is Born -> {
            field("Origin") {
                +"Born to "
                link(call, state, character.origin.father)
                +" & "
                link(call, state, character.origin.mother)
            }
        }

        UndefinedCharacterOrigin -> doNothing()
    }
    when (character.appearance) {
        is HeadOnly -> showHeight(state, character, character.appearance.height)
        is HumanoidBody -> showHeight(state, character, character.appearance.height)
        UndefinedAppearance -> doNothing()
    }
    field(call, state, "Birthdate", character.birthDate)
    showVitalStatus(call, state, character.vitalStatus)
    showAge(state, character, race)
    showHousingStatusHistory(call, state, character.housingStatus)
    showEmploymentStatusHistory(call, state, character.employmentStatus)

    action(generateNameLink, "Generate New Name")
    action(generateBirthdayLink, "Generate Birthday")
    action(editLink, "Edit")
    if (state.canDelete(character.id)) {
        action(deleteLink, "Delete")
    }
}

private fun BODY.showHeight(
    state: State,
    character: Character,
    maxHeight: Distance,
) {
    fieldDistance("Max Height", maxHeight)
    showCurrentHeight(state, character, maxHeight)
}

fun HtmlBlockTag.showCurrentHeight(
    state: State,
    character: Character,
    maxHeight: Distance,
) {
    val currentHeight = state.scaleHeightByAge(character, maxHeight)
    fieldDistance("Current Height", currentHeight)
}

private fun HtmlBlockTag.showAge(
    state: State,
    character: Character,
    race: Race,
) {
    val age = state.getAgeInYears(character)
    fieldAge("Age", age)
    race.lifeStages.getLifeStage(age)?.let {
        val start = race.lifeStages.getLifeStageStartAge(age)
        field("Life Stage", "${it.name} ($start-${it.maxAge} years)")
    }
}

fun BODY.showSocial(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val editLanguagesLink = call.application.href(CharacterRoutes.Languages.Edit(character.id))
    val editRelationshipsLink = call.application.href(CharacterRoutes.Relationships.Edit(character.id))

    h2 { +"Social" }

    fieldLink("Culture", call, state, character.culture)
    showBeliefStatusHistory(call, state, character.beliefStatus)

    showFamily(call, state, character)

    showPersonality(call, state, character.personality)

    showMap("Relationships", character.relationships) { other, relationships ->
        link(call, state, other)
        +": ${relationships.joinToString { it.toString() }}"
    }

    showLanguages(call, state, character)
    showMemberships(call, state, character)

    action(editLanguagesLink, "Edit Languages")
    action(editRelationshipsLink, "Edit Relationships")
}

private fun BODY.showFamily(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val parents = state.getParents(character.id)
    val children = state.getChildren(character.id)
    val siblings = state.getSiblings(character.id)

    showList("Parents", parents) { parent ->
        link(call, state, parent)
    }
    showList("Children", children) { child ->
        link(call, state, child)
    }
    showList("Siblings", siblings) { sibling ->
        link(call, state, sibling)
    }
}

fun BODY.showLanguages(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    showMap("Known Languages", state.getKnownLanguages(character)) { id, level ->
        link(call, state, id)
        +": $level"
    }
}

fun BODY.showMemberships(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    showList("Organizations", state.getOrganizations(character.id)) { organization ->
        link(call, organization)
        showHistory(
            call,
            state,
            organization.members[character.id] ?: History(null),
            "Rank",
        ) { _, _, rankIndex ->
            if (rankIndex != null) {
                +organization.memberRanks[rankIndex].name
            } else {
                +"Unknown"
            }
        }
    }
}

fun BODY.showPossession(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val editEquipmentLink = call.application.href(CharacterRoutes.Equipment.Edit(character.id))

    showOwnedElements(call, state, character.id, true)

    showEquipmentMap(call, state, character.equipmentMap)

    action(editEquipmentLink, "Edit Equipment")
}

// edit

fun FORM.editCharacter(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val races = state.getExistingRaces(character.birthDate)
    val race = state.getRaceStorage().getOrThrow(character.race)

    selectName(state, character)
    selectElement(state, "Race", RACE, state.sortRaces(races), character.race, true)
    selectFromOneOf("Gender", GENDER, race.genders, character.gender)
    selectOrigin(state, character, race)
    selectVitalStatus(state, character)
    showAge(state, character, race)
    selectHousingStatusHistory(state, character.housingStatus, character.birthDate)
    selectEmploymentStatusHistory(state, character.employmentStatus, character.birthDate)

    h2 { +"Social" }

    selectElement(state, "Culture", CULTURE, state.getCultureStorage().getAll(), character.culture)
    editBeliefStatusHistory(state, character.beliefStatus, character.birthDate)
    editPersonality(call, state, character.personality)
}

private fun FORM.selectOrigin(
    state: State,
    character: Character,
    race: Race,
) {
    selectValue("Origin", ORIGIN, CharacterOriginType.entries, true) { type ->
        label = type.name
        value = type.name
        disabled = when (type) {
            CharacterOriginType.Born -> !state.hasPossibleParents(character.id)
            CharacterOriginType.Undefined -> false
        }
        selected = when (type) {
            CharacterOriginType.Born -> character.origin is Born
            CharacterOriginType.Undefined -> character.origin is UndefinedCharacterOrigin
        }
    }
    when (character.origin) {
        is Born -> {
            selectElement(
                state,
                "Father",
                FATHER,
                state.getPossibleFathers(character.id),
                character.origin.father,
            )
            selectElement(
                state,
                "Mother",
                MOTHER,
                state.getPossibleMothers(character.id),
                character.origin.mother,
            )
        }

        else -> doNothing()
    }

    if (race.lifeStages is SimpleAging) {
        selectOptionalValue(
            "Random Age Within Life Stage",
            LIFE_STAGE,
            null,
            race.lifeStages.lifeStages.withIndex().toList(),
            true,
        ) { stage ->
            label = stage.value.name
            value = stage.index.toString()
        }
    }

    selectDate(state, "Birthdate", character.birthDate, combine(ORIGIN, DATE), race.startDate())
}

private fun FORM.selectName(
    state: State,
    character: Character,
) {
    field("Name Type") {
        select {
            id = NAME_TYPE
            name = NAME_TYPE
            onChange = ON_CHANGE_SCRIPT
            option {
                label = "Mononym"
                value = "Mononym"
                selected = character.name is Mononym
            }
            option {
                label = "FamilyName"
                value = "FamilyName"
                selected = character.name is FamilyName
                disabled = !state.canHaveFamilyName(character)
            }
            option {
                label = "Genonym"
                value = "Genonym"
                selected = character.name is Genonym
                disabled = !state.canHaveGenonym(character)
            }
        }
    }
    selectText("Given Name", character.getGivenName(), GIVEN_NAME, 1)
    if (character.name is FamilyName) {
        selectText("Middle Name", character.name.middle ?: "", MIDDLE_NAME, 1)
        selectText("Family Name", character.name.family, FAMILY_NAME, 1)
    }
}
