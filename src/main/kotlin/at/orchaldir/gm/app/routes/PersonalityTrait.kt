package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.fieldName
import at.orchaldir.gm.app.html.model.parsePersonalityTrait
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.core.action.CreatePersonalityTrait
import at.orchaldir.gm.core.action.DeletePersonalityTrait
import at.orchaldir.gm.core.action.UpdatePersonalityTrait
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.PERSONALITY_TRAIT_TYPE
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.core.selector.getPersonalityTraitGroups
import at.orchaldir.gm.core.selector.getPersonalityTraits
import at.orchaldir.gm.core.selector.religion.getGodsWith
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortGods
import io.ktor.http.*
import io.ktor.resources.*
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

@Resource("/$PERSONALITY_TRAIT_TYPE")
class PersonalityTraitRoutes {
    @Resource("details")
    class Details(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("new")
    class New(val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("delete")
    class Delete(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("edit")
    class Edit(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("update")
    class Update(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())
}

fun Application.configurePersonalityRouting() {
    routing {
        get<PersonalityTraitRoutes> {
            logger.info { "Get all personality traits" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllPersonalityTraits(call, STORE.getState())
            }
        }
        get<PersonalityTraitRoutes.Details> { details ->
            logger.info { "Get details of personality trait ${details.id.value}" }

            val state = STORE.getState()
            val trait = state.getPersonalityTraitStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPersonalityTraitDetails(call, state, trait)
            }
        }
        get<PersonalityTraitRoutes.New> {
            logger.info { "Add new personalityTrait" }

            STORE.dispatch(CreatePersonalityTrait)

            val state = STORE.getState()
            call.respondRedirect(call.application.href(PersonalityTraitRoutes.Edit(state.getPersonalityTraitStorage().lastId)))

            STORE.getState().save()
        }
        get<PersonalityTraitRoutes.Delete> { delete ->
            logger.info { "Delete personality trait ${delete.id.value}" }

            STORE.dispatch(DeletePersonalityTrait(delete.id))

            call.respondRedirect(call.application.href(PersonalityTraitRoutes()))

            STORE.getState().save()
        }
        get<PersonalityTraitRoutes.Edit> { edit ->
            logger.info { "Get editor for personality trait ${edit.id.value}" }

            val state = STORE.getState()
            val trait = state.getPersonalityTraitStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPersonalityTraitEditor(call, state, trait)
            }
        }
        post<PersonalityTraitRoutes.Update> { update ->
            logger.info { "Update personality trait ${update.id.value}" }

            val trait = parsePersonalityTrait(update.id, call.receiveParameters())

            STORE.dispatch(UpdatePersonalityTrait(trait))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllPersonalityTraits(call: ApplicationCall, state: State) {
    val personalityTraits = state.getPersonalityTraitStorage().getAll().sortedBy { it.name.text }
    val createLink = call.application.href(PersonalityTraitRoutes.New())

    simpleHtml("Personality Traits") {
        field("Count", personalityTraits.size)

        table {
            tr {
                th { +"Name" }
                th { +"Characters" }
                th { +"Gods" }
            }
            personalityTraits.forEach { trait ->
                tr {
                    td { link(call, state, trait) }
                    tdSkipZero(state.getCharacters(trait.id).size)
                    tdSkipZero(state.getGodsWith(trait.id).size)
                }
            }
        }

        showList("By Group", state.getPersonalityTraitGroups()) { group ->
            state.getPersonalityTraits(group).forEach { trait ->
                +" "
                link(call, state, trait)
            }
        }

        showList("Without Group", personalityTraits.filter { it.group == null }) { personalityTrait ->
            link(call, personalityTrait)
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showPersonalityTraitDetails(
    call: ApplicationCall,
    state: State,
    trait: PersonalityTrait,
) {
    val characters = state.sortCharacters(state.getCharacters(trait.id))
    val gods = state.sortGods(state.getGodsWith(trait.id))
    val backLink = call.application.href(PersonalityTraitRoutes())
    val deleteLink = call.application.href(PersonalityTraitRoutes.Delete(trait.id))
    val editLink = call.application.href(PersonalityTraitRoutes.Edit(trait.id))

    simpleHtml("Personality Trait: ${trait.name}") {
        fieldName(trait.name)

        if (trait.group != null) {
            val traits = state.getPersonalityTraits(trait.group)
                .filter { it != trait }
                .sortedBy { it.name.text }

            showList("Conflicting", traits) { t ->
                link(call, t)
            }
        }

        showList("Characters", characters) { (character, name) ->
            link(call, character.id, name)
        }
        showList("Gods", gods) { god ->
            link(call, state, god)
        }

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showPersonalityTraitEditor(
    call: ApplicationCall,
    state: State,
    trait: PersonalityTrait,
) {
    val groups = state.getPersonalityTraitGroups()
    val newGroup = groups.maxOfOrNull { it.value + 1 } ?: 0
    val backLink = href(call, trait.id)
    val updateLink = call.application.href(PersonalityTraitRoutes.Update(trait.id))

    simpleHtml("Edit PersonalityTrait: ${trait.name}") {
        form {
            selectName(trait.name)
            field("Group") {
                select {
                    id = "group"
                    name = "group"
                    option {
                        label = "No group"
                        value = ""
                        selected = trait.group == null
                    }
                    groups.forEach { g ->
                        option {
                            label = state.getPersonalityTraits(g)
                                .sortedBy { it.name.text }
                                .joinToString(separator = " VS ") { it.name.text }
                            value = g.value.toString()
                            selected = g == trait.group
                        }
                    }
                    option {
                        label = "New group"
                        value = newGroup.toString()
                    }
                }
            }
            button("Update", updateLink)
        }
        back(backLink)
    }
}