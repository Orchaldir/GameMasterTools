package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building

// architectural style

fun State.sortArchitecturalStyles(sort: SortArchitecturalStyle = SortArchitecturalStyle.Name) =
    sortArchitecturalStyles(getArchitecturalStyleStorage().getAll(), sort)

fun sortArchitecturalStyles(
    buildings: Collection<ArchitecturalStyle>,
    sort: SortArchitecturalStyle = SortArchitecturalStyle.Name,
) = buildings
    .sortedWith(
        when (sort) {
            SortArchitecturalStyle.Name -> compareBy { it.name }
            SortArchitecturalStyle.Start -> compareBy { it.start.year }
            SortArchitecturalStyle.End -> compareBy { it.end?.year }
        })

// building

fun State.getBuildingAgeComparator(): Comparator<Building> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Building, b: Building -> calendar.compareToOptional(a.constructionDate, b.constructionDate) }
}

fun State.getBuildingAgePairComparator(): Comparator<Pair<Building, String>> {
    val comparator = getBuildingAgeComparator()
    return Comparator { a: Pair<Building, String>, b: Pair<Building, String> -> comparator.compare(a.first, b.first) }
}

fun State.sortBuildings(sort: SortBuilding = SortBuilding.Name) =
    sortBuildings(getBuildingStorage().getAll(), sort)

fun State.sortBuildings(buildings: Collection<Building>, sort: SortBuilding = SortBuilding.Name) = buildings
    .map { Pair(it, it.name(this)) }
    .sortedWith(
        when (sort) {
            SortBuilding.Name -> compareBy { it.second }
            SortBuilding.Construction -> getBuildingAgePairComparator()
        })

// business

fun State.getBusinessAgeComparator(): Comparator<Business> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Business, b: Business -> calendar.compareToOptional(a.startDate, b.startDate) }
}

fun State.sortBusinesses(sort: SortBusiness = SortBusiness.Name) =
    sortBusinesses(getBusinessStorage().getAll(), sort)

fun State.sortBusinesses(businesses: Collection<Business>, sort: SortBusiness = SortBusiness.Name) = businesses
    .sortedWith(
        when (sort) {
            SortBusiness.Name -> compareBy { it.name(this) }
            SortBusiness.Age -> getBusinessAgeComparator()
            SortBusiness.Employees -> compareBy<Business> { getEmployees(it.id).size }.reversed()
        }
    )

// character

fun State.getCharacterAgeComparator(): Comparator<Character> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Character, b: Character -> calendar.compareTo(a.birthDate, b.birthDate) }
}

fun State.getCharacterAgePairComparator(): Comparator<Pair<Character, String>> {
    val comparator = getCharacterAgeComparator()
    return Comparator { a: Pair<Character, String>, b: Pair<Character, String> -> comparator.compare(a.first, b.first) }
}

fun State.sortCharacters(sort: SortCharacter = SortCharacter.Name) =
    sortCharacters(getCharacterStorage().getAll(), sort)

fun State.sortCharacters(characters: Collection<Character>, sort: SortCharacter = SortCharacter.Name) = characters
    .map { Pair(it, it.name(this)) }
    .sortedWith(
        when (sort) {
            SortCharacter.Name -> compareBy { it.second }
            SortCharacter.Age -> getCharacterAgePairComparator()
        })

// text

fun State.getTextAgeComparator(): Comparator<Text> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Text, b: Text -> calendar.compareToOptional(a.date, b.date) }
}

fun State.sortTexts(sort: SortText = SortText.Name) =
    sortTexts(getTextStorage().getAll(), sort)

fun State.sortTexts(
    buildings: Collection<Text>,
    sort: SortText = SortText.Name,
) = buildings
    .sortedWith(
        when (sort) {
            SortText.Name -> compareBy { it.name }
            SortText.Age -> getTextAgeComparator()
        })