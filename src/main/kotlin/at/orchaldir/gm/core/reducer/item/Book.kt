package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateBook
import at.orchaldir.gm.core.action.DeleteBook
import at.orchaldir.gm.core.action.UpdateBook
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.reducer.util.checkCreator
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.item.canDeleteBook
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BOOK: Reducer<CreateBook, State> = { state, _ ->
    val text = Text(state.getBookStorage().nextId)

    noFollowUps(state.updateStorage(state.getBookStorage().add(text)))
}

val DELETE_BOOK: Reducer<DeleteBook, State> = { state, action ->
    state.getBookStorage().require(action.id)
    require(state.canDeleteBook(action.id)) { "Book ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getBookStorage().remove(action.id)))
}

val UPDATE_BOOK: Reducer<UpdateBook, State> = { state, action ->
    state.getBookStorage().require(action.text.id)
    checkOrigin(state, action.text)
    checkFormat(action.text.format)

    noFollowUps(state.updateStorage(state.getBookStorage().update(action.text)))
}

private fun checkOrigin(
    state: State,
    text: Text,
) {
    when (val origin = text.origin) {
        is OriginalBook -> checkCreator(state, origin.author, text.id, text.date, "Author")
        is TranslatedBook -> {
            val original = state.getBookStorage().getOrThrow(origin.book)
            require(text.id != origin.book) { "Book cannot translate itself!" }
            require(state.getDefaultCalendar().isAfterOrEqualOptional(text.date, original.date)) {
                "The translation must happen after the original was written!"
            }
            checkCreator(state, origin.translator, text.id, text.date, "Translator")
        }
    }
}

private fun checkFormat(format: BookFormat) {
    when (format) {
        is Codex -> {
            require(format.pages >= MIN_PAGES) { "Book requires at least $MIN_PAGES pages!" }

            when (format.binding) {
                is CopticBinding -> {
                    val stitches = when (val sewing = format.binding.sewingPattern) {
                        is ComplexSewingPattern -> sewing.stitches.size
                        is SimpleSewingPattern -> sewing.stitches.size
                    }
                    require(stitches >= MIN_STITCHES) { "Sewing pattern requires at least $MIN_STITCHES stitches!" }
                }

                is Hardcover -> doNothing()
                is LeatherBinding -> doNothing()
            }
        }

        UndefinedBookFormat -> doNothing()
    }
}
