package at.orchaldir.gm.app.routes.economy.money

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.editCurrencyUnit
import at.orchaldir.gm.app.html.economy.money.parseCurrencyUnit
import at.orchaldir.gm.app.html.economy.money.showCurrencyUnit
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.action.DeleteCurrencyUnit
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CURRENCY_UNIT_TYPE
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.economy.money.UndefinedCurrencyFormat
import at.orchaldir.gm.core.model.util.SortCurrencyUnit
import at.orchaldir.gm.core.selector.economy.money.calculateWeight
import at.orchaldir.gm.core.selector.util.sortCurrencyUnits
import at.orchaldir.gm.prototypes.visualization.currency.CURRENCY_CONFIG
import at.orchaldir.gm.visualization.currency.visualizeCurrencyUnit
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

@Resource("/$CURRENCY_UNIT_TYPE")
class CurrencyUnitRoutes {
    @Resource("all")
    class All(
        val sort: SortCurrencyUnit = SortCurrencyUnit.Name,
        val parent: CurrencyUnitRoutes = CurrencyUnitRoutes(),
    )

    @Resource("gallery")
    class Gallery(
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
        get<CurrencyUnitRoutes.Gallery> { gallery ->
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState(), gallery.sort)
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
            handleCreateElement(STORE.getState().getCurrencyUnitStorage()) { id ->
                CurrencyUnitRoutes.Edit(id)
            }
        }
        get<CurrencyUnitRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteCurrencyUnit(delete.id), CurrencyUnitRoutes())
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
            val unit = parseCurrencyUnit(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCurrencyUnitEditor(call, state, unit)
            }
        }
        post<CurrencyUnitRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCurrencyUnit)
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
    val galleryLink = call.application.href(CurrencyUnitRoutes.Gallery())

    simpleHtml("Currency Units") {
        action(galleryLink, "Gallery")
        field("Count", units.size)
        showSortTableLinks(call, SortCurrencyUnit.entries, CurrencyUnitRoutes(), CurrencyUnitRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Currency" }
                th { +"Value" }
                th { +"Weight" }
                th { +"Format" }
                th { +"Materials" }
                th { +"Fonts" }
            }
            units.forEach { unit ->
                val currency = state.getCurrencyStorage().getOrThrow(unit.currency)
                val denomination = currency.getDenomination(unit.denomination)

                tr {
                    tdLink(call, state, unit)
                    tdLink(call, state, currency)
                    td { +denomination.display(unit.number) }
                    td(state.calculateWeight(unit))
                    tdEnum(unit.format.getType())
                    tdInlineIds(call, state, unit.format.getMaterials())
                    tdInlineIds(call, state, unit.format.getFonts())
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
    sort: SortCurrencyUnit,
) {
    val units = state.sortCurrencyUnits(sort)
        .filter { it.format != UndefinedCurrencyFormat }
    val maxSize = units
        .map { CURRENCY_CONFIG.calculatePaddedSize(it.format) }
        .maxBy { it.height.value() }
    val backLink = call.application.href(CurrencyUnitRoutes.All())

    simpleHtml("Currency Units") {
        showSortTableLinks(call, SortCurrencyUnit.entries, CurrencyUnitRoutes(), CurrencyUnitRoutes::Gallery)
        showGallery(call, state, units) { unit ->
            visualizeCurrencyUnit(state, CURRENCY_CONFIG, unit, maxSize)
        }

        back(backLink)
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
        visualizeUnit(state, unit)
        showCurrencyUnit(call, state, unit)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
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
        visualizeUnit(state, unit)
        formWithPreview(previewLink, updateLink, backLink) {
            editCurrencyUnit(state, unit)
        }
    }
}

private fun HtmlBlockTag.visualizeUnit(state: State, unit: CurrencyUnit) {
    val frontSvg = visualizeCurrencyUnit(state, CURRENCY_CONFIG, unit)

    svg(frontSvg, 20)
}
