package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.getEmployees

// generic

fun <Element : HasStartDate> State.getAgeComparator(): Comparator<Element> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Element, b: Element -> calendar.compareToOptional(a.startDate(), b.startDate()) }
}

// architectural style

fun State.sortArchitecturalStyles(sort: SortArchitecturalStyle = SortArchitecturalStyle.Name) =
    sortArchitecturalStyles(getArchitecturalStyleStorage().getAll(), sort)

fun State.sortArchitecturalStyles(
    styles: Collection<ArchitecturalStyle>,
    sort: SortArchitecturalStyle = SortArchitecturalStyle.Name,
) = styles
    .sortedWith(
        when (sort) {
            SortArchitecturalStyle.Name -> compareBy { it.name }
            SortArchitecturalStyle.Start -> getAgeComparator()
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

fun State.sortBuildings(
    buildings: Collection<Building>,
    sort: SortBuilding = SortBuilding.Name,
) = buildings
    .map { Pair(it, it.name(this)) }
    .sortedWith(
        when (sort) {
            SortBuilding.Name -> compareBy { it.second }
            SortBuilding.Construction -> getBuildingAgePairComparator()
        })

// business

fun State.sortBusinesses(sort: SortBusiness = SortBusiness.Name) =
    sortBusinesses(getBusinessStorage().getAll(), sort)

fun State.sortBusinesses(
    businesses: Collection<Business>,
    sort: SortBusiness = SortBusiness.Name,
) = businesses
    .sortedWith(
        when (sort) {
            SortBusiness.Name -> compareBy { it.name(this) }
            SortBusiness.Age -> getAgeComparator()
            SortBusiness.Employees -> compareBy<Business> { getEmployees(it.id).size }.reversed()
        }
    )

// character

fun State.getCharacterAgePairComparator(): Comparator<Pair<Character, String>> {
    val comparator = getAgeComparator<Character>()
    return Comparator { a: Pair<Character, String>, b: Pair<Character, String> -> comparator.compare(a.first, b.first) }
}

fun State.sortCharacters(sort: SortCharacter = SortCharacter.Name) =
    sortCharacters(getCharacterStorage().getAll(), sort)

fun State.sortCharacters(
    characters: Collection<Character>,
    sort: SortCharacter = SortCharacter.Name,
) = characters
    .map { Pair(it, it.name(this)) }
    .sortedWith(
        when (sort) {
            SortCharacter.Name -> compareBy { it.second }
            SortCharacter.Age -> getCharacterAgePairComparator()
        })

// domain

fun State.sortDomains(sort: SortDomain = SortDomain.Name) =
    sortDomains(getDomainStorage().getAll(), sort)

fun State.sortDomains(
    domains: Collection<Domain>,
    sort: SortDomain = SortDomain.Name,
) = domains
    .sortedWith(
        when (sort) {
            SortDomain.Name -> compareBy { it.name(this) }
        })

// font

fun State.sortFonts(sort: SortFont = SortFont.Name) =
    sortFonts(getFontStorage().getAll(), sort)

fun State.sortFonts(
    fonts: Collection<Font>,
    sort: SortFont = SortFont.Name,
) = fonts
    .sortedWith(
        when (sort) {
            SortFont.Name -> compareBy { it.name(this) }
            SortFont.Age -> getAgeComparator()
        })

// god

fun State.sortGods(sort: SortGod = SortGod.Name) =
    sortGods(getGodStorage().getAll(), sort)

fun State.sortGods(
    gods: Collection<God>,
    sort: SortGod = SortGod.Name,
) = gods
    .sortedWith(
        when (sort) {
            SortGod.Name -> compareBy { it.name(this) }
        })

// job

fun State.sortJobs(sort: SortJob = SortJob.Name) =
    sortJobs(getJobStorage().getAll(), sort)

fun State.sortJobs(
    jobs: Collection<Job>,
    sort: SortJob = SortJob.Name,
) = jobs
    .sortedWith(
        when (sort) {
            SortJob.Name -> compareBy { it.name(this) }
        })

// organization

fun State.sortOrganizations(sort: SortOrganization = SortOrganization.Name) =
    sortOrganizations(getOrganizationStorage().getAll(), sort)

fun State.sortOrganizations(
    organizations: Collection<Organization>,
    sort: SortOrganization = SortOrganization.Name,
) = organizations
    .sortedWith(
        when (sort) {
            SortOrganization.Name -> compareBy { it.name }
            SortOrganization.Age -> getAgeComparator()
        })

// race

fun State.sortRaces(sort: SortRace = SortRace.Name) =
    sortRaces(getRaceStorage().getAll(), sort)

fun State.sortRaces(
    races: Collection<Race>,
    sort: SortRace = SortRace.Name,
) = races
    .sortedWith(
        when (sort) {
            SortRace.MaxAge -> compareBy { it.lifeStages.getMaxAge() }
            SortRace.MaxHeight -> compareBy { it.height.getMax().millimeters }
            SortRace.Name -> compareBy { it.name(this) }
        })

// spell

fun State.sortSpells(sort: SortSpell = SortSpell.Name) =
    sortSpells(getSpellStorage().getAll(), sort)

fun State.sortSpells(
    spells: Collection<Spell>,
    sort: SortSpell = SortSpell.Name,
) = spells
    .sortedWith(
        when (sort) {
            SortSpell.Name -> compareBy { it.name }
            SortSpell.Age -> getAgeComparator()
        })

// text

fun State.sortTexts(sort: SortText = SortText.Name) =
    sortTexts(getTextStorage().getAll(), sort)

fun State.sortTexts(
    texts: Collection<Text>,
    sort: SortText = SortText.Name,
) = texts
    .sortedWith(
        when (sort) {
            SortText.Name -> compareBy { it.name(this) }
            SortText.Age -> getAgeComparator()
        })