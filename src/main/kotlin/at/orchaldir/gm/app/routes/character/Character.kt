package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseCharacter
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
import at.orchaldir.gm.core.model.util.SortCharacter
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
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

fun Application.configureCharacterRouting() {
    routing {
        get<CharacterRoutes.All> { all ->
            logger.info { "Get all characters" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCharacters(call, STORE.getState(), all.sort)
            }
        }
        get<CharacterRoutes.Gallery> {
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState())
            }
        }
        get<CharacterRoutes.Details> { details ->
            logger.info { "Get details of character ${details.id.value}" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterDetails(call, state, character)
            }
        }
        get<CharacterRoutes.New> {
            logger.info { "Add new character" }

            STORE.dispatch(CreateCharacter)

            call.respondRedirect(
                call.application.href(
                    CharacterRoutes.Edit(
                        STORE.getState().getCharacterStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<CharacterRoutes.Delete> { delete ->
            logger.info { "Delete character ${delete.id.value}" }

            STORE.dispatch(DeleteCharacter(delete.id))

            call.respondRedirect(call.application.href(CharacterRoutes.All()))

            STORE.getState().save()
        }
        get<CharacterRoutes.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEditor(call, state, character)
            }
        }
        post<CharacterRoutes.Preview> { preview ->
            logger.info { "Preview changes to character ${preview.id.value}" }

            val state = STORE.getState()
            val character = parseCharacter(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEditor(call, state, character)
            }
        }
        post<CharacterRoutes.Update> { update ->
            logger.info { "Update character ${update.id.value}" }

            val state = STORE.getState()
            val character = parseCharacter(state, call.receiveParameters(), update.id)

            STORE.dispatch(UpdateCharacter(character))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        get<CharacterRoutes.Birthday.Generate> { generate ->
            logger.info { "Generate the birthday of character ${generate.id.value}" }

            val state = STORE.getState()
            val generator = DateGenerator(RandomNumberGenerator(Random), state, state.time.defaultCalendar)
            val character = state.getCharacterStorage().getOrThrow(generate.id)
            val birthDate = generator.generateMonthAndDay(character.birthDate)
            val updated = character.copy(birthDate = birthDate)

            STORE.dispatch(UpdateCharacter(updated))

            call.respondRedirect(href(call, generate.id))

            STORE.getState().save()
        }
        get<CharacterRoutes.Name.Generate> { generate ->
            logger.info { "Generate the name of character ${generate.id.value}" }

            val state = STORE.getState()
            val generator = NameGenerator(RandomNumberGenerator(Random), state, generate.id)
            val name = generator.generate()
            val character = state.getCharacterStorage().getOrThrow(generate.id).copy(name = name)

            STORE.dispatch(UpdateCharacter(character))

            call.respondRedirect(href(call, generate.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCharacters(
    call: ApplicationCall,
    state: State,
    sort: SortCharacter,
) {
    val characters = STORE.getState().getCharacterStorage().getAll()
    val charactersWithNames = state.sortCharacters(characters, sort)
    val createLink = call.application.href(CharacterRoutes.New())
    val sortNameLink = call.application.href(CharacterRoutes.All())
    val sortAgeLink = call.application.href(CharacterRoutes.All(SortCharacter.Age))
    val galleryLink = call.application.href(CharacterRoutes.Gallery())

    simpleHtml("Characters") {
        action(galleryLink, "Gallery")
        field("Count", characters.size)
        field("Sort") {
            link(sortNameLink, "Name")
            +" "
            link(sortAgeLink, "Age")
        }
        table {
            tr {
                th { +"Name" }
                th { +"Race" }
                th { +"Culture" }
                th { +"Gender" }
                th { +"Birthdate" }
                th { +"Age" }
                th { +"Housing Status" }
                th { +"Employment Status" }
            }
            charactersWithNames.forEach { (character, name) ->
                tr {
                    td {
                        if (character.vitalStatus is Dead) {
                            del {
                                link(call, character.id, name)
                            }
                        } else {
                            link(call, character.id, name)
                        }
                    }
                    td { link(call, state, character.race) }
                    td { link(call, state, character.culture) }
                    td { +character.gender.toString() }
                    td { showDate(call, state, character.birthDate) }
                    td { +state.getAgeInYears(character).toString() }
                    td { showHousingStatus(call, state, character.housingStatus.current, false) }
                    td { showEmploymentStatus(call, state, character.employmentStatus.current, false) }
                }
            }
        }

        if (state.canCreateCharacter()) {
            action(createLink, "Add")
        }
        back("/")

        showCauseOfDeath(characters)
        showCultureCount(call, state, characters)
        showGenderCount(characters)
        showHousingStatusCount(characters)
        showJobCount(call, state, characters)
        showLanguageCountForCharacters(call, state, characters)
        showPersonalityCountForCharacters(call, state, characters)
        showRaceCount(call, state, characters)
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
) {
    val characters = state
        .getCharacterStorage()
        .getAll()
        .filter { it.appearance !is UndefinedAppearance }
    val maxHeight = characters.map { it.appearance.getSize() }.maxBy { it.millimeters }
    val maxSize = CHARACTER_CONFIG.calculateSize(maxHeight)
    val sortedCharacters = state.sortCharacters(characters, SortCharacter.Name)
    val backLink = call.application.href(CharacterRoutes.All())

    simpleHtml("Characters") {

        div("grid-container") {
            sortedCharacters.forEach { (character, name) ->
                val equipment = state.getEquipment(character)
                val svg = visualizeAppearance(CHARACTER_CONFIG, maxSize, character.appearance, equipment)

                div("grid-item") {
                    a(href(call, character.id)) {
                        div { +name }
                        svg(svg, 100)
                    }
                }
            }
        }

        back(backLink)
    }
}

private fun HTML.showCharacterDetails(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val equipment = state.getEquipment(character)
    val backLink = call.application.href(CharacterRoutes.All())
    val editAppearanceLink = call.application.href(CharacterRoutes.Appearance.Edit(character.id))
    val frontSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipment)
    val backSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipment, false)

    simpleHtml("Character: ${character.name(state)}") {
        svg(frontSvg, 20)
        svg(backSvg, 20)

        action(editAppearanceLink, "Edit Appearance")

        showData(character, call, state)
        showSocial(call, state, character)
        showPossession(call, state, character)
        showCreated(call, state, character.id)

        back(backLink)
    }
}

private fun BODY.showData(
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

private fun BODY.showVitalStatus(
    call: ApplicationCall,
    state: State,
    vitalStatus: VitalStatus,
) {
    if (vitalStatus is Dead) {
        field(call, state, "Date of Death", vitalStatus.deathDay)

        when (vitalStatus.cause) {
            is Accident -> showCauseOfDeath("Accident")
            is DeathByIllness -> showCauseOfDeath("Illness")
            is Murder -> {
                field("Cause of Death") {
                    +"Killed by "
                    link(call, state, vitalStatus.cause.killer)
                }
            }

            is OldAge -> showCauseOfDeath("Old Age")
        }
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

private fun BODY.showCauseOfDeath(cause: String) {
    field("Cause of Death", cause)
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

private fun BODY.showSocial(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val editLanguagesLink = call.application.href(CharacterRoutes.Languages.Edit(character.id))
    val editRelationshipsLink = call.application.href(CharacterRoutes.Relationships.Edit(character.id))

    h2 { +"Social" }

    fieldLink("Culture", call, state, character.culture)

    showFamily(call, state, character)

    showPersonality(call, state, character.personality)

    showMap("Relationships", character.relationships) { other, relationships ->
        link(call, state, other)
        +": ${relationships.joinToString { it.toString() }}"
    }

    showLanguages(call, state, character)

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

fun BODY.showPossession(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val editEquipmentLink = call.application.href(CharacterRoutes.Equipment.Edit(character.id))

    h2 { +"Possession" }

    showOwnedElements(call, state, character.id)

    showList("Equipped", character.equipmentMap.map.values) { item ->
        link(call, state, item)
    }

    action(editEquipmentLink, "Edit Equipment")
}

private fun HTML.showCharacterEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val races = state.getExistingRaces(character.birthDate)
    val characterName = character.name(state)
    val race = state.getRaceStorage().getOrThrow(character.race)
    val backLink = href(call, character.id)
    val previewLink = call.application.href(CharacterRoutes.Preview(character.id))
    val updateLink = call.application.href(CharacterRoutes.Update(character.id))

    simpleHtml("Edit Character: $characterName") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(state, character)
            selectElement(state, "Race", RACE, state.sortRaces(races), character.race, true)
            selectOneOf("Gender", GENDER, race.genders, character.gender) { gender ->
                label = gender.toString()
                value = gender.toString()
            }
            selectOrigin(state, character, race)
            selectVitalStatus(state, character)
            showAge(state, character, race)
            selectHousingStatusHistory(state, character.housingStatus, character.birthDate)
            selectEmploymentStatusHistory(state, character.employmentStatus, character.birthDate)
            h2 { +"Social" }
            selectElement(state, "Culture", CULTURE, state.getCultureStorage().getAll(), character.culture)
            editPersonality(call, state, character.personality)
            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun FORM.selectVitalStatus(
    state: State,
    character: Character,
) {
    val vitalStatus = character.vitalStatus
    selectValue("Vital Status", VITAL, VitalStatusType.entries, vitalStatus.getType(), true)

    if (vitalStatus is Dead) {
        selectDate(state, "Date of Death", vitalStatus.deathDay, combine(DEATH, DATE))
        selectValue("Cause of death", DEATH, CauseOfDeathType.entries, vitalStatus.cause.getType(), true)

        if (vitalStatus.cause is Murder) {
            selectElement(
                state,
                "Killer",
                KILLER,
                state.getCharacterStorage().getAllExcept(character.id),
                vitalStatus.cause.killer,
            )
        }
    }
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
