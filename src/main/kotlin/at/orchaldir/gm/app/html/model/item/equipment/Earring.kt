package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.fieldFactor
import at.orchaldir.gm.app.html.model.item.editColorItemPart
import at.orchaldir.gm.app.html.model.item.parseColorItemPart
import at.orchaldir.gm.app.html.model.item.showColorItemPart
import at.orchaldir.gm.app.html.model.parseFactor
import at.orchaldir.gm.app.html.model.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
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
import kotlinx.html.FORM
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
            field("Thickness", style.thickness)
            showColorItemPart(call, state, style.wire, "Wire")
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
    showColorItemPart(call, state, style.wire, "Wire")
}

fun HtmlBlockTag.showDropEarring(
    call: ApplicationCall,
    state: State,
    style: DropEarring,
) {
    fieldFactor("Top Size", style.topSize)
    fieldFactor("Bottom Size", style.bottomSize)
    fieldFactor("Wire Length", style.wireLength)
    showOrnament(call, state, style.top, "Top Ornament")
    showOrnament(call, state, style.bottom, "Bottom Ornament")
    showColorItemPart(call, state, style.wire, "Wire")
}

// edit

fun FORM.editEarring(
    state: State,
    earring: Earring,
) {
    selectValue("Style", STYLE, EarringStyleType.entries, earring.style.getType(), true)

    when (val style = earring.style) {
        is DangleEarring -> editDangleEarring(state, style)
        is DropEarring -> editDropEarring(state, style)
        is HoopEarring -> {
            selectFactor("Diameter", LENGTH, style.length, ZERO, ONE, ONE_PERCENT, true)
            selectValue("Thickness", SIZE, Size.entries, style.thickness, true)
            editColorItemPart(state, style.wire, WIRE, "Wire")
        }

        is StudEarring -> {
            editOrnament(state, style.ornament)
            selectValue("Size", SIZE, Size.entries, style.size, true)
        }
    }
}

fun FORM.editDangleEarring(
    state: State,
    style: DangleEarring,
) {
    editOrnament(state, style.top, TOP, "Top Ornament")
    editOrnament(state, style.bottom, BOTTOM, "Bottom Ornament")
    editList("Sizes", SIZE, style.sizes, 1, 10, 1) { index, param, size ->
        selectValue("$index.Size", param, Size.entries, size, true)
    }
    editColorItemPart(state, style.wire, WIRE, "Wire")
}

fun FORM.editDropEarring(
    state: State,
    style: DropEarring,
) {
    selectDropSize("Top Size", style.topSize, TOP)
    selectDropSize("Bottom Size", style.bottomSize, BOTTOM)
    selectFactor("Wire Length", LENGTH, style.wireLength, ZERO, ONE, ONE_PERCENT, true)
    editOrnament(state, style.top, TOP, "Top Ornament")
    editOrnament(state, style.bottom, BOTTOM, "Bottom Ornament")
    editColorItemPart(state, style.wire, WIRE, "Wire")
}

private fun FORM.selectDropSize(label: String, size: Factor, param: String) {
    selectFactor(label, combine(param, SIZE), size, ZERO, ONE, ONE_PERCENT, true)
}


// parse

fun parseEarring(parameters: Parameters): Earring {
    val type = parse(parameters, STYLE, EarringStyleType.Stud)

    return Earring(
        when (type) {
            EarringStyleType.Dangle -> parseDangleEarring(parameters)
            EarringStyleType.Drop -> parseDropEarring(parameters)
            EarringStyleType.Hoop -> HoopEarring(
                parseFactor(parameters, LENGTH),
                parse(parameters, SIZE, Size.Medium),
                parseColorItemPart(parameters, WIRE),
            )

            EarringStyleType.Stud -> StudEarring(
                parseOrnament(parameters),
                parse(parameters, SIZE, Size.Medium),
            )
        }
    )
}

fun parseDangleEarring(parameters: Parameters) = DangleEarring(
    parseOrnament(parameters, TOP),
    parseOrnament(parameters, BOTTOM),
    parseList(parameters, SIZE, 1) { param ->
        parse(parameters, param, Size.Medium)
    },
    parseColorItemPart(parameters, WIRE),
)

fun parseDropEarring(parameters: Parameters) = DropEarring(
    parseFactor(parameters, combine(TOP, SIZE)),
    parseFactor(parameters, combine(BOTTOM, SIZE)),
    parseFactor(parameters, LENGTH),
    parseOrnament(parameters, TOP),
    parseOrnament(parameters, BOTTOM),
    parseColorItemPart(parameters, WIRE),
)

