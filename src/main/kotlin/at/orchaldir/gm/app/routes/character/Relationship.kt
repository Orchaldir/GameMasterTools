package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.UpdateRelationships
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.InterpersonalRelationship
import at.orchaldir.gm.core.selector.character.getOthersWithoutRelationship
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

private val logger = KotlinLogging.logger {}

private const val RELATIONSHIP_PARAM = "r"

fun Application.configureCharacterRelationshipRouting() {
    routing {
        get<CharacterRoutes.Relationships.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s relationships" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRelationshipEditor(call, state, character)
            }
        }
        post<CharacterRoutes.Relationships.Preview> { edit ->
            logger.info { "Get preview for character ${edit.id.value}'s relationships" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)
            val formParameters = call.receiveParameters()
            val relationships = parseRelationships(formParameters)

            val otherParam = formParameters["other"]

            if (!otherParam.isNullOrEmpty()) {
                val other = CharacterId(otherParam.toInt())
                relationships.computeIfAbsent(other) { setOf() }
            }

            val updatedCharacter = character.copy(relationships = relationships)

            call.respondHtml(HttpStatusCode.OK) {
                showRelationshipEditor(call, state, updatedCharacter)
            }
        }
        post<CharacterRoutes.Relationships.Update> { update ->
            logger.info { "Update character ${update.id.value}'s relationships" }

            val formParameters = call.receiveParameters()
            val relationships = parseRelationships(formParameters)

            STORE.dispatch(UpdateRelationships(update.id, relationships))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun parseRelationships(parameters: Parameters): MutableMap<CharacterId, Set<InterpersonalRelationship>> {
    val relationships = mutableMapOf<CharacterId, Set<InterpersonalRelationship>>()

    parameters.getAll(RELATIONSHIP_PARAM)?.forEach {
        val parts = it.split('_')
        val other = CharacterId(parts[0].toInt())
        val relationship = InterpersonalRelationship.valueOf(parts[1])
        val set = relationships[other] ?: setOf()
        relationships[other] = set + relationship
    }
    return relationships
}

private fun HTML.showRelationshipEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val backLink = href(call, character.id)
    val previewLink = call.application.href(CharacterRoutes.Relationships.Preview(character.id))
    val updateLink = call.application.href(CharacterRoutes.Relationships.Update(character.id))

    simpleHtml("Edit Relationships: ${character.name(state)}") {
        formWithPreview(previewLink, updateLink, backLink) {
            editRelationships(call, state, character)
        }
    }
}

private fun HtmlBlockTag.editRelationships(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    field("New Target of Relationship") {
        select {
            id = "other"
            name = "other"
            onChange = ON_CHANGE_SCRIPT
            option {
                label = ""
                value = ""
                selected = true
            }
            state.getOthersWithoutRelationship(character).forEach { other ->
                option {
                    label = other.name(state)
                    value = other.id.value.toString()
                }
            }
        }
    }
    showMap("Relationships", character.relationships) { otherId, relationships ->
        link(call, state, otherId)
        showList(InterpersonalRelationship.entries) { relationship ->
            checkBoxInput {
                name = RELATIONSHIP_PARAM
                value = "${otherId.value}_$relationship"
                checked = relationships.contains(relationship)
                +relationship.toString()
            }
        }
    }
}