package at.orchaldir.gm.app.html.util.name

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseLanguageId
import at.orchaldir.gm.app.html.culture.parseOptionalFashionId
import at.orchaldir.gm.app.html.realm.population.showPopulationOfCulture
import at.orchaldir.gm.app.html.time.editHolidays
import at.orchaldir.gm.app.html.time.parseCalendarId
import at.orchaldir.gm.app.html.time.parseHolidays
import at.orchaldir.gm.app.html.time.showHolidays
import at.orchaldir.gm.app.html.util.name.parseNameListId
import at.orchaldir.gm.app.html.util.parseGenderMap
import at.orchaldir.gm.app.html.util.selectGenderMap
import at.orchaldir.gm.app.html.util.showCreated
import at.orchaldir.gm.app.html.util.showGenderMap
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.core.model.culture.name.GivenNamesType.*
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.building.BuildingPurpose
import at.orchaldir.gm.core.model.world.building.BuildingPurposeType
import at.orchaldir.gm.core.model.world.building.BusinessAndHome
import at.orchaldir.gm.core.model.world.building.SingleBusiness
import at.orchaldir.gm.core.model.world.building.SingleFamilyHouse
import at.orchaldir.gm.core.model.world.building.UndefinedBuildingPurpose
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.util.sortNameLists
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

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

    showDetails("Given Names") {
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
    when (parse(parameters, GIVEN_NAME, GivenNamesType.MaleAndFemale)) {
        NonGendered -> NonGenderedGivenNames(
            parseNameListId(parameters, combine(GIVEN_NAME, LIST)),
        )
        MaleAndFemale -> MaleAndFemaleGivenNames(
            parseNameListId(parameters, combine(GIVEN_NAME, Gender.Male)),
            parseNameListId(parameters, combine(GIVEN_NAME, Gender.Female)),
            parseOptionalNameListId(parameters, combine(GIVEN_NAME, LIST)),
        )
    }
