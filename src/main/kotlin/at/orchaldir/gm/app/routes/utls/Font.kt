@file:OptIn(ExperimentalEncodingApi::class)

package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.util.font.editFont
import at.orchaldir.gm.app.html.util.font.parseFont
import at.orchaldir.gm.app.html.util.font.showFont
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortFont
import at.orchaldir.gm.core.model.util.font.FONT_TYPE
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.util.sortFonts
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.visualization.visualizeString
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.core.*
import kotlinx.html.*
import mu.KotlinLogging
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private val logger = KotlinLogging.logger {}
private const val example = "abcdefghijklmnopqrstuvwxyz"
private val FONT_SIZE = fromMeters(40)

@Resource("/$FONT_TYPE")
class FontRoutes : Routes<FontId, SortFont> {
    @Resource("all")
    class All(
        val sort: SortFont = SortFont.Name,
        val parent: FontRoutes = FontRoutes(),
    )

    @Resource("details")
    class Details(val id: FontId, val parent: FontRoutes = FontRoutes())

    @Resource("new")
    class New(val parent: FontRoutes = FontRoutes())

    @Resource("delete")
    class Delete(val id: FontId, val parent: FontRoutes = FontRoutes())

    @Resource("edit")
    class Edit(val id: FontId, val parent: FontRoutes = FontRoutes())

    @Resource("preview")
    class Preview(val id: FontId, val parent: FontRoutes = FontRoutes())

    @Resource("update")
    class Update(val id: FontId, val parent: FontRoutes = FontRoutes())

    @Resource("upload")
    class Upload(val id: FontId, val parent: FontRoutes = FontRoutes())

    @Resource("uploader")
    class Uploader(val id: FontId, val parent: FontRoutes = FontRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortFont) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: FontId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: FontId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: FontId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: FontId) = call.application.href(Update(id))
}

fun Application.configureFontRouting() {
    routing {
        get<FontRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                FontRoutes(),
                state.sortFonts(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    tdColumn("Example", 1000) { svg(visualizeString(example, it, FONT_SIZE), 100) },
                    Column(listOf("Currency", "Units"))
                    { tdSkipZero(state.countCurrencyUnits(it.id)) },
                    Column("Texts") { tdSkipZero(state.countTexts(it.id)) },
                ),
            )
        }
        get<FontRoutes.Details> { details ->
            logger.info { "Get details of font ${details.id.value}" }

            val state = STORE.getState()
            val font = state.getFontStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFontDetails(call, state, FontRoutes(), font)
            }
        }
        get<FontRoutes.New> {
            handleCreateElement(FontRoutes(), STORE.getState().getFontStorage())
        }
        get<FontRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, FontRoutes.All())
        }
        get<FontRoutes.Edit> { edit ->
            handleEditElement(edit.id, FontRoutes(), HtmlBlockTag::editFont)
        }
        post<FontRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, FontRoutes(), ::parseFont, HtmlBlockTag::editFont)
        }
        post<FontRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseFont)
        }
        post<FontRoutes.Upload> { upload ->
            logger.info { "Get uploader for font ${upload.id.value}" }

            val state = STORE.getState()
            val font = state.getFontStorage().getOrThrow(upload.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFontUploader(call, font)
            }
        }
        post<FontRoutes.Uploader> { upload ->
            logger.info { "Upload font ${upload.id.value}" }

            val multipartData = call.receiveMultipart()
            var fileBytes = ""

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileBytes = Base64.encode(part.provider().readBytes())
                    }

                    else -> logger.info { "else: part=$part" }
                }
                part.dispose()
            }

            logger.info { "fileBytes=$fileBytes" }

            val oldFont = STORE.getState().getFontStorage().getOrThrow(upload.id)
            val font = oldFont.copy(base64 = fileBytes)

            STORE.dispatch(UpdateAction(font))

            call.respondRedirect(href(call, upload.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showFontDetails(
    call: ApplicationCall,
    state: State,
    routes: FontRoutes,
    font: Font,
) {
    val backLink = routes.all(call)
    val deleteLink = routes.delete(call, font.id)
    val editLink = routes.edit(call, font.id)
    val uploaderLink = call.application.href(FontRoutes.Uploader(font.id))

    simpleHtmlDetails(font) {
        svg(visualizeString(example, font, FONT_SIZE), 100)

        showFont(call, state, font)

        action(editLink, "Edit")
        action(uploaderLink, "Upload Font File")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showFontUploader(
    call: ApplicationCall,
    font: Font,
) {
    val backLink = href(call, font.id)
    val uploadLink = call.application.href(FontRoutes.Upload(font.id))

    simpleHtml(font, "Upload ") {
        form(encType = FormEncType.multipartFormData) {
            fileInput {
                formEncType = InputFormEncType.multipartFormData
                formMethod = InputFormMethod.post
                id = "myFile"
                name = "filename"
                accept = ".ttf,.otf"
            }

            button("Upload", uploadLink)
        }
        back(backLink)
    }
}
