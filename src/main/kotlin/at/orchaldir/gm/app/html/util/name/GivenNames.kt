package at.orchaldir.gm.app.html.util.name

import at.orchaldir.gm.app.GIVEN_NAME
import at.orchaldir.gm.app.LIST
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.culture.name.GivenNames
import at.orchaldir.gm.core.model.culture.name.GivenNamesType
import at.orchaldir.gm.core.model.culture.name.GivenNamesType.MaleAndFemale
import at.orchaldir.gm.core.model.culture.name.GivenNamesType.NonGendered
import at.orchaldir.gm.core.model.culture.name.MaleAndFemaleGivenNames
import at.orchaldir.gm.core.model.culture.name.NonGenderedGivenNames
import at.orchaldir.gm.core.selector.util.sortNameLists
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGivenNames(
    call: ApplicationCall,
    state: State,
    names: GivenNames,
) {
    showDetails("Given Names") {
        field("Type", names.getType())

        when (names) {
            is NonGenderedGivenNames -> fieldLink("List", call, state, names.list)
            is MaleAndFemaleGivenNames -> {
                fieldLink("Male Names", call, state, names.male)
                fieldLink("Female Names", call, state, names.female)
                optionalFieldLink("Unisex Names", call, state, names.unisex)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editGivenNames(
    state: State,
    names: GivenNames,
) {
    val nameLists = state.sortNameLists()

    showDetails("Given Names", true) {
        selectValue("Type", GIVEN_NAME, GivenNamesType.entries, names.getType())

        when (names) {
            is NonGenderedGivenNames -> selectElement(
                state,
                "List",
                combine(GIVEN_NAME, LIST),
                nameLists,
                names.list,
            )

            is MaleAndFemaleGivenNames -> {
                selectElement(
                    state,
                    "Male Names",
                    combine(GIVEN_NAME, Gender.Male),
                    nameLists,
                    names.male,
                )
                selectElement(
                    state,
                    "Female Names",
                    combine(GIVEN_NAME, Gender.Female),
                    nameLists,
                    names.female,
                )
                selectOptionalElement(
                    state,
                    "Unisex Names",
                    combine(GIVEN_NAME, LIST),
                    nameLists,
                    names.unisex,
                )
            }
        }
    }
}

// parse

fun parseGivenNames(parameters: Parameters): GivenNames =
    when (parse(parameters, GIVEN_NAME, MaleAndFemale)) {
        NonGendered -> NonGenderedGivenNames(
            parseNameListId(parameters, combine(GIVEN_NAME, LIST)),
        )

        MaleAndFemale -> MaleAndFemaleGivenNames(
            parseNameListId(parameters, combine(GIVEN_NAME, Gender.Male)),
            parseNameListId(parameters, combine(GIVEN_NAME, Gender.Female)),
            parseOptionalNameListId(parameters, combine(GIVEN_NAME, LIST)),
        )
    }
