package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.title.parseOptionalTitleId
import at.orchaldir.gm.app.html.culture.parseCultureId
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.routes.character.CharacterRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.SimpleAging
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_CHARACTERS
import at.orchaldir.gm.core.selector.character.*
import at.orchaldir.gm.core.selector.culture.getKnownLanguages
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.race.getExistingRaces
import at.orchaldir.gm.core.selector.realm.getBattlesLedBy
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getCurrentYear
import at.orchaldir.gm.core.selector.util.canHaveFamilyName
import at.orchaldir.gm.core.selector.util.canHaveGenonym
import at.orchaldir.gm.core.selector.util.getGivenName
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.util.*
import kotlinx.html.*
import kotlin.random.Random

// show

fun HtmlBlockTag.showData(
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

    optionalFieldLink("Title", call, state, character.title)
    fieldLink("Race", call, race)
    field("Gender", character.gender)
    fieldOrigin(call, state, character.origin, ::CharacterId)
    when (character.appearance) {
        is HeadOnly -> showHeight(state, character, character.appearance.height)
        is HumanoidBody -> showHeight(state, character, character.appearance.height)
        UndefinedAppearance -> doNothing()
    }
    field(call, state, "Birthdate", character.birthDate)
    showVitalStatus(call, state, character.vitalStatus, "Death")
    showAge(state, character, race)
    showHousingStatusHistory(call, state, character.housingStatus)
    showEmploymentStatusHistory(call, state, character.employmentStatus)
    showDestroyed(call, state, character.id)
    fieldList(call, state, "Led Battles", state.getBattlesLedBy(character.id))
    showDataSources(call, state, character.sources)

    action(generateNameLink, "Generate New Name")
    action(generateBirthdayLink, "Generate Birthday")
    action(editLink, "Edit")
    if (state.canDeleteCharacter(character.id)) {
        action(deleteLink, "Delete")
    }
}

private fun HtmlBlockTag.showHeight(
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
    val age = character.getAgeInYears(state)
    fieldAge("Age", age)
    race.lifeStages.getLifeStage(age)?.let {
        val start = race.lifeStages.getLifeStageStartAge(age)
        field("Life Stage", "${it.name.text} ($start-${it.maxAge} years)")
    }
}

fun HtmlBlockTag.showSocial(
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

    field("Sexuality", character.sexuality)

    showMap("Relationships", character.relationships) { other, relationships ->
        link(call, state, other)
        +": ${relationships.joinToString { it.toString() }}"
    }

    showLanguages(call, state, character)
    showMemberships(call, state, character)

    action(editLanguagesLink, "Edit Languages")
    action(editRelationshipsLink, "Edit Relationships")
}

private fun HtmlBlockTag.showFamily(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val parents = state.getParents(character.id)
    val children = state.getChildren(character.id)
    val siblings = state.getSiblings(character.id)

    fieldList(call, state, "Parents", parents)
    fieldList(call, state, "Children", children)
    fieldList(call, state, "Siblings", siblings)
}

fun HtmlBlockTag.showLanguages(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    showMap("Known Languages", state.getKnownLanguages(character)) { id, level ->
        link(call, state, id)
        +": $level"
    }
}

fun HtmlBlockTag.showMemberships(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    fieldList("Organizations", state.getOrganizations(character.id)) { organization ->
        link(call, organization)
        showHistory(
            call,
            state,
            organization.members[character.id] ?: History(null),
            "Rank",
        ) { _, _, rankIndex ->
            if (rankIndex != null) {
                +organization.memberRanks[rankIndex].name.text
            } else {
                +"Unknown"
            }
        }
    }
}

fun HtmlBlockTag.showPossession(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val editEquipmentLink = call.application.href(CharacterRoutes.Equipment.Edit(character.id))

    showOwnedElements(call, state, character.id, true)

    showEquipmentMap(call, state, "Equipped", character.equipmentMap)

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

    selectCharacterName(state, character)
    selectOptionalElement(state, "Title", TITLE, state.getTitleStorage().getAll(), character.title)
    selectElement(state, "Race", RACE, state.sortRaces(races), character.race)
    selectFromOneOf("Gender", GENDER, race.genders, character.gender)
    selectOrigin(state, character, race)
    selectVitalStatus(
        state,
        character.id,
        character.birthDate,
        character.vitalStatus,
        VALID_CAUSES_FOR_CHARACTERS,
    )
    showAge(state, character, race)
    selectHousingStatusHistory(state, character.housingStatus, character.birthDate)
    selectEmploymentStatusHistory(state, character.employmentStatus, character.birthDate)

    h2 { +"Social" }

    selectElement(state, "Culture", CULTURE, state.getCultureStorage().getAll(), character.culture)
    editBeliefStatusHistory(state, character.beliefStatus, character.birthDate)
    editPersonality(call, state, character.personality)
    if (character.gender == Gender.Genderless) {
        selectValue(
            "Sexuality",
            SEXUALITY,
            SEXUAL_ORIENTATION_FOR_GENDERLESS,
            if (!SEXUAL_ORIENTATION_FOR_GENDERLESS.contains(character.sexuality)) {
                SexualOrientation.Asexual
            } else {
                character.sexuality
            },
        )
    } else {
        selectValue(
            "Sexuality",
            SEXUALITY,
            SexualOrientation.entries,
            character.sexuality,
        )
    }

    editDataSources(state, character.sources)
}

