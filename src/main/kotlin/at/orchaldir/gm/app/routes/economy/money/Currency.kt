package at.orchaldir.gm.app.routes.economy.money

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countColumnForId
import at.orchaldir.gm.app.html.createEndDateColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.economy.money.editCurrency
import at.orchaldir.gm.app.html.economy.money.parseCurrency
import at.orchaldir.gm.app.html.economy.money.showCurrency
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.economy.money.CURRENCY_TYPE
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.util.SortCurrency
import at.orchaldir.gm.core.selector.realm.countRealmsWithCurrencyAtAnyTime
import at.orchaldir.gm.core.selector.util.sortCurrencies
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

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
    override fun preview(call: ApplicationCall, id: CurrencyId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: CurrencyId) = call.application.href(Update(id))
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
                    countColumnForId("Realms", state::countRealmsWithCurrencyAtAnyTime)
                ),
            )
        }
        get<CurrencyRoutes.Details> { details ->
            handleShowElement(details.id, CurrencyRoutes(), HtmlBlockTag::showCurrency)
        }
        get<CurrencyRoutes.New> {
            handleCreateElement(CurrencyRoutes(), STORE.getState().getCurrencyStorage())
        }
        get<CurrencyRoutes.Delete> { delete ->
            handleDeleteElement(CurrencyRoutes(), delete.id)
        }
        get<CurrencyRoutes.Edit> { edit ->
            handleEditElement(edit.id, CurrencyRoutes(), HtmlBlockTag::editCurrency)
        }
        post<CurrencyRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, CurrencyRoutes(), ::parseCurrency, HtmlBlockTag::editCurrency)
        }
        post<CurrencyRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCurrency)
        }
    }
}
