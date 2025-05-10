package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.item.text.scroll.HandleSegment
import at.orchaldir.gm.core.model.item.text.scroll.ScrollHandle
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.visualization.text.content.visualizeScrollContent
import java.io.File

private val ID = TextId(0)

fun main() {
    val font = SolidFont(Distance.fromMillimeters(10), Color.Blue)
    val segment = HandleSegment(fromMillimeters(200), fromMillimeters(40), Color.SaddleBrown)
    val handle = ScrollHandle(segment)
    val scroll = Scroll(
        ScrollWithTwoRods(handle),
    )
    val chapter0 = AbstractChapter(0, AbstractContent(2))
    val chapter1 = AbstractChapter(1, AbstractContent(3))
    val chapters = AbstractChapters(
        listOf(chapter0, chapter1),
        pageNumbering = PageNumberingReusingFont(),
        tableOfContents = ComplexTableOfContents(titleOptions = font),
    )
    val text = Text(
        ID,
        format = scroll,
        content = chapters,
    )

    val svg = visualizeScrollContent(State(), TEXT_CONFIG, text, scroll)

    File("scroll-abstract-chapters.svg").writeText(svg.export())
}