package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.ACCESSORY_RARITY
import at.orchaldir.gm.app.parse.CLOTHING_SETS
import at.orchaldir.gm.app.parse.NAME
import at.orchaldir.gm.app.parse.parseFashion
import at.orchaldir.gm.core.action.CreateFashion
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ACCESSORIES
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCultures
import at.orchaldir.gm.core.selector.getItemTemplatesId
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

@Resource("/fashion")
class Fashions {
    @Resource("details")
    class Details(val id: FashionId, val parent: Fashions = Fashions())

    @Resource("new")
    class New(val parent: Fashions = Fashions())

    @Resource("delete")
    class Delete(val id: FashionId, val parent: Fashions = Fashions())

    @Resource("edit")
    class Edit(val id: FashionId, val parent: Fashions = Fashions())

    @Resource("update")
    class Update(val id: FashionId, val parent: Fashions = Fashions())
}

fun Application.configureFashionRouting() {
    routing {
        get<Fashions> {
            logger.info { "Get all fashions" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllFashions(call)
            }
        }
        get<Fashions.Details> { details ->
            logger.info { "Get details of fashion ${details.id.value}" }

            val state = STORE.getState()
            val fashion = state.fashion.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFashionDetails(call, state, fashion)
            }
        }
        get<Fashions.New> {
            logger.info { "Add new fashion" }

            STORE.dispatch(CreateFashion)

            call.respondRedirect(call.application.href(Fashions.Edit(STORE.getState().fashion.lastId)))

            STORE.getState().save()
        }
        get<Fashions.Delete> { delete ->
            logger.info { "Delete fashion ${delete.id.value}" }

            STORE.dispatch(DeleteFashion(delete.id))

            call.respondRedirect(call.application.href(Fashions()))

            STORE.getState().save()
        }
        get<Fashions.Edit> { edit ->
            logger.info { "Get editor for fashion ${edit.id.value}" }

            val state = STORE.getState()
            val fashion = state.fashion.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFashionEditor(call, state, fashion)
            }
        }
        post<Fashions.Update> { update ->
            logger.info { "Update fashion ${update.id.value}" }

            val fashion = parseFashion(update.id, call.receiveParameters())

            STORE.dispatch(UpdateFashion(fashion))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllFashions(call: ApplicationCall) {
    val fashion = STORE.getState().fashion.getAll().sortedBy { it.name }
    val count = fashion.size
    val createLink = call.application.href(Fashions.New())

    simpleHtml("Fashions") {
        field("Count", count.toString())
        showList(fashion) { fashion ->
            link(call, fashion)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showFashionDetails(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    val backLink = call.application.href(Fashions())
    val deleteLink = call.application.href(Fashions.Delete(fashion.id))
    val editLink = call.application.href(Fashions.Edit(fashion.id))

    simpleHtml("Fashion: ${fashion.name}") {
        field("Id", fashion.id.value.toString())
        field("Name", fashion.name)
        showRarityMap("Clothing Sets", fashion.clothingSets)
        showRarityMap("Accessories", fashion.accessories, ACCESSORIES)
        EquipmentType.entries.forEach {
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
        p { a(editLink) { +"Edit" } }
        if (state.canDelete(fashion.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showFashionEditor(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    val backLink = href(call, fashion.id)
    val updateLink = call.application.href(Fashions.Update(fashion.id))

    simpleHtml("Edit Fashion: ${fashion.name}") {
        field("Id", fashion.id.value.toString())
        form {
            field("Name") {
                textInput(name = NAME) {
                    value = fashion.name
                }
            }
            selectRarityMap("Clothing Sets", CLOTHING_SETS, fashion.clothingSets)
            selectRarityMap("Accessories", ACCESSORY_RARITY, fashion.accessories, false, ACCESSORIES)
            EquipmentType.entries.forEach {
                selectEquipmentType(state, fashion, it)
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun FORM.selectEquipmentType(
    state: State,
    fashion: Fashion,
    type: EquipmentType,
) {
    val items = state.getItemTemplatesId(type)

    if (items.isNotEmpty()) {
        val options = fashion.getOptions(type)
        selectRarityMap(type.name, type.name, state.itemTemplates, items, options) { it.name }
    }
}
