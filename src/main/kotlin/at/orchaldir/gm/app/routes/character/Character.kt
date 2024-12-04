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
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.core.selector.economy.getOwnedBusinesses
import at.orchaldir.gm.core.selector.economy.getPreviouslyOwnedBusinesses
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy
import at.orchaldir.gm.core.selector.world.getOwnedBuildings
import at.orchaldir.gm.core.selector.world.getPreviouslyOwnedBuildings
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.visualization.character.visualizeCharacter
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
    val count = characters.size
    val createLink = call.application.href(CharacterRoutes.New())
    val sortNameLink = call.application.href(CharacterRoutes.All())
    val sortAgeLink = call.application.href(CharacterRoutes.All(SortCharacter.Age))

    simpleHtml("Characters") {
        field("Count", count.toString())
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
                th { +"Living Status" }
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
                    td { showLivingStatus(call, state, character.livingStatus.current, false) }
                    td { showEmploymentStatus(call, state, character.employmentStatus.current, false) }
                }
            }
        }
        showCultureCount(call, state, characters)
        showGenderCount(characters)
        showJobCount(call, state, characters)
        showLivingStatusCount(characters)
        showRaceCount(call, state, characters)

        if (state.canCreateCharacter()) {
            action(createLink, "Add")
        }
        back("/")
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
    val frontSvg = visualizeCharacter(RENDER_CONFIG, state, character, equipment)
    val backSvg = visualizeCharacter(RENDER_CONFIG, state, character, equipment, false)

    simpleHtml("Character: ${character.name(state)}") {
        svg(frontSvg, 20)
        svg(backSvg, 20)

        action(editAppearanceLink, "Edit Appearance")

        showData(character, call, state)
        showSocial(call, state, character)
        showPossession(call, state, character)
        showCrafting(call, state, character)

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
    field("Gender", character.gender.toString())
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
    showLivingStatusHistory(call, state, character.livingStatus)
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

private fun BODY.showCrafting(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    h2 { +"Crafting" }

    showList("Buildings", state.getBuildingsBuildBy(character.id)) { building ->
        link(call, state, building)
    }

    showList("Invented Languages", state.getLanguagesInventedBy(character.id)) { language ->
        link(call, language)
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

    showList("Personality", character.personality) { t ->
        link(call, state, t)
    }

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
    showMap("Known Languages", character.languages) { id, level ->
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

    showList("Owned Buildings", state.getOwnedBuildings(character.id)) { building ->
        link(call, state, building)
    }

    showList("Previously owned Buildings", state.getPreviouslyOwnedBuildings(character.id)) { building ->
        link(call, state, building)
    }

    showList("Owned Businesses", state.getOwnedBusinesses(character.id)) { business ->
        link(call, state, business)
    }

    showList("Previously owned Businesses", state.getPreviouslyOwnedBusinesses(character.id)) { business ->
        link(call, state, business)
    }

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
            selectValue("Race", RACE, state.getRaceStorage().getAll(), true) { r ->
                label = r.name
                value = r.id.value.toString()
                selected = r.id == character.race
            }
            selectOneOf("Gender", GENDER, race.genders) { gender ->
                label = gender.toString()
                value = gender.toString()
                selected = character.gender == gender
            }
            selectOrigin(state, character)
            selectVitalStatus(state, character)
            showAge(state, character, race)
            selectLivingStatusHistory(state, character.livingStatus, character.birthDate)
            selectEmploymentStatusHistory(state, character.employmentStatus, character.birthDate)
            h2 { +"Social" }
            selectValue("Culture", CULTURE, state.getCultureStorage().getAll()) { culture ->
                label = culture.name
                value = culture.id.value.toString()
                selected = culture.id == character.culture
            }
            editPersonality(call, state, character)
            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun FORM.editPersonality(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    field("Personality") {
        details {
            state.getPersonalityTraitGroups().forEach { group ->
                val textId = "$PERSONALITY_PREFIX${group.value}"
                var isAnyCheck = false

                p {
                    state.getPersonalityTraits(group).forEach { trait ->
                        val isChecked = character.personality.contains(trait.id)
                        isAnyCheck = isAnyCheck || isChecked

                        radioInput {
                            id = textId
                            name = textId
                            value = trait.id.value.toString()
                            checked = isChecked
                        }
                        label {
                            htmlFor = textId
                            link(call, trait)
                        }
                    }

                    radioInput {
                        id = textId
                        name = textId
                        value = NONE
                        checked = !isAnyCheck
                    }
                    label {
                        htmlFor = textId
                        +NONE
                    }
                }
            }
        }
    }
}

private fun FORM.selectVitalStatus(
    state: State,
    character: Character,
) {
    val vitalStatus = character.vitalStatus
    selectValue("Vital Status", VITAL, VitalStatusType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == vitalStatus.getType()
    }
    if (vitalStatus is Dead) {
        selectDay(state, "Date of Death", vitalStatus.deathDay, combine(DEATH, DATE))
        selectValue("Cause of death", DEATH, CauseOfDeathType.entries, true) { type ->
            label = type.name
            value = type.name
            selected = type == vitalStatus.cause.getType()
        }
        if (vitalStatus.cause is Murder) {
            selectValue("Killer", KILLER, state.getOthers(character.id)) { c ->
                label = c.name(state)
                value = c.id.value.toString()
                selected = vitalStatus.cause.killer == c.id
            }
        }
    }
}

private fun FORM.selectOrigin(
    state: State,
    character: Character,
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
            selectValue("Father", FATHER, state.getPossibleFathers(character.id)) { c ->
                label = c.name(state)
                value = c.id.value.toString()
                selected = character.origin.father == c.id
            }
            selectValue("Mother", MOTHER, state.getPossibleMothers(character.id)) { c ->
                label = c.name(state)
                value = c.id.value.toString()
                selected = character.origin.mother == c.id
            }
        }

        else -> doNothing()
    }
    selectDay(state, "Birthdate", character.birthDate, combine(ORIGIN, DATE))
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
