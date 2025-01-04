package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.item.parseBook
import at.orchaldir.gm.core.action.CreateBook
import at.orchaldir.gm.core.action.DeleteBook
import at.orchaldir.gm.core.action.UpdateBook
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.book.*
import at.orchaldir.gm.core.selector.item.canDeleteBook
import at.orchaldir.gm.core.selector.item.getTranslationsOf
import at.orchaldir.gm.prototypes.visualization.book.BOOK_CONFIG
import at.orchaldir.gm.visualization.book.book.visualizeBook
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

@Resource("/$BOOK_TYPE")
class BookRoutes {
    @Resource("details")
    class Details(val id: BookId, val parent: BookRoutes = BookRoutes())

    @Resource("new")
    class New(val parent: BookRoutes = BookRoutes())

    @Resource("delete")
    class Delete(val id: BookId, val parent: BookRoutes = BookRoutes())

    @Resource("edit")
    class Edit(val id: BookId, val parent: BookRoutes = BookRoutes())

    @Resource("preview")
    class Preview(val id: BookId, val parent: BookRoutes = BookRoutes())

    @Resource("update")
    class Update(val id: BookId, val parent: BookRoutes = BookRoutes())
}

fun Application.configureBookRouting() {
    routing {
        get<BookRoutes> {
            logger.info { "Get all books" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllBooks(call, STORE.getState())
            }
        }
        get<BookRoutes.Details> { details ->
            logger.info { "Get details of book ${details.id.value}" }

            val state = STORE.getState()
            val book = state.getBookStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBookDetails(call, state, book)
            }
        }
        get<BookRoutes.New> {
            logger.info { "Add new book" }

            STORE.dispatch(CreateBook)

            call.respondRedirect(call.application.href(BookRoutes.Edit(STORE.getState().getBookStorage().lastId)))

            STORE.getState().save()
        }
        get<BookRoutes.Delete> { delete ->
            logger.info { "Delete book ${delete.id.value}" }

            STORE.dispatch(DeleteBook(delete.id))

            call.respondRedirect(call.application.href(BookRoutes()))

            STORE.getState().save()
        }
        get<BookRoutes.Edit> { edit ->
            logger.info { "Get editor for book ${edit.id.value}" }

            val state = STORE.getState()
            val book = state.getBookStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBookEditor(call, state, book)
            }
        }
        post<BookRoutes.Preview> { preview ->
            logger.info { "Get preview for book ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val book = parseBook(formParameters, STORE.getState(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBookEditor(call, STORE.getState(), book)
            }
        }
        post<BookRoutes.Update> { update ->
            logger.info { "Update book ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val book = parseBook(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateBook(book))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllBooks(
    call: ApplicationCall,
    state: State,
) {
    val books = STORE.getState().getBookStorage().getAll().sortedBy { it.name }
    val count = books.size
    val createLink = call.application.href(BookRoutes.New())

    simpleHtml("Books") {
        field("Count", count.toString())

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Origin" }
                th { +"Creator" }
                th { +"Language" }
                th { +"Format" }
            }
            books.forEach { book ->
                tr {
                    td { link(call, state, book) }
                    td { showOptionalDate(call, state, book.date) }
                    td { +book.origin.getType().toString() }
                    td { showCreator(call, state, book.origin.creator()) }
                    td { link(call, state, book.language) }
                    td { +book.format.getType().toString() }
                }
            }
        }

        showBookOriginTypeCount(books)
        showCreatorCount(call, state, books, "Creators")
        showLanguageCountForBooks(call, state, books)

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showBookDetails(
    call: ApplicationCall,
    state: State,
    book: Book,
) {
    val backLink = call.application.href(BookRoutes())
    val deleteLink = call.application.href(BookRoutes.Delete(book.id))
    val editLink = call.application.href(BookRoutes.Edit(book.id))
    val svg = visualizeBook(BOOK_CONFIG, book)

    simpleHtml("Book: ${book.name}") {
        svg(svg, 20)
        showOrigin(call, state, book)
        optionalField(call, state, "Date", book.date)
        fieldLink("Language", call, state, book.language)
        showBookFormat(call, state, book.format)

        showList("Translations", state.getTranslationsOf(book.id)) { book ->
            link(call, state, book)
        }

        action(editLink, "Edit")

        if (state.canDeleteBook(book.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun BODY.showOrigin(
    call: ApplicationCall,
    state: State,
    book: Book,
) {
    when (book.origin) {
        is OriginalBook -> field("Author") {
            showCreator(call, state, book.origin.author)
        }

        is TranslatedBook -> {
            fieldLink("Translation Of", call, state, book.origin.book)
            field("Translator") {
                showCreator(call, state, book.origin.translator)
            }
        }
    }
}

private fun HTML.showBookEditor(
    call: ApplicationCall,
    state: State,
    book: Book,
) {
    val languages = state.getLanguageStorage().getAll()
        .sortedBy { it.name }
    val backLink = href(call, book.id)
    val previewLink = call.application.href(BookRoutes.Preview(book.id))
    val updateLink = call.application.href(BookRoutes.Update(book.id))

    simpleHtml("Edit Book: ${book.name}") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(book.name)
            editOrigin(state, book)
            selectOptionalDate(state, "Date", book.date, DATE)
            selectValue("Language", LANGUAGE, languages, true) { l ->
                label = l.name
                value = l.id.value.toString()
                selected = l.id == book.language
            }
            editBookFormat(state, book.format)
            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun FORM.editOrigin(
    state: State,
    book: Book,
) {
    selectValue("Origin", ORIGIN, BookOriginType.entries, book.origin.getType(), true)

    when (book.origin) {
        is OriginalBook -> selectCreator(state, book.origin.author, book.id, book.date, "Author")
        is TranslatedBook -> {
            val otherBooks = state.getBookStorage().getAllExcept(book.id)
            selectValue("Translation Of", combine(ORIGIN, REFERENCE), otherBooks) { translated ->
                label = translated.name
                value = translated.id.value.toString()
                selected = translated.id == book.origin.book
            }
            selectCreator(state, book.origin.translator, book.id, book.date, "Translator")
        }
    }
}
