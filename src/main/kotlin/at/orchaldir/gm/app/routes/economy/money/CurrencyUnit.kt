package at.orchaldir.gm.app.routes.economy.money

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.economy.money.editCurrencyUnit
import at.orchaldir.gm.app.html.economy.money.parseCurrencyUnit
import at.orchaldir.gm.app.html.economy.money.showCurrencyUnit
import at.orchaldir.gm.app.html.economy.money.visualizeCurrencyUnit
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
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
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$CURRENCY_UNIT_TYPE")
class CurrencyUnitRoutes : Routes<CurrencyUnitId, SortCurrencyUnit> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCurrencyUnit) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: CurrencyUnitId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CurrencyUnitId) = call.application.href(Edit(id))
    override fun gallery(call: ApplicationCall) = call.application.href(Gallery())
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: CurrencyUnitId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: CurrencyUnitId) = call.application.href(Update(id))
}

fun Application.configureCurrencyUnitRouting() {
    routing {
        get<CurrencyUnitRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CurrencyUnitRoutes(),
                state.sortCurrencyUnits(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Currency") { tdLink(call, state, it.currency) },
                    tdColumn("Value") {
                        val currency = state.getCurrencyStorage().getOrThrow(it.currency)
                        val denomination = currency.getDenomination(it.denomination)
                        +denomination.display(it.number)
                    },
                    Column("Weight") { td(state.calculateWeight(it)) },
                    Column("Format") { tdEnum(it.format.getType()) },
                    Column("Materials") { tdInlineIds(call, state, it.format.getMaterials()) },
                    Column("Fonts") { tdInlineIds(call, state, it.format.getFonts()) },
                ),
            )
        }
        get<CurrencyUnitRoutes.Gallery> { gallery ->
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState(), gallery.sort)
            }
        }
        get<CurrencyUnitRoutes.Details> { details ->
            handleShowElement(details.id, CurrencyUnitRoutes(), HtmlBlockTag::showCurrencyUnit)
        }
        get<CurrencyUnitRoutes.New> {
            handleCreateElement(STORE.getState().getCurrencyUnitStorage()) { id ->
                CurrencyUnitRoutes.Edit(id)
            }
        }
        get<CurrencyUnitRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CurrencyUnitRoutes.All())
        }
        get<CurrencyUnitRoutes.Edit> { edit ->
            handleEditElement<CurrencyUnitId, CurrencyUnit, SortCurrencyUnit>(
                edit.id,
                CurrencyUnitRoutes()
            ) { call, state, unit ->
                visualizeCurrencyUnit(state, unit)
                editCurrencyUnit(call, state, unit)
            }
        }
        post<CurrencyUnitRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, CurrencyUnitRoutes(), ::parseCurrencyUnit) { call, state, unit ->
                visualizeCurrencyUnit(state, unit)
                editCurrencyUnit(call, state, unit)
            }
        }
        post<CurrencyUnitRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCurrencyUnit)
        }
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
        showSortTableLinks(call, SortCurrencyUnit.entries, CurrencyUnitRoutes())
        showGallery(call, state, units) { unit ->
            visualizeCurrencyUnit(state, CURRENCY_CONFIG, unit, maxSize)
        }

        back(backLink)
    }
}
