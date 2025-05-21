package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.magic.editSpell
import at.orchaldir.gm.app.html.magic.parseSpell
import at.orchaldir.gm.app.html.magic.showSpell
import at.orchaldir.gm.app.html.util.displayOrigin
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.core.action.CreateSpell
import at.orchaldir.gm.core.action.DeleteSpell
import at.orchaldir.gm.core.action.UpdateSpell
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SPELL_TYPE
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.util.SortSpell
import at.orchaldir.gm.core.selector.economy.countJobs
import at.orchaldir.gm.core.selector.economy.getJobsContaining
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.item.getTextsContaining
import at.orchaldir.gm.core.selector.magic.canDeleteSpell
import at.orchaldir.gm.core.selector.magic.countSpellGroups
import at.orchaldir.gm.core.selector.magic.getSpellGroups
import at.orchaldir.gm.core.selector.religion.countDomains
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith
import at.orchaldir.gm.core.selector.util.getChildrenOf
import at.orchaldir.gm.core.selector.util.sortSpells
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

@Resource("/$SPELL_TYPE")
class SpellRoutes {
    @Resource("all")
    class All(
        val sort: SortSpell = SortSpell.Name,
        val parent: SpellRoutes = SpellRoutes(),
    )

    @Resource("details")
    class Details(val id: SpellId, val parent: SpellRoutes = SpellRoutes())

    @Resource("new")
    class New(val parent: SpellRoutes = SpellRoutes())

    @Resource("delete")
    class Delete(val id: SpellId, val parent: SpellRoutes = SpellRoutes())

    @Resource("edit")
    class Edit(val id: SpellId, val parent: SpellRoutes = SpellRoutes())

    @Resource("preview")
    class Preview(val id: SpellId, val parent: SpellRoutes = SpellRoutes())

    @Resource("update")
    class Update(val id: SpellId, val parent: SpellRoutes = SpellRoutes())
}

fun Application.configureSpellRouting() {
    routing {
        get<SpellRoutes.All> { all ->
            logger.info { "Get all spells" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllSpells(call, STORE.getState(), all.sort)
            }
        }
        get<SpellRoutes.Details> { details ->
            logger.info { "Get details of spell ${details.id.value}" }

            val state = STORE.getState()
            val spell = state.getSpellStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showSpellDetails(call, state, spell)
            }
        }
        get<SpellRoutes.New> {
            logger.info { "Add new spell" }

            STORE.dispatch(CreateSpell)

            call.respondRedirect(call.application.href(SpellRoutes.Edit(STORE.getState().getSpellStorage().lastId)))

            STORE.getState().save()
        }
        get<SpellRoutes.Delete> { delete ->
            logger.info { "Delete spell ${delete.id.value}" }

            STORE.dispatch(DeleteSpell(delete.id))

            call.respondRedirect(call.application.href(SpellRoutes.All()))

            STORE.getState().save()
        }
        get<SpellRoutes.Edit> { edit ->
            logger.info { "Get editor for spell ${edit.id.value}" }

            val state = STORE.getState()
            val spell = state.getSpellStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showSpellEditor(call, state, spell)
            }
        }
        post<SpellRoutes.Preview> { preview ->
            logger.info { "Get preview for spell ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val spell = parseSpell(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showSpellEditor(call, state, spell)
            }
        }
        post<SpellRoutes.Update> { update ->
            logger.info { "Update spell ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val spell = parseSpell(formParameters, state, update.id)

            STORE.dispatch(UpdateSpell(spell))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllSpells(
    call: ApplicationCall,
    state: State,
    sort: SortSpell,
) {
    val spells = state.sortSpells(sort)
    val createLink = call.application.href(SpellRoutes.New())

    simpleHtml("Spells") {
        field("Count", spells.size)
        showSortTableLinks(call, SortSpell.entries, SpellRoutes(), SpellRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Language" }
                th { +"Origin" }
                th { +"Groups" }
                th { +"Domains" }
                th { +"Jobs" }
                th { +"Texts" }
            }
            spells.forEach { spell ->
                tr {
                    tdLink(call, state, spell)
                    td { showOptionalDate(call, state, spell.startDate()) }
                    td { optionalLink(call, state, spell.language) }
                    td { displayOrigin(call, state, spell.origin) }
                    tdSkipZero(state.countSpellGroups(spell.id))
                    tdSkipZero(state.countDomains(spell.id))
                    tdSkipZero(state.countJobs(spell.id))
                    tdSkipZero(state.countTexts(spell.id))
                }
            }
        }

        showLanguageCountForSpells(call, state, spells)
        showSpellOriginCount(spells)

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showSpellDetails(
    call: ApplicationCall,
    state: State,
    spell: Spell,
) {
    val backLink = call.application.href(SpellRoutes.All())
    val deleteLink = call.application.href(SpellRoutes.Delete(spell.id))
    val editLink = call.application.href(SpellRoutes.Edit(spell.id))

    simpleHtmlDetails(spell) {
        showSpell(call, state, spell)

        fieldList(call, state, "Domains containing it", state.getDomainsAssociatedWith(spell.id))
        fieldList(call, state, "Spell Groups containing it", state.getSpellGroups(spell.id))
        fieldList(call, state, "Jobs using it", state.getJobsContaining(spell.id))
        fieldList(call, state, "Spells based on it", state.getChildrenOf(spell.id))
        fieldList("Texts containing it", state.getTextsContaining(spell.id)) { text ->
            link(call, text.id, text.getNameWithDate(state))
        }

        action(editLink, "Edit")

        if (state.canDeleteSpell(spell.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showSpellEditor(
    call: ApplicationCall,
    state: State,
    spell: Spell,
) {
    val backLink = href(call, spell.id)
    val previewLink = call.application.href(SpellRoutes.Preview(spell.id))
    val updateLink = call.application.href(SpellRoutes.Update(spell.id))

    simpleHtmlEditor(spell) {
        formWithPreview(previewLink, updateLink, backLink) {
            editSpell(state, spell)
        }
    }
}

