package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateText
import at.orchaldir.gm.core.action.DeleteText
import at.orchaldir.gm.core.action.UpdateText
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.reducer.util.checkCreator
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.item.canDeleteText
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TEXT: Reducer<CreateText, State> = { state, _ ->
    val text = Text(state.getTextStorage().nextId)

    noFollowUps(state.updateStorage(state.getTextStorage().add(text)))
}

val DELETE_TEXT: Reducer<DeleteText, State> = { state, action ->
    state.getTextStorage().require(action.id)
    require(state.canDeleteText(action.id)) { "The text ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getTextStorage().remove(action.id)))
}

val UPDATE_TEXT: Reducer<UpdateText, State> = { state, action ->
    state.getTextStorage().require(action.text.id)
    checkOrigin(state, action.text)
    checkTextFormat(action.text.format)

    noFollowUps(state.updateStorage(state.getTextStorage().update(action.text)))
}

private fun checkOrigin(
    state: State,
    text: Text,
) {
    when (val origin = text.origin) {
        is OriginalText -> checkCreator(state, origin.author, text.id, text.date, "Author")
        is TranslatedText -> {
            val original = state.getTextStorage().getOrThrow(origin.text)
            require(text.id != origin.text) { "The text cannot translate itself!" }
            require(state.getDefaultCalendar().isAfterOrEqualOptional(text.date, original.date)) {
                "The translation must happen after the original was written!"
            }
            checkCreator(state, origin.translator, text.id, text.date, "Translator")
        }
    }
}

private fun checkTextFormat(format: TextFormat) {
    when (format) {
        is Book -> {
            require(format.pages >= MIN_PAGES) { "The text requires at least $MIN_PAGES pages!" }

            when (format.binding) {
                is CopticBinding -> {
                    val stitches = when (val sewing = format.binding.sewingPattern) {
                        is ComplexSewingPattern -> sewing.stitches.size
                        is SimpleSewingPattern -> sewing.stitches.size
                    }
                    require(stitches >= MIN_STITCHES) { "The sewing pattern requires at least $MIN_STITCHES stitches!" }
                }

                is Hardcover -> doNothing()
                is LeatherBinding -> doNothing()
            }
        }

        is Scroll -> checkScrollFormat(format.format)
        UndefinedTextFormat -> doNothing()
    }
}

private fun checkScrollFormat(format: ScrollFormat) {
    when (format) {
        is ScrollWithOneRod -> checkScrollHandle(format.handle)
        is ScrollWithTwoRods -> checkScrollHandle(format.handle)
        ScrollWithoutRod -> doNothing()
    }
}

private fun checkScrollHandle(handle: ScrollHandle) {
    require(handle.segments.isNotEmpty()) { "A scroll handle needs at least 1 segment!" }
}
