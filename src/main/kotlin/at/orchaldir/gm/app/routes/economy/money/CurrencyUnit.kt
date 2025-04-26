package at.orchaldir.gm.app.routes.economy.money

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.economy.editCurrencyUnit
import at.orchaldir.gm.app.html.model.economy.parseCurrencyUnit
import at.orchaldir.gm.app.html.model.economy.showCurrencyUnit
import at.orchaldir.gm.core.action.CreateCurrencyUnit
import at.orchaldir.gm.core.action.DeleteCurrencyUnit
import at.orchaldir.gm.core.action.UpdateCurrencyUnit
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CURRENCY_UNIT_TYPE
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.util.SortCurrencyUnit
import at.orchaldir.gm.core.selector.economy.canDeleteCurrencyUnit
import at.orchaldir.gm.core.selector.util.sortCurrencyUnits
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

@Resource("/$CURRENCY_UNIT_TYPE")
class CurrencyUnitRoutes {
    @Resource("all")
    class All(
        val sort: SortCurrencyUnit = SortCurrencyUnit.Name,
        val parent: CurrencyUnitRoutes = CurrencyUnitRoutes(),
    )

    @Resource("details")
    class Details(val id: CurrencyUnitId, val parent: CurrencyUnitRoutes = CurrencyUnitRoutes())

    @Resource("new")
    class New(val parent: CurrencyUnitRoutes = CurrencyUnitRoutes())

    @Resource("delete")
    class Delete(val id: CurrencyUnitId, val parent: CurrencyUnitRoutes = CurrencyUnitRoutes())

    @Resource("edit")
    class Edit(val id: CurrencyUnitId, val parent: CurrencyUnitRoutes = CurrencyUnitRoutes())

    @Resource("preview")
    class Preview(val id: CurrencyUnitId, val parent: CurrencyUnitRoutes = CurrencyUnitRoutes())

    @Resource("update")
    class Update(val id: CurrencyUnitId, val parent: CurrencyUnitRoutes = CurrencyUnitRoutes())
}

fun Application.configureCurrencyUnitRouting() {
    routing {
        get<CurrencyUnitRoutes.All> { all ->
            logger.info { "Get all unit" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCurrencies(call, STORE.getState(), all.sort)
            }
        }
        get<CurrencyUnitRoutes.Details> { details ->
            logger.info { "Get details of unit ${details.id.value}" }

            val state = STORE.getState()
            val unit = state.getCurrencyUnitStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCurrencyUnitDetails(call, state, unit)
            }
        }
        get<CurrencyUnitRoutes.New> {
            logger.info { "Add new unit" }

            STORE.dispatch(CreateCurrencyUnit)

            call.respondRedirect(
                call.application.href(
                    CurrencyUnitRoutes.Edit(
                        STORE.getState().getCurrencyUnitStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<CurrencyUnitRoutes.Delete> { delete ->
            logger.info { "Delete unit ${delete.id.value}" }

            STORE.dispatch(DeleteCurrencyUnit(delete.id))

            call.respondRedirect(call.application.href(CurrencyUnitRoutes()))

            STORE.getState().save()
        }
        get<CurrencyUnitRoutes.Edit> { edit ->
            logger.info { "Get editor for unit ${edit.id.value}" }

            val state = STORE.getState()
            val unit = state.getCurrencyUnitStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCurrencyUnitEditor(call, state, unit)
            }
        }
        post<CurrencyUnitRoutes.Preview> { preview ->
            logger.info { "Preview unit ${preview.id.value}" }

            val state = STORE.getState()
            val unit = parseCurrencyUnit(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCurrencyUnitEditor(call, state, unit)
            }
        }
        post<CurrencyUnitRoutes.Update> { update ->
            logger.info { "Update unit ${update.id.value}" }

            val unit = parseCurrencyUnit(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateCurrencyUnit(unit))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCurrencies(
    call: ApplicationCall,
    state: State,
    sort: SortCurrencyUnit,
) {
    val units = state.sortCurrencyUnits(sort)
    val createLink = call.application.href(CurrencyUnitRoutes.New())
    val sortNameLink = call.application.href(CurrencyUnitRoutes.All())
    val sortValueLink = call.application.href(CurrencyUnitRoutes.All(SortCurrencyUnit.Value))

    simpleHtml("Currency Units") {
        field("Count", units.size)
        field("Sort") {
            link(sortNameLink, "Name")
            +" "
            link(sortValueLink, "Value")
        }
        table {
            tr {
                th { +"Name" }
                th { +"Currency" }
                th { +"Value" }
            }
            units.forEach { unit ->
                tr {
                    td { link(call, state, unit) }
                    td { link(call, state, unit.currency) }
                    tdSkipZero(unit.value)
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showCurrencyUnitDetails(
    call: ApplicationCall,
    state: State,
    unit: CurrencyUnit,
) {
    val backLink = call.application.href(CurrencyUnitRoutes.All())
    val deleteLink = call.application.href(CurrencyUnitRoutes.Delete(unit.id))
    val editLink = call.application.href(CurrencyUnitRoutes.Edit(unit.id))

    simpleHtmlDetails(unit) {
        showCurrencyUnit(call, state, unit)

        action(editLink, "Edit")
        if (state.canDeleteCurrencyUnit(unit.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showCurrencyUnitEditor(
    call: ApplicationCall,
    state: State,
    unit: CurrencyUnit,
) {
    val backLink = href(call, unit.id)
    val previewLink = call.application.href(CurrencyUnitRoutes.Preview(unit.id))
    val updateLink = call.application.href(CurrencyUnitRoutes.Update(unit.id))

    simpleHtmlEditor(unit) {
        formWithPreview(previewLink, updateLink, backLink) {
            editCurrencyUnit(state, unit)
        }
    }
}
