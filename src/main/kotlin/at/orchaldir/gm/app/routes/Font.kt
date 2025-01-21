@file:OptIn(ExperimentalEncodingApi::class)

package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.html.model.showOptionalDate
import at.orchaldir.gm.app.parse.parseFont
import at.orchaldir.gm.core.action.CreateFont
import at.orchaldir.gm.core.action.DeleteFont
import at.orchaldir.gm.core.action.UpdateFont
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.FONT_TYPE
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.util.SortFont
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.item.countText
import at.orchaldir.gm.core.selector.item.getTexts
import at.orchaldir.gm.core.selector.util.sortFonts
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

@Resource("/$FONT_TYPE")
class FontRoutes {
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
}

fun Application.configureFontRouting() {
    routing {
        get<FontRoutes.All> { all ->
            logger.info { "Get all fonts" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllFonts(call, STORE.getState(), all.sort)
            }
        }
        get<FontRoutes.Details> { details ->
            logger.info { "Get details of font ${details.id.value}" }

            val state = STORE.getState()
            val font = state.getFontStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFontDetails(call, state, font)
            }
        }
        get<FontRoutes.New> {
            logger.info { "Add new font" }

            STORE.dispatch(CreateFont)

            call.respondRedirect(
                call.application.href(
                    FontRoutes.Edit(
                        STORE.getState().getFontStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<FontRoutes.Delete> { delete ->
            logger.info { "Delete font ${delete.id.value}" }

            STORE.dispatch(DeleteFont(delete.id))

            call.respondRedirect(call.application.href(FontRoutes()))

            STORE.getState().save()
        }
        get<FontRoutes.Edit> { edit ->
            logger.info { "Get editor for font ${edit.id.value}" }

            val state = STORE.getState()
            val font = state.getFontStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFontEditor(call, state, font)
            }
        }
        post<FontRoutes.Preview> { preview ->
            logger.info { "Get preview for font ${preview.id.value}" }

            val state = STORE.getState()
            val font = parseFont(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFontEditor(call, state, font)
            }
        }
        post<FontRoutes.Update> { update ->
            logger.info { "Update font ${update.id.value}" }

            val state = STORE.getState()
            val font = parseFont(call.receiveParameters(), state, update.id)

            STORE.dispatch(UpdateFont(font))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
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

            STORE.dispatch(UpdateFont(font))

            call.respondRedirect(href(call, upload.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllFonts(
    call: ApplicationCall,
    state: State,
    sort: SortFont,
) {
    val fonts = state.sortFonts(sort)
    val createLink = call.application.href(FontRoutes.New())
    val sortNameLink = call.application.href(FontRoutes.All(SortFont.Name))
    val sortAgeLink = call.application.href(FontRoutes.All(SortFont.Age))

    simpleHtml("Fonts") {
        field("Count", fonts.size)
        field("Sort") {
            link(sortNameLink, "Name")
            +" "
            link(sortAgeLink, "Age")
        }

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th {
                    style = "width:1000px"
                    +"Example"
                }
                th { +"Texts" }
            }
            fonts.forEach { font ->
                tr {
                    td { link(call, font) }
                    td { showOptionalDate(call, state, font.date) }
                    td { svg(visualizeString(example, font, 40.0f), 100) }
                    tdSkipZero(state.countText(font.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showFontDetails(
    call: ApplicationCall,
    state: State,
    font: Font,
) {
    val backLink = call.application.href(FontRoutes.All())
    val deleteLink = call.application.href(FontRoutes.Delete(font.id))
    val editLink = call.application.href(FontRoutes.Edit(font.id))
    val uploaderLink = call.application.href(FontRoutes.Uploader(font.id))

    simpleHtml("Font: ${font.name}") {
        svg(visualizeString(example, font, 40.0f), 100)
        optionalField(call, state, "Date", font.date)
        field("Base64") {
            textArea("10", "200", TextAreaWrap.soft) {
                +font.base64
            }
        }
        h2 { +"Usage" }
        showList("Texts", state.getTexts(font.id)) { text ->
            link(call, state, text)
        }

        action(editLink, "Edit")
        action(uploaderLink, "Upload Font File")
        if (state.canDelete(font.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showFontEditor(
    call: ApplicationCall,
    state: State,
    font: Font,
) {
    val backLink = href(call, font.id)
    val previewLink = call.application.href(FontRoutes.Preview(font.id))
    val updateLink = call.application.href(FontRoutes.Update(font.id))

    simpleHtml("Edit Font: ${font.name}") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(font.name)
            selectOptionalDate(state, "Date", font.date, DATE)

            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun HTML.showFontUploader(
    call: ApplicationCall,
    font: Font,
) {
    val backLink = href(call, font.id)
    val uploadLink = call.application.href(FontRoutes.Upload(font.id))

    simpleHtml("Upload Font: ${font.name}") {
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
