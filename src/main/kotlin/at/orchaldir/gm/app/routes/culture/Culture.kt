package at.orchaldir.gm.app.routes.culture

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.editCulture
import at.orchaldir.gm.app.html.culture.parseCulture
import at.orchaldir.gm.app.html.culture.showCulture
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CULTURE_TYPE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.SortCulture
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.util.sortCultures
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
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$CULTURE_TYPE")
class CultureRoutes : Routes<CultureId, SortCulture> {
    @Resource("all")
    class All(
        val sort: SortCulture = SortCulture.Name,
        val parent: CultureRoutes = CultureRoutes(),
    )

    @Resource("details")
    class Details(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("new")
    class New(val parent: CultureRoutes = CultureRoutes())

    @Resource("clone")
    class Clone(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("delete")
    class Delete(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("edit")
    class Edit(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("preview")
    class Preview(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("update")
    class Update(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCulture) = call.application.href(All(sort))
    override fun clone(call: ApplicationCall, id: CultureId) = call.application.href(Clone(id))
    override fun delete(call: ApplicationCall, id: CultureId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CultureId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureCultureRouting() {
    routing {
        get<CultureRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CultureRoutes(),
                state.sortCultures(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Calendar") { tdLink(call, state, it.calendar) },
                    Column("Languages") { tdInlineIds(call, state, it.languages.getValuesFor(Rarity.Everyone)) },
                    Column(listOf("Naming", "Convention")) { tdEnum(it.namingConvention.getType()) },
                    countCollectionColumn("Holidays") { it.holidays },
                    countCollectionColumn("Characters") { state.getCharacters(it.id) },
                ),
            )
        }
        get<CultureRoutes.Details> { details ->
            handleShowElement(details.id, CultureRoutes(), HtmlBlockTag::showCulture)
        }
        get<CultureRoutes.New> {
            handleCreateElement(STORE.getState().getCultureStorage()) { id ->
                CultureRoutes.Edit(id)
            }
        }
        get<CultureRoutes.Clone> { clone ->
            handleCloneElement(clone.id) { cloneId ->
                CultureRoutes.Edit(cloneId)
            }
        }
        get<CultureRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CultureRoutes())
        }
        get<CultureRoutes.Edit> { edit ->
            logger.info { "Get editor for culture ${edit.id.value}" }

            val state = STORE.getState()
            val culture = state.getCultureStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, state, culture)
            }
        }
        post<CultureRoutes.Preview> { preview ->
            logger.info { "Get preview for culture ${preview.id.value}" }

            val state = STORE.getState()
            val formParameters = call.receiveParameters()
            val culture = parseCulture(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, state, culture)
            }
        }
        post<CultureRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCulture)
        }
    }
}

private fun HTML.showCultureEditor(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val backLink = href(call, culture.id)
    val previewLink = call.application.href(CultureRoutes.Preview(culture.id))
    val updateLink = call.application.href(CultureRoutes.Update(culture.id))

    simpleHtmlEditor(culture) {
        formWithPreview(previewLink, updateLink, backLink) {
            editCulture(state, culture)
        }
    }
}


