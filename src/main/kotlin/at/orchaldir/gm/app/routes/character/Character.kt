package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.generator.DateGenerator
import at.orchaldir.gm.core.generator.NameGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.SexualOrientation
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.SortCharacter
import at.orchaldir.gm.core.selector.character.getAppearanceForAge
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
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
            handleShowElement(details.id, CharacterRoutes(), HtmlBlockTag::showCharacterDetails)
        }
        get<CharacterRoutes.New> {
            handleCreateElement(STORE.getState().getCharacterStorage()) { id ->
                CharacterRoutes.Edit(id)
            }
        }
        get<CharacterRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CharacterRoutes.All())
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
            handleUpdateElement(update.id, ::parseCharacter)
        }
        get<CharacterRoutes.Birthday.Generate> { generate ->
            handleUpdateElement(generate.id, ::generateBirthday, "Generate the birthday of")
        }
        get<CharacterRoutes.Name.Generate> { generate ->
            handleUpdateElement(generate.id, ::generateName, "Generate the name of")
        }
    }
}

fun generateBirthday(
    state: State,
    id: CharacterId,
): Character {
    val generator = DateGenerator(RandomNumberGenerator(Random), state, state.getDefaultCalendarId())
    val character = state.getCharacterStorage().getOrThrow(id)
    val birthDate = generator.generateMonthAndDay(character.birthDate)

    return character.copy(birthDate = birthDate)
}

fun generateName(
    state: State,
    id: CharacterId,
): Character {
    val generator = NameGenerator(RandomNumberGenerator(Random), state, id)
    val name = generator.generate()

    return state.getCharacterStorage().getOrThrow(id).copy(name = name)
}

private fun HTML.showAllCharacters(
    call: ApplicationCall,
    state: State,
    sort: SortCharacter,
) {
    val characters = state.sortCharacters(sort)
    val createLink = call.application.href(CharacterRoutes.New())
    val galleryLink = call.application.href(CharacterRoutes.Gallery())

    simpleHtml("Characters") {
        action(galleryLink, "Gallery")
        field("Count", characters.size)
        showSortTableLinks(call, SortCharacter.entries, CharacterRoutes())
        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Race" }
                th { +"Gender" }
                th { +"Sexuality" }
                th { +"Culture" }
                th { +"Belief" }
                th { +"Age" }
                th { +"Birthdate" }
                th { +"Deathdate" }
                th { +"Death" }
                th { +"Housing Status" }
                th { +"Employment Status" }
                th { +"Organizations" }
                th { +"Cost" }
            }
            characters.forEach { character ->
                val name = character.nameForSorting(state)
                tr {
                    td {
                        if (character.vitalStatus is Dead) {
                            del {
                                link(call, character, name)
                            }
                        } else {
                            link(call, character, name)
                        }
                    }
                    td { optionalLink(call, state, character.title) }
                    tdLink(call, state, character.race)
                    tdEnum(character.gender)
                    td {
                        if (character.sexuality != SexualOrientation.Heterosexual) {
                            +character.sexuality.toString()
                        }
                    }
                    tdLink(call, state, character.culture)
                    td { showBeliefStatus(call, state, character.beliefStatus.current, false) }
                    tdSkipZero(character.getAgeInYears(state))
                    td { showDate(call, state, character.birthDate) }
                    td { showOptionalDate(call, state, character.vitalStatus.getDeathDate()) }
                    td { displayVitalStatus(call, state, character.vitalStatus, false) }
                    td { showPosition(call, state, character.housingStatus.current, false) }
                    td { showEmploymentStatus(call, state, character.employmentStatus.current, false, false) }
                    tdSkipZero(state.getOrganizations(character.id))
                    tdSkipZero(character.statblock.calculateCost(state))
                }
            }
        }

        action(createLink, "Add")
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
        sortedCharacters.map { Triple(it, it.name(state), calculatePaddedSize(CHARACTER_CONFIG, it.appearance)) }
    val maxSize = charactersWithSize
        .maxBy { it.third.baseSize.height.value() }
        .third
        .getFullSize()
    val backLink = call.application.href(CharacterRoutes.All())

    simpleHtml("Characters") {
        showGallery(call, charactersWithSize) { (character, _, paddedSize) ->
            val equipment = state.getEquipment(character)
            val appearance = state.getAppearanceForAge(character)

            visualizeAppearance(state, CHARACTER_CONFIG, maxSize, appearance, paddedSize, equipment)
        }

        back(backLink)
    }
}

private fun HtmlBlockTag.showCharacterDetails(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val equipment = state.getEquipment(character)
    val editAppearanceLink = call.application.href(CharacterRoutes.Appearance.Edit(character.id))
    val frontSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipment)
    val backSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipment, false)

    svg(frontSvg, 20)
    svg(backSvg, 20)

    action(editAppearanceLink, "Edit Appearance")

    showData(character, call, state)
    showSocial(call, state, character)
    showPossession(call, state, character)
    showCreated(call, state, character.id)
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

    simpleHtml("Edit Character: $characterName", true) {
        mainFrame {
            formWithPreview(previewLink, updateLink, backLink) {
                editCharacter(call, state, character)
            }
        }
    }
}
