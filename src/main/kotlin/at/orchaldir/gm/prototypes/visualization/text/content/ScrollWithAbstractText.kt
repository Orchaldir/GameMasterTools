package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.content.AbstractContent
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.item.text.scroll.HandleSegment
import at.orchaldir.gm.core.model.item.text.scroll.ScrollHandle
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.visualization.text.content.visualizeAllPagesOfScroll
import java.io.File

private val ID = TextId(0)

fun main() {
    val segment = HandleSegment(fromCentimeters(5), fromCentimeters(2), Color.SaddleBrown)
    val handle = ScrollHandle(segment)
    val scroll = Scroll(
        ScrollWithTwoRods(handle),
        main = ColorItemPart(Color.AntiqueWhite),
    )
    val content = AbstractText(
        AbstractContent(5),
    )
    val text = Text(
        ID,
        format = scroll,
        content = content,
    )

    val svg = visualizeAllPagesOfScroll(State(), TEXT_CONFIG, text, scroll)

    File("scroll-abstract-text.svg").writeText(svg.export())
}