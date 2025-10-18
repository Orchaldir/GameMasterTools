package at.orchaldir.gm.app.routes.economy.money

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.editCurrency
import at.orchaldir.gm.app.html.economy.money.parseCurrency
import at.orchaldir.gm.app.html.economy.money.showCurrency
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CURRENCY_TYPE
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.util.SortCurrency
import at.orchaldir.gm.core.selector.realm.countRealmsWithCurrencyAtAnyTime
import at.orchaldir.gm.core.selector.util.sortCurrencies
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

@Resource("/$CURRENCY_TYPE")
class CurrencyRoutes : Routes<CurrencyId, SortCurrency> {
    @Resource("all")
    class All(
        val sort: SortCurrency = SortCurrency.Name,
        val parent: CurrencyRoutes = CurrencyRoutes(),
    )

    @Resource("details")
    class Details(val id: CurrencyId, val parent: CurrencyRoutes = CurrencyRoutes())

    @Resource("new")
    class New(val parent: CurrencyRoutes = CurrencyRoutes())

    @Resource("delete")
    class Delete(val id: CurrencyId, val parent: CurrencyRoutes = CurrencyRoutes())

    @Resource("edit")
    class Edit(val id: CurrencyId, val parent: CurrencyRoutes = CurrencyRoutes())

    @Resource("preview")
    class Preview(val id: CurrencyId, val parent: CurrencyRoutes = CurrencyRoutes())

    @Resource("update")
    class Update(val id: CurrencyId, val parent: CurrencyRoutes = CurrencyRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCurrency) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: CurrencyId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CurrencyId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureCurrencyRouting() {
    routing {
        get<CurrencyRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CurrencyRoutes(),
                state.sortCurrencies(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createEndDateColumn(call, state),
                    createSkipZeroColumnForId("Realms", state::countRealmsWithCurrencyAtAnyTime)
                ),
            )
        }
        get<CurrencyRoutes.Details> { details ->
            handleShowElement(details.id, CurrencyRoutes(), HtmlBlockTag::showCurrency)
        }
        get<CurrencyRoutes.New> {
            handleCreateElement(STORE.getState().getCurrencyStorage()) { id ->
                CurrencyRoutes.Edit(id)
            }
        }
        get<CurrencyRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CurrencyRoutes.All())
        }
        get<CurrencyRoutes.Edit> { edit ->
            logger.info { "Get editor for currency ${edit.id.value}" }

            val state = STORE.getState()
            val currency = state.getCurrencyStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCurrencyEditor(call, state, currency)
            }
        }
        post<CurrencyRoutes.Preview> { preview ->
            logger.info { "Preview currency ${preview.id.value}" }

            val state = STORE.getState()
            val currency = parseCurrency(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCurrencyEditor(call, state, currency)
            }
        }
        post<CurrencyRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCurrency)
        }
    }
}

private fun HTML.showCurrencyEditor(
    call: ApplicationCall,
    state: State,
    currency: Currency,
) {
    val backLink = href(call, currency.id)
    val previewLink = call.application.href(CurrencyRoutes.Preview(currency.id))
    val updateLink = call.application.href(CurrencyRoutes.Update(currency.id))

    simpleHtmlEditor(currency) {
        formWithPreview(previewLink, updateLink, backLink) {
            editCurrency(state, currency)
        }
    }
}
