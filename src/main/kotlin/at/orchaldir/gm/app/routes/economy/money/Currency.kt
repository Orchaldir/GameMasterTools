package at.orchaldir.gm.app.routes.economy.money

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.editCurrency
import at.orchaldir.gm.app.html.economy.money.parseCurrency
import at.orchaldir.gm.app.html.economy.money.showCurrency
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.routes.economy.MaterialRoutes
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.CreateCurrency
import at.orchaldir.gm.core.action.DeleteCurrency
import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateCurrency
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CURRENCY_TYPE
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.util.SortCurrency
import at.orchaldir.gm.core.selector.economy.money.canDeleteCurrency
import at.orchaldir.gm.core.selector.realm.countRealmsWithCurrencyAtAnyTime
import at.orchaldir.gm.core.selector.util.sortCurrencies
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

@Resource("/$CURRENCY_TYPE")
class CurrencyRoutes {
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
}

fun Application.configureCurrencyRouting() {
    routing {
        get<CurrencyRoutes.All> { all ->
            logger.info { "Get all currency" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCurrencies(call, STORE.getState(), all.sort)
            }
        }
        get<CurrencyRoutes.Details> { details ->
            logger.info { "Get details of currency ${details.id.value}" }

            val state = STORE.getState()
            val currency = state.getCurrencyStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCurrencyDetails(call, state, currency)
            }
        }
        get<CurrencyRoutes.New> {
            logger.info { "Add new currency" }

            STORE.dispatch(CreateCurrency)

            call.respondRedirect(
                call.application.href(
                    CurrencyRoutes.Edit(
                        STORE.getState().getCurrencyStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<CurrencyRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteCurrency(delete.id), CurrencyRoutes())
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
            val currency = parseCurrency(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCurrencyEditor(call, state, currency)
            }
        }
        post<CurrencyRoutes.Update> { update ->
            logger.info { "Update currency ${update.id.value}" }

            val currency = parseCurrency(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateCurrency(currency))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCurrencies(
    call: ApplicationCall,
    state: State,
    sort: SortCurrency,
) {
    val currencies = state.sortCurrencies(sort)
    val createLink = call.application.href(CurrencyRoutes.New())

    simpleHtml("Currencies") {
        field("Count", currencies.size)
        showSortTableLinks(call, SortCurrency.entries, CurrencyRoutes(), CurrencyRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Start" }
                th { +"End" }
                th { +"Realms" }
            }
            currencies.forEach { currency ->
                tr {
                    tdLink(call, state, currency)
                    td { showOptionalDate(call, state, currency.startDate) }
                    td { showOptionalDate(call, state, currency.endDate) }
                    tdSkipZero(state.countRealmsWithCurrencyAtAnyTime(currency.id))
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showCurrencyDetails(
    call: ApplicationCall,
    state: State,
    currency: Currency,
) {
    val backLink = call.application.href(CurrencyRoutes.All())
    val deleteLink = call.application.href(CurrencyRoutes.Delete(currency.id))
    val editLink = call.application.href(CurrencyRoutes.Edit(currency.id))

    simpleHtmlDetails(currency) {
        showCurrency(call, state, currency)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
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
