package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.content.FontInitials
import at.orchaldir.gm.core.model.item.text.content.Initials
import at.orchaldir.gm.core.model.item.text.content.LargeInitials
import at.orchaldir.gm.core.model.item.text.content.NormalInitials
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeParagraphWithInitial(
    state: TextRenderState,
    builder: PagesBuilder,
    options: RenderStringOptions,
    string: String,
    initials: Initials,
) {
    when (initials) {
        NormalInitials -> builder.addParagraph(string, options)
        is LargeInitials -> {
            val initialSize = options.size * initials.size.toNumber()
            builder.addParagraphWithInitial(
                string,
                options,
                options.copy(size = initialSize),
                initials.position,
            )
        }

        is FontInitials -> builder.addParagraphWithInitial(
            string,
            options,
            initials.fontOption.convert(state.state, VerticalAlignment.Top),
            initials.position,
        )
    }

    builder.addBreak(options.size)
}