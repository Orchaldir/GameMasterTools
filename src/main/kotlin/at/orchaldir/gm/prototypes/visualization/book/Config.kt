package at.orchaldir.gm.prototypes.visualization.book

import at.orchaldir.gm.core.model.item.book.LeatherBindingType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.book.BookRenderConfig
import at.orchaldir.gm.visualization.book.LeatherBindingConfig

val BOOK_CONFIG = BookRenderConfig(
    Distance(100),
    LineOptions(Color.Black.toRender(), Distance(1)),
    mapOf(
        LeatherBindingType.ThreeQuarter to LeatherBindingConfig(Factor(0.4f), Factor(0.4f)),
        LeatherBindingType.Half to LeatherBindingConfig(Factor(0.3f), Factor(0.3f)),
        LeatherBindingType.Quarter to LeatherBindingConfig(Factor(0.2f), Factor(0.2f)),
    )
)