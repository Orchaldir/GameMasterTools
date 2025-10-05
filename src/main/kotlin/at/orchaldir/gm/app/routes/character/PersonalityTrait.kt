package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.editPersonalityTrait
import at.orchaldir.gm.app.html.character.parsePersonalityTrait
import at.orchaldir.gm.app.html.character.showPersonalityTrait
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.PERSONALITY_TRAIT_TYPE
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.model.util.SortPersonalityTrait
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.character.getPersonalityTraitGroups
import at.orchaldir.gm.core.selector.character.getPersonalityTraits
import at.orchaldir.gm.core.selector.religion.getGodsWith
import at.orchaldir.gm.core.selector.util.sortPersonalityTraits
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$PERSONALITY_TRAIT_TYPE")
class PersonalityTraitRoutes : Routes<PersonalityTraitId, SortPersonalityTrait> {
    @Resource("all")
    class All(
        val sort: SortPersonalityTrait = SortPersonalityTrait.Name,
        val parent: PersonalityTraitRoutes = PersonalityTraitRoutes(),
    )

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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortPersonalityTrait) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: PersonalityTraitId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PersonalityTraitId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configurePersonalityRouting() {
    routing {
        get<PersonalityTraitRoutes.All> { all ->
            logger.info { "Get all personality traits" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllPersonalityTraits(call, STORE.getState(), all.sort)
            }
        }
        get<PersonalityTraitRoutes.Details> { details ->
            handleShowElement(details.id, PersonalityTraitRoutes(), HtmlBlockTag::showPersonalityTrait)
        }
        get<PersonalityTraitRoutes.New> {
            handleCreateElement(STORE.getState().getPersonalityTraitStorage()) { id ->
                PersonalityTraitRoutes.Edit(id)
            }
        }
        get<PersonalityTraitRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, PersonalityTraitRoutes())
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
            handleUpdateElement(update.id, ::parsePersonalityTrait)
        }
    }
}

private fun HTML.showAllPersonalityTraits(
    call: ApplicationCall,
    state: State,
    sort: SortPersonalityTrait,
) {
    val personalityTraits = state.sortPersonalityTraits(sort)
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
                    tdLink(call, state, trait)
                    tdSkipZero(state.getCharacters(trait.id))
                    tdSkipZero(state.getGodsWith(trait.id))
                }
            }
        }

        fieldList("By Group", state.getPersonalityTraitGroups()) { group ->
            state.getPersonalityTraits(group).forEach { trait ->
                +" "
                link(call, state, trait)
            }
        }

        fieldElements(call, state, "Without Group", personalityTraits.filter { it.group == null })

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showPersonalityTraitEditor(
    call: ApplicationCall,
    state: State,
    trait: PersonalityTrait,
) {
    val backLink = href(call, trait.id)
    val updateLink = call.application.href(PersonalityTraitRoutes.Update(trait.id))

    simpleHtmlEditor(trait) {
        form {
            editPersonalityTrait(call, state, trait)

            button("Update", updateLink)
        }
        back(backLink)
    }
}