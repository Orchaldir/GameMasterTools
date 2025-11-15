package at.orchaldir.gm.app.html.math

import at.orchaldir.gm.app.PROFILE
import at.orchaldir.gm.app.X
import at.orchaldir.gm.app.Y
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.parseBool
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectBool
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.showListWithIndex
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.utils.math.shape.*
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRotatedShape(
    shape: RotatedShape,
    label: String = "Shape",
) {
    showDetails(label, true) {
        fieldList("Profile", shape.profile) { (y, x) ->
            +"y:$y% x:$x%"
        }
        field("Rounded", shape.rounded)
    }
}

// edit

fun HtmlBlockTag.editRotatedShape(
    shape: RotatedShape,
    param: String,
    label: String = "Shape",
) {
    val profileParam = combine(param, PROFILE)
    var minY = 0

    showDetails(label, true) {
        selectInt("Points", shape.profile.size, 1, 100, 1, profileParam)

        showListWithIndex(shape.profile) { index, (y,x) ->
            val pointParam = combine(profileParam, index)
            selectInt(
                "Y",
                y,
                minY,
                100,
                1,
                combine(pointParam, Y),
            )
            selectInt(
                "X",
                x,
                0,
                100,
                1,
                combine(pointParam, X),
            )

            minY = y + 1
        }

        selectBool("Rounded", shape.rounded, param)
    }
}


// parse

fun parseRotatedShape(parameters: Parameters, param: String) = RotatedShape(
    parseProfile(parameters, param),
    parseBool(parameters, param),
)

private fun parseProfile(
    parameters: Parameters,
    param: String,
): List<Pair<Int,Int>> {
    val profileParam = combine(param, PROFILE)
    val count = parseInt(parameters, profileParam, 1)
    var minY = 0

    return (0..<count)
        .map {
            val x = parseInt(parameters, combine(profileParam, X), 50)
            val y = parseInt(parameters, combine(profileParam, Y), minY)
            minY = y + 1

            Pair(y,x)
        }
}
