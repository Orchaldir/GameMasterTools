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
import at.orchaldir.gm.core.selector.economy.countEachJob
import at.orchaldir.gm.core.selector.item.countEachTextOrigin
import at.orchaldir.gm.core.selector.item.countEachLanguage
import at.orchaldir.gm.core.selector.util.countEachCreator
import at.orchaldir.gm.core.selector.world.countEachArchitecturalStyle
import at.orchaldir.gm.core.selector.world.countEachPurpose
import at.orchaldir.gm.core.selector.world.countEachStreetTemplate
import at.orchaldir.gm.core.selector.world.countEachTown
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// count

fun HtmlBlockTag.showArchitecturalStyleCount(
    call: ApplicationCall,
    state: State,
    buildings: Collection<Building>,
) = showCount(call, state, "Architectural Styles", countEachArchitecturalStyle(buildings))

fun HtmlBlockTag.showTextOriginTypeCount(texts: Collection<Text>) =
    showCount("Origin", countEachTextOrigin(texts))

fun <ELEMENT : Created> HtmlBlockTag.showCreatorCount(
    call: ApplicationCall,
    state: State,
    collection: Collection<ELEMENT>,
    label: String,
) {
    showMap(label, countEachCreator(collection)) { builder, count ->
        showCreator(call, state, builder)
        +": $count"
    }
}

fun HtmlBlockTag.showBuildingPurposeCount(buildings: Collection<Building>) =
    showCount("Building Purpose", countEachPurpose(buildings))

fun HtmlBlockTag.showCauseOfDeath(characters: Collection<Character>) =
    showCount("Cause Of Death", countEachCauseOfDeath(characters))

fun HtmlBlockTag.showCultureCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Cultures", countEachCulture(characters))

fun HtmlBlockTag.showGenderCount(characters: Collection<Character>) =
    showCount("Genders", countEachGender(characters))

fun HtmlBlockTag.showJobCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
    label: String = "Jobs",
) {
    showMap(label, countEachJob(characters)) { job, count ->
        if (job == null) {
            +"Unemployed"
        } else {
            link(call, state, job)
        }
        +": $count"
    }
}

fun HtmlBlockTag.showHousingStatusCount(characters: Collection<Character>) =
    showCount("Housing Status", countEachHousingStatus(characters))

fun HtmlBlockTag.showLanguageCountForTexts(
    call: ApplicationCall,
    state: State,
    texts: Collection<Text>,
) = showCount(call, state, "Languages", countEachLanguage(texts))

fun HtmlBlockTag.showLanguageCountForCharacters(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Languages", countEachLanguage(characters))

fun HtmlBlockTag.showMaterialCategoryCount(materials: Collection<Material>) =
    showCount("Material Category", countEachMaterialCategory(materials))

fun HtmlBlockTag.showBuildingOwnershipCount(call: ApplicationCall, state: State, collection: Collection<Building>) =
    showOwnerCount(call, state, collection.map { it.ownership })

fun HtmlBlockTag.showBusinessOwnershipCount(call: ApplicationCall, state: State, collection: Collection<Business>) =
    showOwnerCount(call, state, collection.map { it.ownership })

fun HtmlBlockTag.showOwnerCount(
    call: ApplicationCall,
    state: State,
    ownershipCollection: Collection<History<Owner>>,
) {
    showMap("Ownership", countEachOwner(ownershipCollection)) { owner, count ->
        showOwner(call, state, owner)
        +": $count"
    }
}

fun countEachOwner(ownershipCollection: Collection<History<Owner>>) = ownershipCollection
    .groupingBy { it.current }
    .eachCount()

fun HtmlBlockTag.showPersonalityCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
    label: String = "Personality",
) = showCount(call, state, label, countEachPersonality(characters))

fun HtmlBlockTag.showRaceCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Races", countEachRace(characters))

fun HtmlBlockTag.showStreetTemplateCount(
    call: ApplicationCall,
    state: State,
    town: TownId,
) = showCount(call, state, "Street Templates", state.countEachStreetTemplate(town))

fun HtmlBlockTag.showTownCount(
    call: ApplicationCall,
    state: State,
    buildings: Collection<Building>,
) = showCount(call, state, "Towns", countEachTown(buildings))

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