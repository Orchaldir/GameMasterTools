package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.quote.editQuote
import at.orchaldir.gm.app.html.util.quote.parseQuote
import at.orchaldir.gm.app.html.util.quote.showQuote
import at.orchaldir.gm.app.html.util.showCreator
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.core.action.CreateQuote
import at.orchaldir.gm.core.action.DeleteQuote
import at.orchaldir.gm.core.action.UpdateQuote
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.quote.QUOTE_TYPE
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.SortQuote
import at.orchaldir.gm.core.selector.quote.canDeleteQuote
import at.orchaldir.gm.core.selector.util.sortQuotes
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

@Resource("/$QUOTE_TYPE")
class QuoteRoutes {
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
            logger.info { "Get details of quote ${details.id.value}" }

            val state = STORE.getState()
            val quote = state.getQuoteStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showQuoteDetails(call, state, quote)
            }
        }
        get<QuoteRoutes.New> {
            logger.info { "Add new quote" }

            STORE.dispatch(CreateQuote)

            call.respondRedirect(
                call.application.href(
                    QuoteRoutes.Edit(
                        STORE.getState().getQuoteStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<QuoteRoutes.Delete> { delete ->
            logger.info { "Delete quote ${delete.id.value}" }

            STORE.dispatch(DeleteQuote(delete.id))

            call.respondRedirect(call.application.href(QuoteRoutes.All()))

            STORE.getState().save()
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
            val quote = parseQuote(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showQuoteEditor(call, state, quote)
            }
        }
        post<QuoteRoutes.Update> { update ->
            logger.info { "Update quote ${update.id.value}" }

            val quote = parseQuote(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateQuote(quote))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
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
                    td { showCreator(call, state, quote.source, false) }
                }
            }
        }
        showCreatorCount(call, state, qquotes, "Sources")
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showQuoteDetails(
    call: ApplicationCall,
    state: State,
    quote: Quote,
) {
    val backLink = call.application.href(QuoteRoutes.All())
    val deleteLink = call.application.href(QuoteRoutes.Delete(quote.id))
    val editLink = call.application.href(QuoteRoutes.Edit(quote.id))

    simpleHtmlDetails(quote) {
        showQuote(call, state, quote)

        action(editLink, "Edit")
        if (state.canDeleteQuote(quote.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
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
