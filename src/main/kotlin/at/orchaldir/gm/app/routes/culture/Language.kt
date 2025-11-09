package at.orchaldir.gm.app.routes.culture

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.editLanguage
import at.orchaldir.gm.app.html.culture.parseLanguage
import at.orchaldir.gm.app.html.culture.showLanguage
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.culture.language.LANGUAGE_TYPE
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.util.SortLanguage
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.core.selector.culture.countChildren
import at.orchaldir.gm.core.selector.culture.countCultures
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.item.periodical.countPeriodicals
import at.orchaldir.gm.core.selector.magic.countSpells
import at.orchaldir.gm.core.selector.util.sortLanguages
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$LANGUAGE_TYPE")
class LanguageRoutes : Routes<LanguageId, SortLanguage> {
    @Resource("all")
    class All(
        val sort: SortLanguage = SortLanguage.Name,
        val parent: LanguageRoutes = LanguageRoutes(),
    )

    @Resource("details")
    class Details(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("new")
    class New(val parent: LanguageRoutes = LanguageRoutes())

    @Resource("delete")
    class Delete(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("edit")
    class Edit(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("preview")
    class Preview(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("update")
    class Update(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortLanguage) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: LanguageId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: LanguageId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: LanguageId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: LanguageId) = call.application.href(Update(id))
}

fun Application.configureLanguageRouting() {
    routing {
        get<LanguageRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                LanguageRoutes(),
                state.sortLanguages(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    createOriginColumn(call, state, ::LanguageId),
                    countColumnForId("Characters", state::countCharacters),
                    countColumnForId("Cultures", state::countCultures),
                    countColumnForId("Languages", state::countChildren),
                    countColumnForId("Spells", state::countSpells),
                    countColumnForId("Periodicals", state::countPeriodicals),
                    countColumnForId("Texts", state::countTexts),
                ),
            )
        }
        get<LanguageRoutes.Details> { details ->
            handleShowElement(details.id, LanguageRoutes(), HtmlBlockTag::showLanguage)
        }
        get<LanguageRoutes.New> {
            handleCreateElement(LanguageRoutes(), STORE.getState().getLanguageStorage())
        }
        get<LanguageRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, LanguageRoutes())
        }
        get<LanguageRoutes.Edit> { edit ->
            handleEditElement(edit.id, LanguageRoutes(), HtmlBlockTag::editLanguage)
        }
        post<LanguageRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, LanguageRoutes(), ::parseLanguage, HtmlBlockTag::editLanguage)
        }
        post<LanguageRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseLanguage)
        }
    }
}
