package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateBook
import at.orchaldir.gm.core.action.DeleteBook
import at.orchaldir.gm.core.action.UpdateBook
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.selector.item.canDeleteBook
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BOOK: Reducer<CreateBook, State> = { state, _ ->
    val book = Book(state.getBookStorage().nextId)

    noFollowUps(state.updateStorage(state.getBookStorage().add(book)))
}

val DELETE_BOOK: Reducer<DeleteBook, State> = { state, action ->
    state.getBookStorage().require(action.id)
    require(state.canDeleteBook(action.id)) { "Book ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getBookStorage().remove(action.id)))
}

val UPDATE_BOOK: Reducer<UpdateBook, State> = { state, action ->
    state.getBookStorage().require(action.book.id)

    noFollowUps(state.updateStorage(state.getBookStorage().update(action.book)))
}
