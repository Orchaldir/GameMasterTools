package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.magic.editSpell
import at.orchaldir.gm.app.html.magic.parseSpell
import at.orchaldir.gm.app.html.magic.showSpell
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showOrigin
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SPELL_TYPE
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.model.util.SortSpell
import at.orchaldir.gm.core.selector.economy.countJobs
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.magic.countSpellGroups
import at.orchaldir.gm.core.selector.religion.countDomains
import at.orchaldir.gm.core.selector.util.sortSpells
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$SPELL_TYPE")
class SpellRoutes : Routes<SpellId,SortSpell> {
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


    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortSpell) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: SpellId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: SpellId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
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
            handleShowElement(details.id, SpellRoutes(), HtmlBlockTag::showSpell)
        }
        get<SpellRoutes.New> {
            handleCreateElement(STORE.getState().getSpellStorage()) { id ->
                SpellRoutes.Edit(id)
            }
        }
        get<SpellRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, SpellRoutes.All())
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
            val spell = parseSpell(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showSpellEditor(call, state, spell)
            }
        }
        post<SpellRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseSpell)
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
        showSortTableLinks(call, SortSpell.entries, SpellRoutes())
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
                    td { showOptionalDate(call, state, spell.date) }
                    td { optionalLink(call, state, spell.language) }
                    td { showOrigin(call, state, spell.origin, ::SpellId) }
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

