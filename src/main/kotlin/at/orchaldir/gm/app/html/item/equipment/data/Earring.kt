package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEarring(
    call: ApplicationCall,
    state: State,
    earring: Earring,
) {
    field("Style", earring.style.getType())

    when (val style = earring.style) {
        is DangleEarring -> showDangleEarring(call, state, style)
        is DropEarring -> showDropEarring(call, state, style)
        is HoopEarring -> {
            fieldFactor("Diameter", style.length)
            showLineStyle(call, state, style.wire, "Wire")
        }

        is StudEarring -> {
            showOrnament(call, state, style.ornament)
            field("Size", style.size)
        }
    }
}

fun HtmlBlockTag.showDangleEarring(
    call: ApplicationCall,
    state: State,
    style: DangleEarring,
) {
    showOrnament(call, state, style.top, "Top Ornament")
    showOrnament(call, state, style.bottom, "Bottom Ornament")
    fieldList("Sizes", style.sizes) {
        +it.name
    }
    showLineStyle(call, state, style.line, "Wire")
}

fun HtmlBlockTag.showDropEarring(
    call: ApplicationCall,
    state: State,
    style: DropEarring,
) {
    fieldFactor("Top Size", style.topSize)
    fieldFactor("Bottom Size", style.bottomSize)
    fieldFactor("Wire Length", style.lineLength)
    showOrnament(call, state, style.top, "Top Ornament")
    showOrnament(call, state, style.bottom, "Bottom Ornament")
    showLineStyle(call, state, style.line, "Wire")
}

// edit

fun HtmlBlockTag.editEarring(
    state: State,
    earring: Earring,
) {
    selectValue("Style", STYLE, EarringStyleType.entries, earring.style.getType())

    when (val style = earring.style) {
        is DangleEarring -> editDangleEarring(state, style)
        is DropEarring -> editDropEarring(state, style)
        is HoopEarring -> {
            selectFactor("Diameter", LENGTH, style.length, ZERO, ONE, ONE_PERCENT)
            editLineStyle(state, style.wire, "Wire", WIRE, setOf(LineStyleType.Wire))
        }

        is StudEarring -> {
            editOrnament(state, style.ornament)
            selectValue("Size", SIZE, Size.entries, style.size)
        }
    }
}

fun HtmlBlockTag.editDangleEarring(
    state: State,
    style: DangleEarring,
) {
    editOrnament(state, style.top, TOP, "Top Ornament")
    editOrnament(state, style.bottom, BOTTOM, "Bottom Ornament")
    editList("Sizes", SIZE, style.sizes, 1, 10, 1) { index, param, size ->
        selectValue("$index.Size", param, Size.entries, size)
    }
    editLineStyle(state, style.line, "Wire", WIRE, WITHOUT_ORNAMENT_LINE)
}

fun HtmlBlockTag.editDropEarring(
    state: State,
    style: DropEarring,
) {
    selectDropSize("Top Size", style.topSize, TOP)
    selectDropSize("Bottom Size", style.bottomSize, BOTTOM)
    selectFactor("Wire Length", LENGTH, style.lineLength, ZERO, ONE, ONE_PERCENT)
    editOrnament(state, style.top, TOP, "Top Ornament")
    editOrnament(state, style.bottom, BOTTOM, "Bottom Ornament")
    editLineStyle(state, style.line, "Wire", WIRE, WITHOUT_ORNAMENT_LINE)
}

private fun HtmlBlockTag.selectDropSize(label: String, size: Factor, param: String) {
    selectFactor(label, combine(param, SIZE), size, ZERO, ONE, ONE_PERCENT)
}


// parse

fun parseEarring(
    state: State,
    parameters: Parameters,
): Earring {
    val type = parse(parameters, STYLE, EarringStyleType.Stud)

    return Earring(
        when (type) {
            EarringStyleType.Dangle -> parseDangleEarring(state, parameters)
            EarringStyleType.Drop -> parseDropEarring(state, parameters)
            EarringStyleType.Hoop -> HoopEarring(
                parseFactor(parameters, LENGTH),
                parseWire(state, parameters, WIRE),
            )

            EarringStyleType.Stud -> StudEarring(
                parseOrnament(state, parameters),
                parse(parameters, SIZE, Size.Medium),
            )
        }
    )
}

fun parseDangleEarring(
    state: State,
    parameters: Parameters,
) = DangleEarring(
    parseOrnament(state, parameters, TOP),
    parseOrnament(state, parameters, BOTTOM),
    parseList(parameters, SIZE, 1) { _, param ->
        parse(parameters, param, Size.Medium)
    },
    parseLineStyle(state, parameters, WIRE),
)

fun parseDropEarring(
    state: State,
    parameters: Parameters,
) = DropEarring(
    parseFactor(parameters, combine(TOP, SIZE)),
    parseFactor(parameters, combine(BOTTOM, SIZE)),
    parseFactor(parameters, LENGTH),
    parseOrnament(state, parameters, TOP),
    parseOrnament(state, parameters, BOTTOM),
    parseLineStyle(state, parameters, WIRE),
)

