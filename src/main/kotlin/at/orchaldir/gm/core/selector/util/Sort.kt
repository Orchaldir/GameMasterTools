package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.character.Genonym
import at.orchaldir.gm.core.model.character.Mononym
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.Salary
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.core.selector.character.getBelievers
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.item.getEquipmentMadeOf
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.createSorter
import kotlin.collections.sortedWith

// generic

fun <Element : HasStartDate> State.getAgeComparator(): Comparator<Element> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Element, b: Element -> calendar.compareToOptional(a.startDate(), b.startDate()) }
}

fun <Element : HasComplexStartDate> State.getComplexAgeComparator(valueForNull: Int = Int.MAX_VALUE): Comparator<Element> {
    val sorter = getDefaultCalendar().createSorter()

    return compareBy { element ->
        val date = element.startDate(this)

        if (date == null) {
            valueForNull
        } else {
            sorter(date)
        }
    }
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
            SortArchitecturalStyle.Name -> compareBy { it.name.text }
            SortArchitecturalStyle.Start -> getAgeComparator()
            SortArchitecturalStyle.End -> compareBy { it.end?.year }
        })

// periodical

fun State.sortArticles(sort: SortArticle = SortArticle.Title) =
    sortArticles(getArticleStorage().getAll(), sort)

fun State.sortArticles(
    articles: Collection<Article>,
    sort: SortArticle = SortArticle.Title,
) = articles
    .sortedWith(
        when (sort) {
            SortArticle.Title -> compareBy { it.title.text }
            SortArticle.Date -> getAgeComparator()
        }
    )

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
            SortBusiness.Name -> compareBy { it.name.text }
            SortBusiness.Age -> getAgeComparator()
            SortBusiness.Employees -> compareByDescending { getEmployees(it.id).size }
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
    .map {
        val name = when (it.name) {
            is FamilyName -> it.name.family.text + it.name.given.text + it.name.middle?.text
            is Genonym -> it.name.given.text
            is Mononym -> it.name.name.text
        }.lowercase()
        Pair(it, name)
    }
    .sortedWith(
        when (sort) {
            SortCharacter.Name -> compareBy { it.second }
            SortCharacter.Age -> getCharacterAgePairComparator()
        })
    .map { it.first }

// currency

fun State.sortCurrencies(sort: SortCurrency = SortCurrency.Name) =
    sortCurrencies(getCurrencyStorage().getAll(), sort)

fun State.sortCurrencies(
    businesses: Collection<Currency>,
    sort: SortCurrency = SortCurrency.Name,
) = businesses
    .sortedWith(
        when (sort) {
            SortCurrency.Name -> compareBy { it.name.text }
            SortCurrency.Date -> getAgeComparator()
        }
    )

// currency unit

fun State.sortCurrencyUnits(sort: SortCurrencyUnit = SortCurrencyUnit.Name) =
    sortCurrencyUnits(getCurrencyUnitStorage().getAll(), sort)

fun State.sortCurrencyUnits(
    businesses: Collection<CurrencyUnit>,
    sort: SortCurrencyUnit = SortCurrencyUnit.Name,
) = businesses
    .sortedWith(
        when (sort) {
            SortCurrencyUnit.Name -> compareBy { it.name.text }
            SortCurrencyUnit.Value -> compareBy { it.denomination * 1000 + it.number }
        }
    )

// domain

fun State.sortDomains(sort: SortDomain = SortDomain.Name) =
    sortDomains(getDomainStorage().getAll(), sort)

fun State.sortDomains(
    domains: Collection<Domain>,
    sort: SortDomain = SortDomain.Name,
) = domains
    .sortedWith(
        when (sort) {
            SortDomain.Name -> compareBy { it.name.text }
        })

// equipment

fun State.sortEquipmentList(sort: SortEquipment = SortEquipment.Name) =
    sortEquipmentList(getEquipmentStorage().getAll(), sort)

fun State.sortEquipmentList(
    equipmentList: Collection<Equipment>,
    sort: SortEquipment = SortEquipment.Name,
) = equipmentList
    .sortedWith(
        when (sort) {
            SortEquipment.Name -> compareBy { it.name.text }
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
            SortFont.Name -> compareBy { it.name.text }
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
            SortGod.Name -> compareBy { it.name.text }
            SortGod.Believers -> compareByDescending { getBelievers(it.id).size }
        })

// holiday

fun State.sortHolidays() = sortHolidays(getHolidayStorage().getAll())

fun State.sortHolidays(
    holidays: Collection<Holiday>,
) = holidays.sortedBy { it.name.text }

