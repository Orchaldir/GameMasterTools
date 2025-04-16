package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.character.*
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.generator.DateGenerator
import at.orchaldir.gm.core.generator.NameGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.util.SortCharacter
import at.orchaldir.gm.core.selector.canCreateCharacter
import at.orchaldir.gm.core.selector.getAgeInYears
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
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
                th { +"Gender" }
                th { +"Sexuality" }
                th { +"Culture" }
                th { +"Belief" }
                th { +"Birthdate" }
                th { +"Age" }
                th { +"Housing Status" }
                th { +"Employment Status" }
                th { +"Organizations" }
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
                    tdEnum(character.gender)
                    tdEnum(character.sexuality)
                    td { link(call, state, character.culture) }
                    td { showBeliefStatus(call, state, character.beliefStatus.current, false) }
                    td { showDate(call, state, character.birthDate) }
                    tdSkipZero(state.getAgeInYears(character))
                    td { showHousingStatus(call, state, character.housingStatus.current, false) }
                    td { showEmploymentStatus(call, state, character.employmentStatus.current, false) }
                    tdSkipZero(state.getOrganizations(character.id).size)
                }
            }
        }

        if (state.canCreateCharacter()) {
            action(createLink, "Add")
        }
        back("/")

        showCauseOfDeath(characters)
        showGenderCount(characters)
        showSexualOrientationCount(characters)
        showHousingStatusCount(characters)
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
    val sortedCharacters = state.sortCharacters(characters, SortCharacter.Name)
    val charactersWithSize =
        sortedCharacters.map { Triple(it.first, it.second, calculatePaddedSize(CHARACTER_CONFIG, it.first.appearance)) }
    val maxSize = charactersWithSize
        .maxBy { it.third.baseSize.height }
        .third
        .getFullSize()
    val backLink = call.application.href(CharacterRoutes.All())

    simpleHtml("Characters") {
        showGallery(call, charactersWithSize) { (character, _, paddedSize) ->
            val equipment = state.getEquipment(character)

            visualizeAppearance(state, CHARACTER_CONFIG, maxSize, character.appearance, paddedSize, equipment)
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


private fun HTML.showCharacterEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val characterName = character.name(state)
    val backLink = href(call, character.id)
    val previewLink = call.application.href(CharacterRoutes.Preview(character.id))
    val updateLink = call.application.href(CharacterRoutes.Update(character.id))

    simpleHtml("Edit Character: $characterName") {
        formWithPreview(previewLink, updateLink, backLink) {
            editCharacter(call, state, character)
        }
    }
}
