package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.character.*
import at.orchaldir.gm.core.selector.economy.countEachJob
import at.orchaldir.gm.core.selector.economy.countEachMaterialCategory
import at.orchaldir.gm.core.selector.item.countEachLanguage
import at.orchaldir.gm.core.selector.item.countEachTextFormat
import at.orchaldir.gm.core.selector.item.countEachTextOrigin
import at.orchaldir.gm.core.selector.item.periodical.countPublicationFrequencies
import at.orchaldir.gm.core.selector.magic.countEachLanguage
import at.orchaldir.gm.core.selector.magic.countSpellOrigin
import at.orchaldir.gm.core.selector.race.countEachRace
import at.orchaldir.gm.core.selector.religion.countEachDomain
import at.orchaldir.gm.core.selector.rpg.countEachCharacterTraitForCharacters
import at.orchaldir.gm.core.selector.rpg.countEachCharacterTraitForGods
import at.orchaldir.gm.core.selector.util.countEachCreator
import at.orchaldir.gm.core.selector.world.countEachArchitecturalStyle
import at.orchaldir.gm.core.selector.world.countEachPurpose
import at.orchaldir.gm.core.selector.world.countEachStreetTemplate
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// count

fun HtmlBlockTag.showArchitecturalStyleCount(
    call: ApplicationCall,
    state: State,
    buildings: Collection<Building>,
) = showCount(call, state, "Architectural Styles", countEachArchitecturalStyle(buildings))

fun HtmlBlockTag.showTextFormatCount(texts: Collection<Text>) =
    showCount("Format", countEachTextFormat(texts))

fun HtmlBlockTag.showTextOriginCount(texts: Collection<Text>) =
    showCount("Origin", countEachTextOrigin(texts))

fun <ELEMENT : Creation> HtmlBlockTag.showCreatorCount(
    call: ApplicationCall,
    state: State,
    collection: Collection<ELEMENT>,
    label: String,
) {
    showMap(label, countEachCreator(collection)) { creator, count ->
        showReference(call, state, creator)
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
) = showOptionalCount(call, state, "Cultures", countEachCulture(characters))

fun HtmlBlockTag.showDomainCount(
    call: ApplicationCall,
    state: State,
    gods: Collection<God>,
) = showCount(call, state, "Domains", countEachDomain(gods))

fun HtmlBlockTag.showGenderCount(characters: Collection<Character>) =
    showCount("Genders", countEachGender(characters))

fun HtmlBlockTag.showSexualOrientationCount(characters: Collection<Character>) =
    showCount("Sexual Orientation", countEachSexualOrientation(characters))

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

fun HtmlBlockTag.showLanguageCountForCharacters(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Languages", state.countEachLanguage(characters))

fun HtmlBlockTag.showLanguageCountForSpells(
    call: ApplicationCall,
    state: State,
    characters: Collection<Spell>,
) = showCount(call, state, "Languages", countEachLanguage(characters))

fun HtmlBlockTag.showLanguageCountForTexts(
    call: ApplicationCall,
    state: State,
    texts: Collection<Text>,
) = showCount(call, state, "Languages", countEachLanguage(texts))

fun HtmlBlockTag.showMaterialCategoryCount(materials: Collection<Material>) =
    showCount("Material Category", countEachMaterialCategory(materials))

fun HtmlBlockTag.showBuildingOwnershipCount(call: ApplicationCall, state: State, collection: Collection<Building>) =
    showOwnerCount(call, state, collection.map { it.ownership })

fun HtmlBlockTag.showBusinessOwnershipCount(call: ApplicationCall, state: State, collection: Collection<Business>) =
    showOwnerCount(call, state, collection.map { it.ownership })

fun HtmlBlockTag.showPeriodicalOwnershipCount(call: ApplicationCall, state: State, collection: Collection<Periodical>) =
    showOwnerCount(call, state, collection.map { it.ownership })

fun HtmlBlockTag.showOwnerCount(
    call: ApplicationCall,
    state: State,
    ownershipCollection: Collection<History<Reference>>,
) {
    showMap("Ownership", countEachOwner(ownershipCollection)) { owner, count ->
        showReference(call, state, owner)
        +": $count"
    }
}

fun countEachOwner(ownershipCollection: Collection<History<Reference>>) = ownershipCollection
    .groupingBy { it.current }
    .eachCount()

fun HtmlBlockTag.showPersonalityCountForCharacters(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
    label: String = "Personality",
) = showCount(call, state, label, countEachCharacterTraitForCharacters(characters))

fun HtmlBlockTag.showPersonalityCountForGods(
    call: ApplicationCall,
    state: State,
    gods: Collection<God>,
    label: String = "Personality",
) = showCount(call, state, label, countEachCharacterTraitForGods(gods))

fun HtmlBlockTag.showPublicationFrequencies(characters: Collection<Periodical>) =
    showCount("Frequencies", countPublicationFrequencies(characters))

fun HtmlBlockTag.showRaceCount(
    call: ApplicationCall,
    state: State,
    characters: Collection<Character>,
) = showCount(call, state, "Races", countEachRace(characters))

fun HtmlBlockTag.showSpellOriginCount(characters: Collection<Spell>) =
    showCount("Origin", countSpellOrigin(characters))

fun HtmlBlockTag.showStreetTemplateCount(
    call: ApplicationCall,
    state: State,
    townMap: TownMapId,
) = showCount(call, state, "Street Templates", state.countEachStreetTemplate(townMap))

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

fun <ID : Id<ID>> HtmlBlockTag.showOptionalCount(
    call: ApplicationCall,
    state: State,
    label: String,
    map: Map<ID?, Int>,
) {
    showMap(label, map) { id, count ->
        if (id != null) {
            link(call, state, id)
        } else {
            +"None"
        }
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