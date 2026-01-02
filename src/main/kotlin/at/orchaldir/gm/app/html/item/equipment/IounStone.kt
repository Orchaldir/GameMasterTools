package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.IounStone
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.shape.SHAPES_WITHOUT_CROSS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showIounStone(
    call: ApplicationCall,
    state: State,
    stone: IounStone,
) {
    showComplexShape(stone.shape)
    field("Size", stone.size)
    showColorSchemeItemPart(call, state, stone.main, "Main")
}

// edit

fun HtmlBlockTag.editIounStone(
    state: State,
    stone: IounStone,
) {
    selectComplexShape(stone.shape, SHAPE, SHAPES_WITHOUT_CROSS)
    selectValue("Size", SIZE, Size.entries, stone.size)
    editColorSchemeItemPart(state, stone.main, MAIN, "Main")
}

// parse

fun parseIounStone(parameters: Parameters) = IounStone(
    parseComplexShape(parameters, SHAPE),
    parse(parameters, SIZE, Size.Medium),
    parseColorSchemeItemPart(parameters, MAIN),
)