// job

fun State.sortJobs(sort: SortJob = SortJob.Name) =
    sortJobs(getJobStorage().getAll(), sort)

fun State.sortJobs(
    jobs: Collection<Job>,
    sort: SortJob = SortJob.Name,
) = jobs
    .sortedWith(
        when (sort) {
            SortJob.Name -> compareBy { it.name.text }
            SortJob.Spells -> compareByDescending { it.spells.getSize() }
            SortJob.Income -> compareByDescending {
                if (it.income is Salary) {
                    it.income.salary.value
                } else {
                    0
                }
            }
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
            SortOrganization.Name -> compareBy { it.name.text }
            SortOrganization.Age -> getAgeComparator()
            SortOrganization.Members -> compareByDescending { it.countAllMembers() }
        })

// domain

fun State.sortPantheons(sort: SortPantheon = SortPantheon.Name) =
    sortPantheons(getPantheonStorage().getAll(), sort)

fun State.sortPantheons(
    domains: Collection<Pantheon>,
    sort: SortPantheon = SortPantheon.Name,
) = domains
    .sortedWith(
        when (sort) {
            SortPantheon.Name -> compareBy { it.name.text }
            SortPantheon.Gods -> compareBy { it.gods.size }
            SortPantheon.Believers -> compareByDescending { getBelievers(it.id).size }
        })

// material

fun State.sortMaterial(sort: SortMaterial = SortMaterial.Name) =
    sortMaterial(getMaterialStorage().getAll(), sort)

fun State.sortMaterial(
    planes: Collection<Material>,
    sort: SortMaterial = SortMaterial.Name,
) = planes
    .sortedWith(
        when (sort) {
            SortMaterial.Name -> compareBy { it.name.text }
            SortMaterial.Density -> compareByDescending { it.density.value() }
            SortMaterial.Currency -> compareByDescending { countCurrencyUnits(it.id) }
            SortMaterial.Equipment -> compareByDescending { countEquipment(it.id) }
        }
    )

// periodical

fun State.sortPeriodicals(sort: SortPeriodical = SortPeriodical.Name) =
    sortPeriodicals(getPeriodicalStorage().getAll(), sort)

fun State.sortPeriodicals(
    periodicals: Collection<Periodical>,
    sort: SortPeriodical = SortPeriodical.Name,
) = periodicals
    .sortedWith(
        when (sort) {
            SortPeriodical.Name -> compareBy { it.name.text }
            SortPeriodical.Age -> getAgeComparator()
        }
    )

// periodical issue

fun State.sortPeriodicalIssues(sort: SortPeriodicalIssue = SortPeriodicalIssue.Date) =
    sortPeriodicalIssues(getPeriodicalIssueStorage().getAll(), sort)

fun State.sortPeriodicalIssues(
    issues: Collection<PeriodicalIssue>,
    sort: SortPeriodicalIssue = SortPeriodicalIssue.Date,
) = issues
    .sortedWith(
        when (sort) {
            SortPeriodicalIssue.Periodical -> compareBy { getPeriodicalStorage().getOrThrow(it.periodical).name.text }
            SortPeriodicalIssue.Date -> getComplexAgeComparator()
        }
    )

// plane

fun State.sortPlanes(sort: SortPlane = SortPlane.Name) =
    sortPlanes(getPlaneStorage().getAll(), sort)

fun State.sortPlanes(
    planes: Collection<Plane>,
    sort: SortPlane = SortPlane.Name,
) = planes
    .sortedWith(
        when (sort) {
            SortPlane.Name -> compareBy { it.name.text }
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
            SortRace.Age -> getAgeComparator()
            SortRace.Height -> compareByDescending { it.height.center.value() }
            SortRace.Weight -> compareByDescending { it.weight.value() }
            SortRace.MaxLifeSpan -> compareByDescending { it.lifeStages.getMaxAge() }
            SortRace.Name -> compareBy { it.name.text }
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
            SortSpell.Name -> compareBy { it.name.text }
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
            SortText.Name -> compareBy { it.name.text }
            SortText.Age -> getAgeComparator()
        })

// title

fun State.sortTitles(sort: SortTitle = SortTitle.Name) =
    sortTitles(getTitleStorage().getAll(), sort)

fun State.sortTitles(
    titles: Collection<Title>,
    sort: SortTitle = SortTitle.Name,
) = titles
    .sortedWith(
        when (sort) {
            SortTitle.Name -> compareBy { it.name.text }
            SortTitle.Characters -> compareByDescending { countCharacters(it.id) }
        }
    )