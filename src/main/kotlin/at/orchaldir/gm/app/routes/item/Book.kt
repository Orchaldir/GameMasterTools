package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.parse.item.parseBook
import at.orchaldir.gm.core.action.CreateBook
import at.orchaldir.gm.core.action.DeleteBook
import at.orchaldir.gm.core.action.UpdateBook
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.book.BOOK_TYPE
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.BookId
import at.orchaldir.gm.core.selector.item.canDeleteBook
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
                showAllBooks(call)
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

private fun HTML.showAllBooks(call: ApplicationCall) {
    val books = STORE.getState().getBookStorage().getAll().sortedBy { it.name }
    val count = books.size
    val createLink = call.application.href(BookRoutes.New())

    simpleHtml("Books") {
        field("Count", count.toString())
        showList(books) { book ->
            link(call, book)
        }
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

    simpleHtml("Book: ${book.name}") {
        field("Name", book.name)
        optionalField(call, state, "Date", book.date)
        action(editLink, "Edit")

        if (state.canDeleteBook(book.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showBookEditor(
    call: ApplicationCall,
    state: State,
    book: Book,
) {
    val backLink = href(call, book.id)
    val previewLink = call.application.href(BookRoutes.Preview(book.id))
    val updateLink = call.application.href(BookRoutes.Update(book.id))

    simpleHtml("Edit Book: ${book.name}") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(book.name)
            selectOptionalDate(state, "Date", book.date, DATE)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
