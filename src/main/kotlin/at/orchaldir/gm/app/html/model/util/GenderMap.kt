package at.orchaldir.gm.app.html.model.util

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.showMap
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.util.GenderMap
import kotlinx.html.HtmlBlockTag
import kotlinx.html.P
import kotlinx.html.li
import kotlinx.html.ul

private fun createParam(param: String, gender: Gender) = "$param-$gender"

// show

fun <T> HtmlBlockTag.showGenderMap(
    label: String,
    map: GenderMap<T>,
    content: P.(T) -> Unit,
) {
    showDetails(label) {
        showGenderMap(map, content)
    }
}

fun <T> HtmlBlockTag.showGenderMap(
    map: GenderMap<T>,
    content: P.(T) -> Unit,
) {
    ul {
        map.getMap()
            .filterValues { it != null }
            .forEach { (gender, value) ->
                li {
                    field(gender.name) {
                        content(value)
                    }
                }
            }
    }
}

// edit

fun <T> HtmlBlockTag.selectGenderMap(
    label: String,
    map: GenderMap<T>,
    param: String,
    content: P.(String, T) -> Unit,
) {
    showDetails(label, true) {
        selectGenderMap(map, param, content)
    }
}

fun <T> HtmlBlockTag.selectGenderMap(
    map: GenderMap<T>,
    param: String,
    content: P.(String, T) -> Unit,
) {
    showMap(map.getMap()) { gender, value ->
        field(gender.toString()) {
            content(createParam(param, gender), value)
        }
    }
}


// parse

fun <T> parseGenderMap(
    param: String,
    parseGender: (String) -> T,
): GenderMap<T> {
    val female = parseGender(createParam(param, Gender.Female))
    val genderless = parseGender(createParam(param, Gender.Genderless))
    val male = parseGender(createParam(param, Gender.Male))

    return GenderMap(female, genderless, male)
}

