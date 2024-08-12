package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.generator.NameGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
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
import kotlinx.html.*
import mu.KotlinLogging
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

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
            val character = parseCharacter(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEditor(call, state, character)
            }
        }
        post<Characters.Update> { update ->
            logger.info { "Update character ${update.id.value}" }

            val state = STORE.getState()
            val character = parseCharacter(state, call.receiveParameters(), update.id)

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
    val createLink = call.application.href(Characters.New())

    simpleHtml("Characters") {
        field("Count", count.toString())
        showList(characters) { character ->
            link(call, character.first, character.second)
        }
        if (state.canCreateCharacter()) {
            p { a(createLink) { +"Add" } }
        }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCharacterDetails(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val equipment = state.getEquipment(character)
    val backLink = call.application.href(Characters())
    val editAppearanceLink = call.application.href(Characters.Appearance.Edit(character.id))
    val frontSvg = visualizeCharacter(RENDER_CONFIG, character.appearance, equipment)
    val backSvg = visualizeCharacter(RENDER_CONFIG, character.appearance, equipment, false)

    simpleHtml("Character: ${state.getName(character)}") {
        svg(frontSvg, 20)
        svg(backSvg, 20)

        p { a(editAppearanceLink) { +"Edit Appearance" } }

        showData(character, call, state)
        showSocial(call, state, character)
        showItems(call, state, character)

        p { a(backLink) { +"Back" } }
    }
}

private fun BODY.showData(
    character: Character,
    call: ApplicationCall,
    state: State,
) {
    val deleteLink = call.application.href(Characters.Delete(character.id))
    val editLink = call.application.href(Characters.Edit(character.id))
    val generateNameLink = call.application.href(Characters.Name.Generate(character.id))

    h2 { +"Data" }

    field("Id", character.id.value.toString())
    field("Race") {
        link(call, state, character.race)
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
    field(state, "Birthdate", character.birthDate)

    p { a(generateNameLink) { +"Generate New Name" } }
    p { a(editLink) { +"Edit" } }
    if (state.canDelete(character.id)) {
        p { a(deleteLink) { +"Delete" } }
    }
}

private fun BODY.showSocial(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val editLanguagesLink = call.application.href(Characters.Languages.Edit(character.id))
    val editRelationshipsLink = call.application.href(Characters.Relationships.Edit(character.id))

    h2 { +"Social" }

    field("Culture") {
        link(call, state, character.culture)
    }

    showFamily(call, state, character)

    showList("Personality", character.personality) { t ->
        link(call, state, t)
    }

    showMap("Relationships", character.relationships) { other, relationships ->
        link(call, state, other)
        +": ${relationships.joinToString { it.toString() }}"
    }

    showLanguages(call, state, character)

    p { a(editLanguagesLink) { +"Edit Languages" } }
    p { a(editRelationshipsLink) { +"Edit Relationships" } }
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
    val inventedLanguages = state.getInventedLanguages(character.id)

    showMap("Known Languages", character.languages) { id, level ->
        link(call, state, id)
        +": $level"
    }

    showList("Invented Languages", inventedLanguages) { language ->
        link(call, language)
    }
}

fun BODY.showItems(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val editEquipmentLink = call.application.href(Characters.Equipment.Edit(character.id))

    h2 { +"Items" }

    p { a(editEquipmentLink) { +"Edit Equipment" } }

    showList("Equipped", character.equipmentMap.map.values) { item ->
        link(call, state, item)
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
            selectOneOf("Gender", GENDER, race.genders) { gender ->
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
            selectDate(state, "Birthdate", character.birthDate, combine(ORIGIN, DATE))
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
