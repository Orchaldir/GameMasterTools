package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.html.model.showCreator
import at.orchaldir.gm.app.html.model.showOwner
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.core.selector.economy.countJobs
import at.orchaldir.gm.core.selector.item.countBookOriginTypes
import at.orchaldir.gm.core.selector.item.countLanguages
import at.orchaldir.gm.core.selector.util.countCreators
import at.orchaldir.gm.core.selector.world.countArchitecturalStyles
import at.orchaldir.gm.core.selector.world.countPurpose
import at.orchaldir.gm.core.selector.world.countStreetTemplates
import at.orchaldir.gm.core.selector.world.countTowns
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// count

fun HtmlBlockTag.showArchitecturalStyleCount(
    call: ApplicationCall,
    state: State,
    buildings: Collection<Building>,
) = showCount(call, state, "Architectural Styles", countArchitecturalStyles(buildings))

fun HtmlBlockTag.showBookOriginTypeCount(texts: Collection<Text>) =
    showCount("Origin", countBookOriginTypes(texts))

fun <ELEMENT : Created> HtmlBlockTag.showCreatorCount(
    call: ApplicationCall,
    state: State,
    collection: Collection<ELEMENT>,
    label: String,
) {
    showMap(label, countCreators(collection)) { builder, count ->
        showCreator(call, state, builder)
        +": $count"
    }
}

fun HtmlBlockTag.showBuildingPurposeCount(buildings: Collection<Building>) =
    showCount("Building Purpose", countPurpose(buildings))

fun HtmlBlockTag.showCauseOfDeath(characters: Collection<Character>) =
    showCount("Cause Of Death", countCauseOfDeath(characters))

fun HtmlBlockTag.showCultureCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Cultures", countCultures(characters))

fun HtmlBlockTag.showGenderCount(characters: Collection<Character>) =
    showCount("Genders", countGender(characters))

fun HtmlBlockTag.showJobCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
    label: String = "Jobs",
) {
    showMap(label, countJobs(characters)) { job, count ->
        if (job == null) {
            +"Unemployed"
        } else {
            link(call, state, job)
        }
        +": $count"
    }
}

fun HtmlBlockTag.showHousingStatusCount(characters: Collection<Character>) =
    showCount("Housing Status", countHousingStatus(characters))

fun HtmlBlockTag.showLanguageCountForBooks(
    call: ApplicationCall,
    state: State,
    texts: Collection<Text>,
) = showCount(call, state, "Languages", countLanguages(texts))

fun HtmlBlockTag.showLanguageCountForCharacters(
    call: ApplicationCall,
    state: State,
    books: Collection<Character>,
) = showCount(call, state, "Languages", countLanguages(books))

fun HtmlBlockTag.showMaterialCategoryCount(materials: Collection<Material>) =
    showCount("Material Category", countMaterialCategory(materials))

fun HtmlBlockTag.showBuildingOwnershipCount(call: ApplicationCall, state: State, collection: Collection<Building>) =
    showOwnershipCount(call, state, collection.map { it.ownership })

fun HtmlBlockTag.showBusinessOwnershipCount(call: ApplicationCall, state: State, collection: Collection<Business>) =
    showOwnershipCount(call, state, collection.map { it.ownership })

fun HtmlBlockTag.showOwnershipCount(
    call: ApplicationCall,
    state: State,
    ownershipCollection: Collection<History<Owner>>,
) {
    showMap("Ownership", countOwnership(ownershipCollection)) { owner, count ->
        showOwner(call, state, owner)
        +": $count"
    }
}

fun countOwnership(ownershipCollection: Collection<History<Owner>>) = ownershipCollection
    .groupingBy { it.current }
    .eachCount()

fun HtmlBlockTag.showPersonalityCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
    label: String = "Personality",
) = showCount(call, state, label, countPersonality(characters))

fun HtmlBlockTag.showRaceCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Races", countRace(characters))

fun HtmlBlockTag.showStreetTemplateCount(
    call: ApplicationCall,
    state: State,
    town: TownId,
) = showCount(call, state, "Street Templates", state.countStreetTemplates(town))

fun HtmlBlockTag.showTownCount(
    call: ApplicationCall,
    state: State,
    buildings: Collection<Building>,
) = showCount(call, state, "Towns", countTowns(buildings))

fun <ID : Id<ID>> HtmlBlockTag.showCount(
    call: ApplicationCall,
    state: State,
    label: String,
    map: Map<ID, Int>,
) {
    showMap(label, map) { id, count ->
        link(call, state, id)
        +": $count"
    }
}

fun <T> HtmlBlockTag.showCount(
    label: String,
    map: Map<T, Int>,
) {
    showMap(label, map) { value, count ->
        +"$value: $count"
    }
}