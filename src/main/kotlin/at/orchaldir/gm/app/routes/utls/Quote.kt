package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.util.quote.editQuote
import at.orchaldir.gm.app.html.util.quote.parseQuote
import at.orchaldir.gm.app.html.util.quote.showQuote
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortQuote
import at.orchaldir.gm.core.model.util.quote.QUOTE_TYPE
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.selector.util.sortQuotes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$QUOTE_TYPE")
class QuoteRoutes : Routes<QuoteId, SortQuote> {
    @Resource("all")
    class All(
        val sort: SortQuote = SortQuote.Name,
        val parent: QuoteRoutes = QuoteRoutes(),
    )

    @Resource("details")
    class Details(val id: QuoteId, val parent: QuoteRoutes = QuoteRoutes())

    @Resource("new")
    class New(val parent: QuoteRoutes = QuoteRoutes())

    @Resource("delete")
    class Delete(val id: QuoteId, val parent: QuoteRoutes = QuoteRoutes())

    @Resource("edit")
    class Edit(val id: QuoteId, val parent: QuoteRoutes = QuoteRoutes())

    @Resource("preview")
    class Preview(val id: QuoteId, val parent: QuoteRoutes = QuoteRoutes())

    @Resource("update")
    class Update(val id: QuoteId, val parent: QuoteRoutes = QuoteRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortQuote) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: QuoteId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: QuoteId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: QuoteId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: QuoteId) = call.application.href(Update(id))
}

fun Application.configureQuoteRouting() {
    routing {
        get<QuoteRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                QuoteRoutes(),
                state.sortQuotes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Type") { tdEnum(it.type) },
                    createStartDateColumn(call, state),
                    tdColumn("Source") { showReference(call, state, it.source, false) }
                ),
            ) {
                showCreatorCount(call, state, it, "Sources")
            }
        }
        get<QuoteRoutes.Details> { details ->
            handleShowElement(details.id, QuoteRoutes(), HtmlBlockTag::showQuote)
        }
        get<QuoteRoutes.New> {
            handleCreateElement(STORE.getState().getQuoteStorage()) { id ->
                QuoteRoutes.Edit(id)
            }
        }
        get<QuoteRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, QuoteRoutes.All())
        }
        get<QuoteRoutes.Edit> { edit ->
            handleEditElement(edit.id, QuoteRoutes(), HtmlBlockTag::editQuote)
        }
        post<QuoteRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, QuoteRoutes(), ::parseQuote, HtmlBlockTag::editQuote)
        }
        post<QuoteRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseQuote)
        }
    }
}
