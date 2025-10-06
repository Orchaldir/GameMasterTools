package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.formWithPreview
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.magic.editSpell
import at.orchaldir.gm.app.html.magic.parseSpell
import at.orchaldir.gm.app.html.magic.showSpell
import at.orchaldir.gm.app.html.optionalLink
import at.orchaldir.gm.app.html.showLanguageCountForSpells
import at.orchaldir.gm.app.html.showSpellOriginCount
import at.orchaldir.gm.app.html.simpleHtmlEditor
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.Column.Companion.tdColumn
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SPELL_TYPE
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
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
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.td
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
            val state = STORE.getState()

            handleShowAllElements(
                SpellRoutes(),
                state.sortSpells(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createIdColumn(call, state, "Language", Spell::language),
                    createOriginColumn(call, state, ::SpellId),
                    createSkipZeroColumnForId("Groups", state::countSpellGroups),
                    createSkipZeroColumnForId("Domains", state::countDomains),
                    createSkipZeroColumnForId("Jobs", state::countJobs),
                    createSkipZeroColumnForId("Texts", state::countTexts),
                ),
            ) {
                showLanguageCountForSpells(call, state, it)
                showSpellOriginCount(it)
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

