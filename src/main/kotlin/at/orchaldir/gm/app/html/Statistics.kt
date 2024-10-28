package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.util.Ownership
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.selector.countCultures
import at.orchaldir.gm.core.selector.countGender
import at.orchaldir.gm.core.selector.countLivingStatus
import at.orchaldir.gm.core.selector.countRace
import at.orchaldir.gm.core.selector.world.countArchitecturalStyles
import at.orchaldir.gm.core.selector.world.countPurpose
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

fun HtmlBlockTag.showBuildingPurposeCount(buildings: Collection<Building>) =
    showCount("Building Purpose", countPurpose(buildings))

fun HtmlBlockTag.showCultureCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Cultures", countCultures(characters))

fun HtmlBlockTag.showGenderCount(characters: Collection<Character>) =
    showCount("Genders", countGender(characters))

fun HtmlBlockTag.showLivingStatusCount(characters: Collection<Character>) =
    showCount("Living Status", countLivingStatus(characters))

fun HtmlBlockTag.showBuildingOwnershipCount(call: ApplicationCall, state: State, collection: Collection<Building>) =
    showOwnershipCount(call, state, collection.map { it.ownership })

fun HtmlBlockTag.showOwnershipCount(call: ApplicationCall, state: State, ownershipCollection: Collection<Ownership>) {
    showMap("Ownership", countOwnership(ownershipCollection)) { owner, count ->
        showOwner(call, state, owner)
        +": $count"
    }
}

fun countOwnership(ownershipCollection: Collection<Ownership>) = ownershipCollection
    .groupingBy { it.owner }
    .eachCount()

fun HtmlBlockTag.showRaceCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Races", countRace(characters))

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