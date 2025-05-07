package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.text.editText
import at.orchaldir.gm.app.html.model.item.text.parseText
import at.orchaldir.gm.app.html.model.item.text.showText
import at.orchaldir.gm.app.html.model.showCreator
import at.orchaldir.gm.app.html.model.showOptionalDate
import at.orchaldir.gm.core.action.CreateText
import at.orchaldir.gm.core.action.DeleteText
import at.orchaldir.gm.core.action.UpdateText
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.util.SortText
import at.orchaldir.gm.core.selector.item.canDeleteText
import at.orchaldir.gm.core.selector.util.sortTexts
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.text.visualizeText
import at.orchaldir.gm.visualization.text.visualizeTextFormat
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

@Resource("/$TEXT_TYPE")
class TextRoutes {
    @Resource("all")
    class All(
        val sort: SortText = SortText.Name,
        val parent: TextRoutes = TextRoutes(),
    )

    @Resource("gallery")
    class Gallery(val parent: TextRoutes = TextRoutes())

    @Resource("details")
    class Details(val id: TextId, val parent: TextRoutes = TextRoutes())

    @Resource("new")
    class New(val parent: TextRoutes = TextRoutes())

    @Resource("delete")
    class Delete(val id: TextId, val parent: TextRoutes = TextRoutes())

    @Resource("edit")
    class Edit(val id: TextId, val parent: TextRoutes = TextRoutes())

    @Resource("preview")
    class Preview(val id: TextId, val parent: TextRoutes = TextRoutes())

    @Resource("update")
    class Update(val id: TextId, val parent: TextRoutes = TextRoutes())
}

fun Application.configureTextRouting() {
    routing {
        get<TextRoutes.All> { all ->
            logger.info { "Get all texts" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTexts(call, STORE.getState(), all.sort)
            }
        }
        get<TextRoutes.Gallery> {
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState())
            }
        }
        get<TextRoutes.Details> { details ->
            logger.info { "Get details of text ${details.id.value}" }

            val state = STORE.getState()
            val text = state.getTextStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTextDetails(call, state, text)
            }
        }
        get<TextRoutes.New> {
            logger.info { "Add new text" }

            STORE.dispatch(CreateText)

            call.respondRedirect(call.application.href(TextRoutes.Edit(STORE.getState().getTextStorage().lastId)))

            STORE.getState().save()
        }
        get<TextRoutes.Delete> { delete ->
            logger.info { "Delete text ${delete.id.value}" }

            STORE.dispatch(DeleteText(delete.id))

            call.respondRedirect(call.application.href(TextRoutes()))

            STORE.getState().save()
        }
        get<TextRoutes.Edit> { edit ->
            logger.info { "Get editor for text ${edit.id.value}" }

            val state = STORE.getState()
            val text = state.getTextStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTextEditor(call, state, text)
            }
        }
        post<TextRoutes.Preview> { preview ->
            logger.info { "Get preview for text ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val text = parseText(formParameters, STORE.getState(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTextEditor(call, STORE.getState(), text)
            }
        }
        post<TextRoutes.Update> { update ->
            logger.info { "Update text ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val text = parseText(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateText(text))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllTexts(
    call: ApplicationCall,
    state: State,
    sort: SortText,
) {
    val texts = state.sortTexts(sort)
    val createLink = call.application.href(TextRoutes.New())
    val galleryLink = call.application.href(TextRoutes.Gallery())

    simpleHtml("Texts") {
        action(galleryLink, "Gallery")
        field("Count", texts.size)
        showSortTableLinks(call, SortText.entries, TextRoutes(), TextRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Origin" }
                th { +"Publisher" }
                th { +"Language" }
                th { +"Format" }
                th { +"Materials" }
                th { +"Pages" }
                th { +"Spells" }
            }
            texts.forEach { text ->
                tr {
                    td { link(call, state, text) }
                    td { showOptionalDate(call, state, text.date) }
                    td {
                        when (text.origin) {
                            is OriginalText -> {
                                +"Written by "
                                showCreator(call, state, text.origin.creator())
                            }

                            is TranslatedText -> {
                                +"Translated by "
                                showCreator(call, state, text.origin.creator())
                            }
                        }
                    }
                    td { optionalLink(call, state, text.publisher) }
                    td { link(call, state, text.language) }
                    tdEnum(text.format.getType())
                    tdInlineLinks(call, state, text.materials())
                    tdSkipZero(text.content.pages())
                    tdSkipZero(text.content.spells().size)
                }
            }
        }

        action(createLink, "Add")
        back("/")

        showTextFormatCount(texts)
        showTextOriginCount(texts)
        showCreatorCount(call, state, texts, "Creators")
        showLanguageCountForTexts(call, state, texts)
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
) {
    val texts = state.sortTexts()
        .filter { it.format !is UndefinedTextFormat }
    val maxSize = Size2d.square(texts.maxOf { TEXT_CONFIG.calculateSize(it.format).height })
    val size = TEXT_CONFIG.addPadding(maxSize)
    val backLink = call.application.href(TextRoutes.All())

    simpleHtml("Texts") {
        showGallery(call, texts, { it.getNameWithDate(state) }) { text ->
            visualizeTextFormat(state, TEXT_CONFIG, text, size)
        }

        back(backLink)
    }
}

private fun HTML.showTextDetails(
    call: ApplicationCall,
    state: State,
    text: Text,
) {
    val backLink = call.application.href(TextRoutes.All())
    val deleteLink = call.application.href(TextRoutes.Delete(text.id))
    val editLink = call.application.href(TextRoutes.Edit(text.id))
    val svg = visualizeText(state, TEXT_CONFIG, text)

    simpleHtml("Text: ${text.name(state)}") {
        if (text.format !is UndefinedTextFormat) {
            svg(svg, 20)
        }
        showText(call, state, text)

        action(editLink, "Edit")

        if (state.canDeleteText(text.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showTextEditor(
    call: ApplicationCall,
    state: State,
    text: Text,
) {
    val backLink = href(call, text.id)
    val previewLink = call.application.href(TextRoutes.Preview(text.id))
    val updateLink = call.application.href(TextRoutes.Update(text.id))
    val svg = visualizeText(state, TEXT_CONFIG, text)

    simpleHtmlEditor(text, true) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                editText(state, text)
            }
        }, {
            svg(svg, 50)
        })

    }
}
