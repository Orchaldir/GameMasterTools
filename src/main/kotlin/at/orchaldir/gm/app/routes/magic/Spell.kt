package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.magic.editSpell
import at.orchaldir.gm.app.html.magic.parseSpell
import at.orchaldir.gm.app.html.magic.showSpell
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.magic.SPELL_TYPE
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.util.SortSpell
import at.orchaldir.gm.core.selector.economy.countJobs
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.magic.countSpellGroups
import at.orchaldir.gm.core.selector.religion.countDomains
import at.orchaldir.gm.core.selector.util.sortSpells
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$SPELL_TYPE")
class SpellRoutes : Routes<SpellId, SortSpell> {
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
    override fun preview(call: ApplicationCall, id: SpellId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: SpellId) = call.application.href(Edit(id))
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
                    countColumnForId("Groups", state::countSpellGroups),
                    countColumnForId("Domains", state::countDomains),
                    countColumnForId("Jobs", state::countJobs),
                    countColumnForId("Texts", state::countTexts),
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
            handleEditElement(edit.id, SpellRoutes(), HtmlBlockTag::editSpell)
        }
        post<SpellRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, SpellRoutes(), ::parseSpell, HtmlBlockTag::editSpell)
        }
        post<SpellRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseSpell)
        }
    }
}

