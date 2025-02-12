package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.ACCESSORY_RARITY
import at.orchaldir.gm.app.CLOTHING_SETS
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseFashion
import at.orchaldir.gm.core.action.CreateFashion
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.fashion.FASHION_TYPE
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.equipment.ACCESSORIES
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCultures
import at.orchaldir.gm.core.selector.item.getEquipmentId
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.FORM
import kotlinx.html.HTML
import kotlinx.html.form
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$FASHION_TYPE")
class FashionRoutes {
    @Resource("details")
    class Details(val id: FashionId, val parent: FashionRoutes = FashionRoutes())

    @Resource("new")
    class New(val parent: FashionRoutes = FashionRoutes())

    @Resource("delete")
    class Delete(val id: FashionId, val parent: FashionRoutes = FashionRoutes())

    @Resource("edit")
    class Edit(val id: FashionId, val parent: FashionRoutes = FashionRoutes())

    @Resource("update")
    class Update(val id: FashionId, val parent: FashionRoutes = FashionRoutes())
}

fun Application.configureFashionRouting() {
    routing {
        get<FashionRoutes> {
            logger.info { "Get all fashions" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllFashions(call)
            }
        }
        get<FashionRoutes.Details> { details ->
            logger.info { "Get details of fashion ${details.id.value}" }

            val state = STORE.getState()
            val fashion = state.getFashionStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFashionDetails(call, state, fashion)
            }
        }
        get<FashionRoutes.New> {
            logger.info { "Add new fashion" }

            STORE.dispatch(CreateFashion)

            call.respondRedirect(call.application.href(FashionRoutes.Edit(STORE.getState().getFashionStorage().lastId)))

            STORE.getState().save()
        }
        get<FashionRoutes.Delete> { delete ->
            logger.info { "Delete fashion ${delete.id.value}" }

            STORE.dispatch(DeleteFashion(delete.id))

            call.respondRedirect(call.application.href(FashionRoutes()))

            STORE.getState().save()
        }
        get<FashionRoutes.Edit> { edit ->
            logger.info { "Get editor for fashion ${edit.id.value}" }

            val state = STORE.getState()
            val fashion = state.getFashionStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFashionEditor(call, state, fashion)
            }
        }
        post<FashionRoutes.Update> { update ->
            logger.info { "Update fashion ${update.id.value}" }

            val fashion = parseFashion(update.id, call.receiveParameters())

            STORE.dispatch(UpdateFashion(fashion))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllFashions(call: ApplicationCall) {
    val fashion = STORE.getState().getFashionStorage().getAll().sortedBy { it.name }
    val createLink = call.application.href(FashionRoutes.New())

    simpleHtml("Fashions") {
        field("Count", fashion.size)
        showList(fashion) { fashion ->
            link(call, fashion)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showFashionDetails(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    val backLink = call.application.href(FashionRoutes())
    val deleteLink = call.application.href(FashionRoutes.Delete(fashion.id))
    val editLink = call.application.href(FashionRoutes.Edit(fashion.id))

    simpleHtml("Fashion: ${fashion.name}") {
        field("Name", fashion.name)
        showRarityMap("Clothing Sets", fashion.clothingSets)
        showRarityMap("Accessories", fashion.accessories, ACCESSORIES)
        EquipmentDataType.entries.forEach {
            val options = fashion.getOptions(it)

            if (options.isNotEmpty()) {
                showRarityMap(it.name, options) { id ->
                    link(call, state, id)
                }
            }
        }
        showList("Cultures", state.getCultures(fashion.id)) { culture ->
            link(call, culture)
        }
        action(editLink, "Edit")
        if (state.canDelete(fashion.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showFashionEditor(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    val backLink = href(call, fashion.id)
    val updateLink = call.application.href(FashionRoutes.Update(fashion.id))

    simpleHtml("Edit Fashion: ${fashion.name}") {
        form {
            selectName(fashion.name)
            selectRarityMap("Clothing Sets", CLOTHING_SETS, fashion.clothingSets)
            selectRarityMap("Accessories", ACCESSORY_RARITY, fashion.accessories, false, ACCESSORIES)
            EquipmentDataType.entries.forEach {
                selectEquipmentType(state, fashion, it)
            }
            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun FORM.selectEquipmentType(
    state: State,
    fashion: Fashion,
    type: EquipmentDataType,
) {
    val items = state.getEquipmentId(type)

    if (items.isNotEmpty()) {
        val options = fashion.getOptions(type)
        selectRarityMap(type.name, type.name, state.getEquipmentStorage(), items, options) { it.name }
    }
}
