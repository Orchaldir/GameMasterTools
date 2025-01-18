package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.text.editTextFormat
import at.orchaldir.gm.app.html.model.text.showTextFormat
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.item.parseText
import at.orchaldir.gm.core.action.CreateText
import at.orchaldir.gm.core.action.DeleteText
import at.orchaldir.gm.core.action.UpdateText
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.util.SortText
import at.orchaldir.gm.core.selector.item.canDeleteText
import at.orchaldir.gm.core.selector.item.getTranslationsOf
import at.orchaldir.gm.core.selector.sortTexts
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
    val sortNameLink = call.application.href(TextRoutes.All(SortText.Name))
    val sortAgeLink = call.application.href(TextRoutes.All(SortText.Age))

    simpleHtml("Texts") {
        action(galleryLink, "Gallery")
        field("Count", texts.size)
        field("Sort") {
            link(sortNameLink, "Name")
            +" "
            link(sortAgeLink, "Age")
        }

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Origin" }
                th { +"Creator" }
                th { +"Language" }
                th { +"Format" }
            }
            texts.forEach { text ->
                tr {
                    td { link(call, state, text) }
                    td { showOptionalDate(call, state, text.date) }
                    td { +text.origin.getType().toString() }
                    td { showCreator(call, state, text.origin.creator()) }
                    td { link(call, state, text.language) }
                    td { +text.format.getType().toString() }
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
    val texts = state
        .getTextStorage()
        .getAll()
        .filter { it.format !is UndefinedTextFormat }
        .sortedBy { it.name }
    val maxSize = Size2d.square(texts.maxOf { it.format.getTextSize().height })
    val size = TEXT_CONFIG.addPadding(maxSize)
    val backLink = call.application.href(TextRoutes.All())

    simpleHtml("Texts") {

        div("grid-container") {
            texts.forEach { text ->
                val svg = visualizeTextFormat(state, TEXT_CONFIG, text, size)

                div("grid-item") {
                    a(href(call, text.id)) {
                        div {
                            if (text.date != null) {
                                +"${text.name} ("
                                +displayDate(state, text.date)
                                +")"
                            } else {
                                +text.name
                            }
                        }
                        svg(svg, 100)
                    }
                }
            }
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

    simpleHtml("Text: ${text.name}") {
        if (text.format !is UndefinedTextFormat) {
            svg(svg, 20)
        }
        showOrigin(call, state, text)
        optionalField(call, state, "Date", text.date)
        fieldLink("Language", call, state, text.language)
        showTextFormat(call, state, text.format)

        showList("Translations", state.getTranslationsOf(text.id)) { text ->
            link(call, state, text)
        }

        action(editLink, "Edit")

        if (state.canDeleteText(text.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun BODY.showOrigin(
    call: ApplicationCall,
    state: State,
    text: Text,
) {
    when (text.origin) {
        is OriginalText -> field("Author") {
            showCreator(call, state, text.origin.author)
        }

        is TranslatedText -> {
            fieldLink("Translation Of", call, state, text.origin.text)
            field("Translator") {
                showCreator(call, state, text.origin.translator)
            }
        }
    }
}

private fun HTML.showTextEditor(
    call: ApplicationCall,
    state: State,
    text: Text,
) {
    val languages = state.getLanguageStorage().getAll()
        .sortedBy { it.name }
    val backLink = href(call, text.id)
    val previewLink = call.application.href(TextRoutes.Preview(text.id))
    val updateLink = call.application.href(TextRoutes.Update(text.id))
    val svg = visualizeText(state, TEXT_CONFIG, text)

    simpleHtml("Edit Text: ${text.name}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectName(text.name)
                editOrigin(state, text)
                selectOptionalDate(state, "Date", text.date, DATE)
                selectValue("Language", LANGUAGE, languages, true) { l ->
                    label = l.name
                    value = l.id.value.toString()
                    selected = l.id == text.language
                }
                editTextFormat(state, text.format)
                button("Update", updateLink)
            }
            back(backLink)
        }, {
            svg(svg, 50)
        })

    }
}

private fun FORM.editOrigin(
    state: State,
    text: Text,
) {
    selectValue("Origin", ORIGIN, TextOriginType.entries, text.origin.getType(), true)

    when (text.origin) {
        is OriginalText -> selectCreator(state, text.origin.author, text.id, text.date, "Author")
        is TranslatedText -> {
            val otherTexts = state.getTextStorage().getAllExcept(text.id)

            selectValue("Translation Of", combine(ORIGIN, REFERENCE), otherTexts) { translated ->
                label = if (translated.date != null) {
                    "${translated.name} (" + displayDate(state, translated.date) + ")"
                } else {
                    translated.name
                }
                value = translated.id.value.toString()
                selected = translated.id == text.origin.text
            }
            selectCreator(state, text.origin.translator, text.id, text.date, "Translator")
        }
    }
}
