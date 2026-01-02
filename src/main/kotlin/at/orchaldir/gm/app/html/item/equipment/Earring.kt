package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.equipment.style.editOrnament
import at.orchaldir.gm.app.html.item.equipment.style.parseOrnament
import at.orchaldir.gm.app.html.item.equipment.style.showOrnament
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
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
            field("Thickness", style.thickness)
            showColorSchemeItemPart(call, state, style.wire, "Wire")
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
    showColorSchemeItemPart(call, state, style.wire, "Wire")
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
    showColorSchemeItemPart(call, state, style.wire, "Wire")
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
            selectValue("Thickness", SIZE, Size.entries, style.thickness)
            editColorSchemeItemPart(state, style.wire, WIRE, "Wire")
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
    editColorSchemeItemPart(state, style.wire, WIRE, "Wire")
}

fun HtmlBlockTag.editDropEarring(
    state: State,
    style: DropEarring,
) {
    selectDropSize("Top Size", style.topSize, TOP)
    selectDropSize("Bottom Size", style.bottomSize, BOTTOM)
    selectFactor("Wire Length", LENGTH, style.wireLength, ZERO, ONE, ONE_PERCENT)
    editOrnament(state, style.top, TOP, "Top Ornament")
    editOrnament(state, style.bottom, BOTTOM, "Bottom Ornament")
    editColorSchemeItemPart(state, style.wire, WIRE, "Wire")
}

private fun HtmlBlockTag.selectDropSize(label: String, size: Factor, param: String) {
    selectFactor(label, combine(param, SIZE), size, ZERO, ONE, ONE_PERCENT)
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
                parseColorSchemeItemPart(parameters, WIRE),
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
    parseList(parameters, SIZE, 1) { _, param ->
        parse(parameters, param, Size.Medium)
    },
    parseColorSchemeItemPart(parameters, WIRE),
)

fun parseDropEarring(parameters: Parameters) = DropEarring(
    parseFactor(parameters, combine(TOP, SIZE)),
    parseFactor(parameters, combine(BOTTOM, SIZE)),
    parseFactor(parameters, LENGTH),
    parseOrnament(parameters, TOP),
    parseOrnament(parameters, BOTTOM),
    parseColorSchemeItemPart(parameters, WIRE),
)

