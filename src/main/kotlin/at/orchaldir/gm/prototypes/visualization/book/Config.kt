package at.orchaldir.gm.prototypes.visualization.book

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.book.BookRenderConfig

val BOOK_CONFIG = BookRenderConfig(
    Distance(100),
    LineOptions(Color.Black.toRender(), Distance(1)),
)