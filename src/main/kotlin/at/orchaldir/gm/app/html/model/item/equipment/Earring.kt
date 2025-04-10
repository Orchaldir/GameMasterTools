package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.fieldFactor
import at.orchaldir.gm.app.html.model.parseFactor
import at.orchaldir.gm.app.html.model.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showEarring(
    call: ApplicationCall,
    state: State,
    earring: Earring,
) {
    field("Style", earring.style.getType())

    when (val style = earring.style) {
        is DangleEarring -> {
            showOrnament(call, state, style.top, "Top Ornament")
            showOrnament(call, state, style.bottom, "Bottom Ornament")
            showList("Sizes", style.sizes) {
                +it.name
            }
            showLook(call, state, style.wireColor, style.wireMaterial, "Wire")
        }

        is DropEarring -> {
            fieldFactor("Top Size", style.topSize)
            fieldFactor("Bottom Size", style.bottomSize)
            fieldFactor("Wire Length", style.wireLength)
            showOrnament(call, state, style.top, "Top Ornament")
            showOrnament(call, state, style.bottom, "Bottom Ornament")
            showLook(call, state, style.wireColor, style.wireMaterial, "Wire")
        }

        is HoopEarring -> {
            fieldFactor("Diameter", style.length)
            field("Thickness", style.thickness)
            showLook(call, state, style.color, style.material)
        }

        is StudEarring -> {
            showOrnament(call, state, style.ornament)
            field("Size", style.size)
        }
    }
}

// edit

fun FORM.editEarring(
    state: State,
    earring: Earring,
) {
    selectValue("Style", STYLE, EarringStyleType.entries, earring.style.getType(), true)

    when (val style = earring.style) {
        is DangleEarring -> {
            editOrnament(state, style.top, TOP, "Top Ornament")
            editOrnament(state, style.bottom, BOTTOM, "Bottom Ornament")
            editList("Sizes", SIZE, style.sizes, 1, 10, 1) { index, param, size ->
                selectValue("$index.Size", param, Size.entries, size, true)
            }
            editLook(state, style.wireColor, style.wireMaterial, WIRE, "Wire")
        }

        is DropEarring -> {
            selectDropSize("Top Size", style.topSize, TOP)
            selectDropSize("Bottom Size", style.bottomSize, BOTTOM)
            selectFactor("Wire Length", LENGTH, style.wireLength, ZERO, ONE, ONE_PERCENT, true)
            editOrnament(state, style.top, TOP, "Top Ornament")
            editOrnament(state, style.bottom, BOTTOM, "Bottom Ornament")
            editLook(state, style.wireColor, style.wireMaterial, WIRE, "Wire")
        }

        is HoopEarring -> {
            selectFactor("Diameter", LENGTH, style.length, ZERO, ONE, ONE_PERCENT, true)
            selectValue("Thickness", SIZE, Size.entries, style.thickness, true)
            editLook(state, style.color, style.material, WIRE)
        }

        is StudEarring -> {
            editOrnament(state, style.ornament)
            selectValue("Size", SIZE, Size.entries, style.size, true)
        }
    }
}

private fun FORM.selectDropSize(label: String, size: Factor, param: String) {
    selectFactor(label, combine(param, SIZE), size, ZERO, ONE, ONE_PERCENT, true)
}


// parse

fun parseEarring(parameters: Parameters): Earring {
    val type = parse(parameters, STYLE, EarringStyleType.Stud)

    return Earring(
        when (type) {
            EarringStyleType.Dangle -> DangleEarring(
                parseOrnament(parameters, TOP),
                parseOrnament(parameters, BOTTOM),
                parseList(parameters, SIZE, 1) { param ->
                    parse(parameters, param, Size.Medium)
                },
                parse(parameters, combine(WIRE, COLOR), Color.Gold),
                parseMaterialId(parameters, combine(WIRE, MATERIAL)),
            )

            EarringStyleType.Drop -> DropEarring(
                parseFactor(parameters, combine(TOP, SIZE)),
                parseFactor(parameters, combine(BOTTOM, SIZE)),
                parseFactor(parameters, LENGTH),
                parseOrnament(parameters, TOP),
                parseOrnament(parameters, BOTTOM),
                parse(parameters, combine(WIRE, COLOR), Color.Gold),
                parseMaterialId(parameters, combine(WIRE, MATERIAL)),
            )

            EarringStyleType.Hoop -> HoopEarring(
                parseFactor(parameters, LENGTH),
                parse(parameters, SIZE, Size.Medium),
                parse(parameters, combine(WIRE, COLOR), Color.Gold),
                parseMaterialId(parameters, combine(WIRE, MATERIAL)),
            )

            EarringStyleType.Stud -> StudEarring(
                parseOrnament(parameters),
                parse(parameters, SIZE, Size.Medium),
            )
        }
    )
}

