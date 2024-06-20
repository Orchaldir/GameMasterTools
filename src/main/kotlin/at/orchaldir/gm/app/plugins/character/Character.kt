package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.generator.NameGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.character.visualizeCharacter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.html.*
import mu.KotlinLogging
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

private const val GIVEN_NAME = "given"
private const val MIDDLE_NAME = "middle"
private const val FAMILY_NAME = "family"
private const val NAME_TYPE = "name_type"
private const val RACE = "race"
private const val GENDER = "gender"
private const val CULTURE = "culture"
private const val ORIGIN = "origin"
private const val FATHER = "father"
private const val MOTHER = "mother"
private const val GROUP_PREFIX = "group_"
private const val NONE = "None"

fun Application.configureCharacterRouting() {
    routing {
        get<Characters> {
            logger.info { "Get all characters" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCharacters(call, STORE.getState())
            }
        }
        get<Characters.Details> { details ->
            logger.info { "Get details of character ${details.id.value}" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterDetails(call, state, character)
            }
        }
        get<Characters.New> {
            logger.info { "Add new character" }

            STORE.dispatch(CreateCharacter)

            call.respondRedirect(call.application.href(Characters.Edit(STORE.getState().characters.lastId)))

            STORE.getState().save()
        }
        get<Characters.Delete> { delete ->
            logger.info { "Delete character ${delete.id.value}" }

            STORE.dispatch(DeleteCharacter(delete.id))

            call.respondRedirect(call.application.href(Characters()))

            STORE.getState().save()
        }
        get<Characters.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEditor(call, state, character)
            }
        }
        post<Characters.Preview> { preview ->
            logger.info { "Preview changes to character ${preview.id.value}" }

            val state = STORE.getState()
            val character = parseCharacter(state, preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEditor(call, state, character)
            }
        }
        post<Characters.Update> { update ->
            logger.info { "Update character ${update.id.value}" }

            val state = STORE.getState()
            val character = parseCharacter(state, update.id, call.receiveParameters())

            STORE.dispatch(UpdateCharacter(character))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        get<Characters.Name.Generate> { generate ->
            logger.info { "Random generate the name of character ${generate.id.value}" }

            val state = STORE.getState()
            val generator = NameGenerator(RandomNumberGenerator(Random), state, generate.id)
            val name = generator.generate()
            val character = state.characters.getOrThrow(generate.id).copy(name = name)

            STORE.dispatch(UpdateCharacter(character))

            call.respondRedirect(href(call, generate.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCharacters(call: ApplicationCall, state: State) {
    val characters = STORE.getState().characters.getAll()
        .map { Pair(it.id, state.getName(it)) }
        .sortedBy { it.second }
    val count = characters.size
    val createLink = call.application.href(Characters.New(Characters()))

    simpleHtml("Characters") {
        field("Count", count.toString())
        showList(characters) { character ->
            link(call, character.first, character.second)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCharacterDetails(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val backLink = call.application.href(Characters())
    val deleteLink = call.application.href(Characters.Delete(character.id))
    val editLink = call.application.href(Characters.Edit(character.id))
    val generateNameLink = call.application.href(Characters.Name.Generate(character.id))
    val editAppearanceLink = call.application.href(Characters.Appearance.Edit(character.id))
    val editLanguagesLink = call.application.href(Characters.Languages.Edit(character.id))
    val editRelationshipsLink = call.application.href(Characters.Relationships.Edit(character.id))
    val frontSvg = visualizeCharacter(RENDER_CONFIG, character.appearance)

    simpleHtml("Character: ${state.getName(character)}") {
        svg(frontSvg, 20)
        field("Id", character.id.value.toString())
        field("Race") {
            link(call, state, character.race)
        }
        field("Gender", character.gender.toString())
        field("Culture") {
            link(call, state, character.culture)
        }

        showFamily(call, state, character)

        if (character.personality.isNotEmpty()) {
            field("Personality") {
                showList(character.personality) { t ->
                    link(call, state, t)
                }
            }
        }

        if (character.relationships.isNotEmpty()) {
            field("Relationships") {
                showMap(character.relationships) { other, relationships ->
                    link(call, state, other)
                    +": ${relationships.joinToString { it.toString() }}"
                }
            }
        }

        showLanguages(call, state, character)

        p { a(generateNameLink) { +"Generate New Name" } }
        p { a(editLink) { +"Edit" } }
        p { a(editAppearanceLink) { +"Edit Appearance" } }
        p { a(editLanguagesLink) { +"Edit Languages" } }
        p { a(editRelationshipsLink) { +"Edit Relationships" } }
        if (state.canDelete(character.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun BODY.showFamily(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val parents = state.getParents(character.id)
    val children = state.getChildren(character.id)
    val siblings = state.getSiblings(character.id)

    if (parents.isNotEmpty()) {
        field("Parents") {
            showList(parents) { parent ->
                link(call, state, parent)
            }
        }
    }
    if (children.isNotEmpty()) {
        field("Children") {
            showList(children) { child ->
                link(call, state, child)
            }
        }
    }
    if (siblings.isNotEmpty()) {
        field("Siblings") {
            showList(siblings) { sibling ->
                link(call, state, sibling)
            }
        }
    }
}

private fun BODY.showLanguages(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val inventedLanguages = state.getInventedLanguages(character.id)

    if (character.languages.isNotEmpty()) {
        field("Known Languages") {
            showMap(character.languages) { id, level ->
                link(call, state, id)
                +": $level"
            }
        }
    }

    if (inventedLanguages.isNotEmpty()) {
        field("Invented Languages") {
            showList(inventedLanguages) { language ->
                link(call, language)
            }
        }
    }
}

private fun HTML.showCharacterEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val characterName = state.getName(character)
    val race = state.races.getOrThrow(character.race)
    val backLink = href(call, character.id)
    val previewLink = call.application.href(Characters.Preview(character.id))
    val updateLink = call.application.href(Characters.Update(character.id))

    simpleHtml("Edit Character: $characterName") {
        field("Id", character.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
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
            field("Given Name") {
                textInput(name = GIVEN_NAME) {
                    value = character.getGivenName()
                }
            }
            if (character.name is FamilyName) {
                field("Middle Name") {
                    textInput(name = MIDDLE_NAME) {
                        value = character.name.middle ?: ""
                    }
                }
                field("Family Name") {
                    textInput(name = FAMILY_NAME) {
                        value = character.name.family
                    }
                }
            }
            selectEnum("Race", RACE, state.races.getAll(), true) { r ->
                label = r.name
                value = r.id.value.toString()
                selected = r.id == character.race
            }
            selectEnum("Gender", GENDER, race.genders) { gender ->
                label = gender.toString()
                value = gender.toString()
                selected = character.gender == gender
            }
            selectEnum("Culture", CULTURE, state.cultures.getAll()) { culture ->
                label = culture.name
                value = culture.id.value.toString()
                selected = culture.id == character.culture
            }
            field("Origin") {
                select {
                    id = ORIGIN
                    name = ORIGIN
                    onChange = ON_CHANGE_SCRIPT
                    option {
                        label = "Born"
                        value = "Born"
                        selected = character.origin is Born
                    }
                    option {
                        label = "Undefined"
                        value = "Undefined"
                        selected = character.origin is UndefinedCharacterOrigin
                    }
                }
            }
            when (character.origin) {
                is Born -> {
                    selectEnum("Father", FATHER, state.getPossibleFathers(character.id)) { c ->
                        label = state.getName(c)
                        value = c.id.value.toString()
                        selected = character.origin.father == c.id
                    }
                    selectEnum("Mother", MOTHER, state.getPossibleMothers(character.id)) { c ->
                        label = state.getName(c)
                        value = c.id.value.toString()
                        selected = character.origin.mother == c.id
                    }
                }

                else -> doNothing()
            }
            field("Personality") {
                details {
                    state.getPersonalityTraitGroups().forEach { group ->
                        val textId = "$GROUP_PREFIX${group.value}"
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
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun parseCharacter(state: State, id: CharacterId, parameters: Parameters): Character {
    val character = state.characters.getOrThrow(id)

    val name = parseCharacterName(parameters)
    val race = RaceId(parameters.getOrFail(RACE).toInt())
    val gender = Gender.valueOf(parameters.getOrFail(GENDER))
    val culture = CultureId(parameters.getOrFail(CULTURE).toInt())
    val personality = parameters.entries()
        .asSequence()
        .filter { e -> e.key.startsWith(GROUP_PREFIX) }
        .map { e -> e.value.first() }
        .filter { it != NONE }
        .map { PersonalityTraitId(it.toInt()) }
        .toSet()

    val origin = when (parameters[ORIGIN]) {
        "Born" -> {
            val father = CharacterId(parameters[FATHER]?.toInt() ?: 0)
            val mother = CharacterId(parameters[MOTHER]?.toInt() ?: 0)
            Born(mother, father)
        }

        else -> UndefinedCharacterOrigin
    }

    return character.copy(
        name = name,
        race = race,
        gender = gender,
        origin = origin,
        culture = culture,
        personality = personality
    )
}

private fun parseCharacterName(parameters: Parameters): CharacterName {
    val given = parameters.getOrFail(GIVEN_NAME)

    return when (parameters.getOrFail(NAME_TYPE)) {
        "FamilyName" -> {
            var middle = parameters[MIDDLE_NAME]

            if (middle.isNullOrEmpty()) {
                middle = null
            }

            FamilyName(
                given,
                middle,
                parameters[FAMILY_NAME] ?: "Unknown"
            )
        }

        "Genonym" -> Genonym(given)
        else -> Mononym(given)
    }
}