package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.content.FontInitials
import at.orchaldir.gm.core.model.item.text.content.Initials
import at.orchaldir.gm.core.model.item.text.content.LargeInitials
import at.orchaldir.gm.core.model.item.text.content.NormalInitials
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.visualization.text.TextRenderState

fun calculateInitialsOptions(
    state: TextRenderState,
    options: RenderStringOptions,
    initials: Initials,
) = when (initials) {
    NormalInitials -> options
    is LargeInitials -> options.copy(size = options.size * initials.size)
    is FontInitials -> initials.fontOption.convert(state.state, VerticalAlignment.Top)
}

fun visualizeParagraphWithInitial(
    builder: PagesBuilder,
    mainOptions: RenderStringOptions,
    initialOptions: RenderStringOptions,
    string: String,
    initials: Initials,
) {
    when (initials) {
        NormalInitials -> builder.addParagraph(string, mainOptions)
        is LargeInitials -> builder.addParagraphWithInitial(
            string,
            mainOptions,
            initialOptions,
            initials.position,
        )
        is FontInitials -> builder.addParagraphWithInitial(
            string,
            mainOptions,
            initialOptions,
            initials.position,
        )
    }

    builder.addBreak(mainOptions.size)
}