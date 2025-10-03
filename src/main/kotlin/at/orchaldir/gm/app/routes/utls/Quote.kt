package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.quote.editQuote
import at.orchaldir.gm.app.html.util.quote.parseQuote
import at.orchaldir.gm.app.html.util.quote.showQuote
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortQuote
import at.orchaldir.gm.core.model.util.quote.QUOTE_TYPE
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.selector.util.sortQuotes
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

@Resource("/$QUOTE_TYPE")
class QuoteRoutes: Routes<QuoteId> {
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
    override fun delete(call: ApplicationCall, id: QuoteId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: QuoteId) = call.application.href(Edit(id))
}

fun Application.configureQuoteRouting() {
    routing {
        get<QuoteRoutes.All> { all ->
            logger.info { "Get all quote" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllQuotes(call, STORE.getState(), all.sort)
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
            logger.info { "Get editor for quote ${edit.id.value}" }

            val state = STORE.getState()
            val quote = state.getQuoteStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showQuoteEditor(call, state, quote)
            }
        }
        post<QuoteRoutes.Preview> { preview ->
            logger.info { "Preview quote ${preview.id.value}" }

            val state = STORE.getState()
            val quote = parseQuote(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showQuoteEditor(call, state, quote)
            }
        }
        post<QuoteRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseQuote)
        }
    }
}

private fun HTML.showAllQuotes(
    call: ApplicationCall,
    state: State,
    sort: SortQuote,
) {
    val qquotes = state.sortQuotes(sort)
    val createLink = call.application.href(QuoteRoutes.New())

    simpleHtml("Quotes") {
        field("Count", qquotes.size)
        showSortTableLinks(call, SortQuote.entries, QuoteRoutes(), QuoteRoutes::All)
        table {
            tr {
                th { +"Text" }
                th { +"Type" }
                th { +"Start" }
                th { +"Source" }
            }
            qquotes.forEach { quote ->
                tr {
                    tdLink(call, state, quote)
                    tdEnum(quote.type)
                    td { showOptionalDate(call, state, quote.startDate()) }
                    td { showReference(call, state, quote.source, false) }
                }
            }
        }
        showCreatorCount(call, state, qquotes, "Sources")
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showQuoteEditor(
    call: ApplicationCall,
    state: State,
    quote: Quote,
) {
    val backLink = href(call, quote.id)
    val previewLink = call.application.href(QuoteRoutes.Preview(quote.id))
    val updateLink = call.application.href(QuoteRoutes.Update(quote.id))

    simpleHtmlEditor(quote) {
        formWithPreview(previewLink, updateLink, backLink) {
            editQuote(state, quote)
        }
    }
}
