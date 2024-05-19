package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.CreatePersonalityTrait
import at.orchaldir.gm.core.action.DeletePersonalityTrait
import at.orchaldir.gm.core.action.UpdatePersonalityTrait
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.selector.getPersonalityTraits
import io.ktor.http.*
import io.ktor.resources.*
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

private val logger = KotlinLogging.logger {}

@Resource("/personality")
class Personality {
    @Resource("details")
    class Details(val id: PersonalityTraitId, val parent: Personality = Personality())

    @Resource("new")
    class New(val parent: Personality = Personality())

    @Resource("delete")
    class Delete(val id: PersonalityTraitId, val parent: Personality = Personality())

    @Resource("edit")
    class Edit(val id: PersonalityTraitId, val parent: Personality = Personality())

    @Resource("update")
    class Update(val id: PersonalityTraitId, val parent: Personality = Personality())
}

fun Application.configurePersonalityRouting() {
    routing {
        get<Personality> {
            logger.info { "Get all personality traits" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllPersonalityTraits(call)
            }
        }
        get<Personality.Details> { details ->
            logger.info { "Get details of personality trait ${details.id.value}" }

            val state = STORE.getState()
            val trait = state.personalityTraits.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPersonalityTraitDetails(call, state, trait)
            }
        }
        get<Personality.New> {
            logger.info { "Add new personalityTrait" }

            STORE.dispatch(CreatePersonalityTrait)

            call.respondRedirect(call.application.href(Personality.Edit(STORE.getState().personalityTraits.lastId)))
        }
        get<Personality.Delete> { delete ->
            logger.info { "Delete personality trait ${delete.id.value}" }

            STORE.dispatch(DeletePersonalityTrait(delete.id))

            call.respondRedirect(call.application.href(Personality()))
        }
        get<Personality.Edit> { edit ->
            logger.info { "Get editor for personality trait ${edit.id.value}" }

            val state = STORE.getState()
            val trait = state.personalityTraits.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPersonalityTraitEditor(call, state, trait)
            }
        }
        post<Personality.Update> { update ->
            logger.info { "Update personality trait ${update.id.value}" }

            val trait = parsePersonalityTrait(update.id, call.receiveParameters())

            STORE.dispatch(UpdatePersonalityTrait(trait))

            call.respondRedirect(href(call, update.id))
        }
    }
}

private fun parsePersonalityTrait(id: PersonalityTraitId, parameters: Parameters): PersonalityTrait {
    val name = parameters.getOrFail("name")
    val group = parameters["group"]
        ?.toIntOrNull()
        ?.let { PersonalityTraitGroup(it) }

    return PersonalityTrait(id, name, group)
}

private fun HTML.showAllPersonalityTraits(call: ApplicationCall) {
    val personalityTraits = STORE.getState().personalityTraits
    val count = personalityTraits.getSize()
    val createLink = call.application.href(Personality.New(Personality()))

    simpleHtml("Personality Traits") {
        field("Count", count.toString())
        showList(personalityTraits.getAll()) { personalityTrait ->
            link(call, personalityTrait)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showPersonalityTraitDetails(
    call: ApplicationCall,
    state: State,
    trait: PersonalityTrait,
) {
    val backLink = call.application.href(Personality())
    val deleteLink = call.application.href(Personality.Delete(trait.id))
    val editLink = call.application.href(Personality.Edit(trait.id))

    simpleHtml("Personality Trait: ${trait.name}") {
        field("Id", trait.id.value.toString())
        field("Name", trait.name)
        if (trait.group != null) {
            field("Group") {
                showList(state.getPersonalityTraits(trait.group)) { t ->
                    link(call, t)
                }
            }
        }
        p { a(editLink) { +"Edit" } }
        p { a(deleteLink) { +"Delete" } }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showPersonalityTraitEditor(
    call: ApplicationCall,
    state: State,
    trait: PersonalityTrait,
) {
    val backLink = href(call, trait.id)
    val updateLink = call.application.href(Personality.Update(trait.id))

    simpleHtml("Edit PersonalityTrait: ${trait.name}") {
        field("Id", trait.id.value.toString())
        form {
            field("Name") {
                b { +"Name: " }
                textInput(name = "name") {
                    value = trait.name
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