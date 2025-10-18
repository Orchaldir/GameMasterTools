package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.text.editText
import at.orchaldir.gm.app.html.item.text.parseText
import at.orchaldir.gm.app.html.item.text.showText
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.content.UndefinedTextContent
import at.orchaldir.gm.core.model.util.SortText
import at.orchaldir.gm.core.selector.util.sortTexts
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.text.content.visualizePageOfContent
import at.orchaldir.gm.visualization.text.visualizeText
import at.orchaldir.gm.visualization.text.visualizeTextFormat
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$TEXT_TYPE")
class TextRoutes : Routes<TextId, SortText> {
    @Resource("all")
    class All(
        val sort: SortText = SortText.Name,
        val parent: TextRoutes = TextRoutes(),
    )

    @Resource("gallery")
    class Gallery(val parent: TextRoutes = TextRoutes())

    @Resource("details")
    class Details(
        val id: TextId,
        val pageIndex: Int = 0,
        val parent: TextRoutes = TextRoutes(),
    )

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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortText) = call.application.href(All(sort))
    override fun gallery(call: ApplicationCall) = call.application.href(Gallery())
    override fun delete(call: ApplicationCall, id: TextId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: TextId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureTextRouting() {
    routing {
        get<TextRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                TextRoutes(),
                state.sortTexts(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createOriginColumn(call, state, ::TextId),
                    Column("Publisher") { tdLink(call, state, it.publisher) },
                    Column("Language") { tdLink(call, state, it.language) },
                    Column("Format") { tdEnum(it.format.getType()) },
                    Column("Materials") { tdInlineIds(call, state, it.materials()) },
                    createSkipZeroColumn("Pages") { it.content.pages() },
                    createSkipZeroColumnFromCollection("Spells") { it.content.spells() },
                ),
            ) {
                showTextFormatCount(it)
                showTextOriginCount(it)
                showCreatorCount(call, state, it, "Creators")
                showLanguageCountForTexts(call, state, it)
            }
        }
        get<TextRoutes.Gallery> {
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState())
            }
        }
        get<TextRoutes.Details> { details ->
            handleShowElement<TextId, Text, SortText>(details.id, TextRoutes()) { call, state, text ->
                visualizeFrontAndContent(call, state, text, 20, details.pageIndex, true)
                showText(call, state, text)
            }
        }
        get<TextRoutes.New> {
            handleCreateElement(STORE.getState().getTextStorage()) { id ->
                TextRoutes.Edit(id)
            }
        }
        get<TextRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, TextRoutes.All())
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
            val text = parseText(STORE.getState(), formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTextEditor(call, STORE.getState(), text)
            }
        }
        post<TextRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseText)
        }
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
) {
    val texts = state.sortTexts()
        .filter { it.format !is UndefinedTextFormat }
    val maxHeight = texts
        .map { TEXT_CONFIG.calculateClosedSize(it.format).height }
        .maxBy { it.value() }
    val maxSize = Size2d.square(maxHeight)
    val size = TEXT_CONFIG.addPadding(maxSize)
    val backLink = call.application.href(TextRoutes.All())

    simpleHtml("Texts") {
        showGallery(call, texts, { it.getNameWithDate(state) }) { text ->
            visualizeTextFormat(state, TEXT_CONFIG, text, size)
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

    simpleHtmlEditor(text, true) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                editText(state, text)
            }
        }, {
            visualizeFrontAndContent(call, state, text, 40, 0)
        })

    }
}

private fun HtmlBlockTag.visualizeFrontAndContent(
    call: ApplicationCall,
    state: State,
    text: Text,
    width: Int,
    pageIndex: Int,
    showActions: Boolean = false,
) {
    if (text.format is Book || (text.format is Scroll && text.content is UndefinedTextContent)) {
        val frontSvg = visualizeText(state, TEXT_CONFIG, text)
        svg(frontSvg, width)
    }
    if (text.format !is UndefinedTextFormat && text.content !is UndefinedTextContent) {
        val contentSvg = visualizePageOfContent(state, TEXT_CONFIG, text, pageIndex)!!
        svg(contentSvg, width)

        if (showActions) {
            val pages = text.content.pages()

            field("Page", "${pageIndex + 1} of $pages")

            if (pageIndex < pages - 1) {
                val nextPageLink = call.application.href(TextRoutes.Details(text.id, pageIndex + 1))
                action(nextPageLink, "Next Page")
            }
            if (pageIndex > 0) {
                val firstPageLink = call.application.href(TextRoutes.Details(text.id, 0))
                val previousPageLink = call.application.href(TextRoutes.Details(text.id, pageIndex - 1))
                action(previousPageLink, "Previous Page")
                action(firstPageLink, "First Page")
            }
        }
    }
}