private fun FORM.selectOrigin(
    state: State,
    character: Character,
    race: Race,
) {
    editOrigin(state, character.id, character.origin, character.birthDate, ALLOWED_CHARACTER_ORIGINS, ::CharacterId)

    if (race.lifeStages is SimpleAging) {
        selectOptionalValue(
            "Random Age Within Life Stage",
            LIFE_STAGE,
            null,
            race.lifeStages.lifeStages.withIndex().toList(),
        ) { stage ->
            label = stage.value.name.text
            value = stage.index.toString()
        }
    }

    selectDate(state, "Birthdate", character.birthDate, combine(ORIGIN, DATE), race.startDate())
}

private fun FORM.selectCharacterName(
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
    selectName("Given Name", character.getGivenName(), GIVEN_NAME)

    if (character.name is FamilyName) {
        selectOptionalName("Middle Name", character.name.middle, MIDDLE_NAME)
        selectName("Family Name", character.name.family, FAMILY_NAME)
    }
}

// parse

fun parseCharacterId(parameters: Parameters, param: String) = CharacterId(parseInt(parameters, param))
fun parseCharacterId(value: String) = CharacterId(value.toInt())
fun parseOptionalCharacterId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { CharacterId(it) }

fun parseCharacter(
    state: State,
    parameters: Parameters,
    id: CharacterId,
): Character {
    val character = state.getCharacterStorage().getOrThrow(id)

    val name = parseCharacterName(parameters)
    val race = parseRaceId(parameters, RACE)
    val origin = parseOrigin(parameters)
    val birthDate = parseBirthday(parameters, state, race)

    return character.copy(
        name = name,
        race = race,
        gender = parseGender(parameters),
        sexuality = parse(parameters, SEXUALITY, SexualOrientation.Asexual),
        origin = origin,
        birthDate = birthDate,
        vitalStatus = parseVitalStatus(parameters, state),
        culture = parseCultureId(parameters, CULTURE),
        personality = parsePersonality(parameters),
        housingStatus = parseHousingStatusHistory(parameters, state, birthDate),
        employmentStatus = parseEmploymentStatusHistory(parameters, state, birthDate),
        beliefStatus = parseBeliefStatusHistory(parameters, state, birthDate),
        title = parseOptionalTitleId(parameters, TITLE),
        sources = parseDataSources(parameters),
    )
}

fun parseGender(parameters: Parameters) = Gender.valueOf(parameters.getOrFail(GENDER))

private fun parseBirthday(
    parameters: Parameters,
    state: State,
    raceId: RaceId,
): Date {
    val index = parameters[LIFE_STAGE]?.toIntOrNull()

    if (index != null) {
        val race = state.getRaceStorage().getOrThrow(raceId)
        val minAge = if (index > 0) {
            race.lifeStages.getAllLifeStages()[index - 1].maxAge
        } else {
            0
        }
        val maxAge = race.lifeStages.getAllLifeStages()[index].maxAge
        val age = Random.nextInt(minAge, maxAge)

        return Year(state.getCurrentYear().year - age)
    }

    return parseDate(parameters, state.getDefaultCalendar(), combine(ORIGIN, DATE))
}

private fun parseCharacterName(parameters: Parameters): CharacterName {
    val given = parseName(parameters, GIVEN_NAME)

    return when (parameters.getOrFail(NAME_TYPE)) {
        "FamilyName" -> FamilyName(
            given,
            parseOptionalName(parameters, MIDDLE_NAME),
            parseName(parameters, FAMILY_NAME, "Unknown"),
        )

        "Genonym" -> Genonym(given)
        else -> Mononym(given)
    }
}
