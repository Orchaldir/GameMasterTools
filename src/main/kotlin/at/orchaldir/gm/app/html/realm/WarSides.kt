package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.SIDE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarSide
import at.orchaldir.gm.core.model.util.render.Color
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showWarSides(
    war: War,
) {
    fieldListWithIndex("Sides", war.sides) { index, side ->
        fieldColor(side.color, side.name?.text ?: "${index + 1}.Side")
    }
}

// edit

fun HtmlBlockTag.editWarSides(
    war: War,
) {
    var colors = Color.entries.toList()

    editList("Sides", SIDE, war.sides, 0, 10) { index, param, side ->
        selectOptionalName("${index + 1}.Side", side.name, combine(param, NAME))
        selectColor(side.color, combine(param, COLOR), values = colors)
        colors -= side.color
    }
}

// parse

fun parseWarSides(parameters: Parameters) = parseList(parameters, SIDE, 0) { index, param ->
    WarSide(
        parse(parameters, combine(param, COLOR), Color.Pink),
        parseOptionalName(parameters, combine(param, NAME)),
    )
}
