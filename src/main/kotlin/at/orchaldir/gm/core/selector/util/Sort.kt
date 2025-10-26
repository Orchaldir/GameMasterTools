package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.Salary
import at.orchaldir.gm.core.model.economy.job.UndefinedIncome
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.rpg.combat.DamageType
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeapon
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.core.selector.character.countCharactersWithJob
import at.orchaldir.gm.core.selector.character.countResidents
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.culture.countCultures
import at.orchaldir.gm.core.selector.economy.money.calculateWeight
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.realm.countOwnedTowns
import at.orchaldir.gm.core.selector.realm.countRealmsWithCurrencyAtAnyTime
import at.orchaldir.gm.core.selector.realm.countRealmsWithLegalCodeAtAnyTime
import at.orchaldir.gm.core.selector.time.date.createSorter
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.world.countBuildings
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id

// generic

fun <ID : Id<ID>, ELEMENT : Element<ID>> State.sortElements(elements: Collection<ELEMENT>) = elements
    .sortedWith(compareBy { it.toSortString(this) })

fun <Element : HasStartDate> State.getStartDateComparator(valueForNull: Int = Int.MAX_VALUE) =
    getDateComparator<Element>(valueForNull) { it.startDate() }

fun <Element : HasStartAndEndDate> State.getEndDateComparator(valueForNull: Int = Int.MAX_VALUE) =
    getDateComparator<Element>(valueForNull) { it.endDate() }

fun <Element : HasComplexStartDate> State.getComplexStartAgeComparator(valueForNull: Int = Int.MAX_VALUE) =
    getDateComparator<Element>(valueForNull) { it.startDate(this) }

fun <T> State.getDateComparator(
    valueForNull: Int = Int.MAX_VALUE,
    getDate: (T) -> Date?,
): Comparator<T> {
    val sorter = getDefaultCalendar().createSorter()

    return compareBy { element ->
        val date = getDate(element)

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
            SortArchitecturalStyle.Start -> getStartDateComparator()
            SortArchitecturalStyle.End -> getEndDateComparator()
        })

// article

fun State.sortArticles(sort: SortArticle = SortArticle.Title) =
    sortArticles(getArticleStorage().getAll(), sort)

fun State.sortArticles(
    articles: Collection<Article>,
    sort: SortArticle = SortArticle.Title,
) = articles
    .sortedWith(
        when (sort) {
            SortArticle.Title -> compareBy { it.title.text }
            SortArticle.Date -> getStartDateComparator()
        }
    )

// battle

fun State.sortBattles(sort: SortBattle = SortBattle.Name) =
    sortBattles(getBattleStorage().getAll(), sort)

fun State.sortBattles(
    battles: Collection<Battle>,
    sort: SortBattle = SortBattle.Name,
) = battles
    .sortedWith(
        when (sort) {
            SortBattle.Name -> compareBy { it.name.text }
            SortBattle.Date -> getStartDateComparator()
            SortBattle.Participants -> compareByDescending { it.participants.size }
        })

// building

fun State.sortBuildings(sort: SortBuilding = SortBuilding.Name) =
    sortBuildings(getBuildingStorage().getAll(), sort)

fun State.sortBuildings(
    buildings: Collection<Building>,
    sort: SortBuilding = SortBuilding.Name,
) = buildings
    .sortedWith(
        when (sort) {
            SortBuilding.Name -> compareBy { it.name(this) }
            SortBuilding.Construction -> getStartDateComparator()
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
            SortBusiness.Date -> getStartDateComparator()
            SortBusiness.Employees -> compareByDescending { getEmployees(it.id).size }
        }
    )

// calendar

fun State.sortCalendars(sort: SortCalendar = SortCalendar.Name) =
    sortCalendars(getCalendarStorage().getAll(), sort)

fun State.sortCalendars(
    calendars: Collection<Calendar>,
    sort: SortCalendar = SortCalendar.Name,
) = calendars
    .sortedWith(
        when (sort) {
            SortCalendar.Name -> compareBy { it.name.text }
        })

// catastrophe

fun State.sortCatastrophes(sort: SortCatastrophe = SortCatastrophe.Name) =
    sortCatastrophes(getCatastropheStorage().getAll(), sort)

fun State.sortCatastrophes(
    catastrophes: Collection<Catastrophe>,
    sort: SortCatastrophe = SortCatastrophe.Name,
) = catastrophes
    .sortedWith(
        when (sort) {
            SortCatastrophe.Name -> compareBy { it.name.text }
            SortCatastrophe.Start -> getStartDateComparator()
            SortCatastrophe.End -> getEndDateComparator()
        })

// character

fun State.getCharacterStartDatePairComparator(): Comparator<Pair<Character, String>> {
    val comparator = getStartDateComparator<Character>()
    return Comparator { a: Pair<Character, String>, b: Pair<Character, String> -> comparator.compare(a.first, b.first) }
}

fun State.sortCharacters(sort: SortCharacter = SortCharacter.Name) =
    sortCharacters(getCharacterStorage().getAll(), sort)

fun State.sortCharacters(
    characters: Collection<Character>,
    sort: SortCharacter = SortCharacter.Name,
): List<Character> {
    val currentDay = getCurrentDate()

    return characters
        .map {
            Pair(it, it.name.toSortString())
        }
        .sortedWith(
            when (sort) {
                SortCharacter.Name -> compareBy { it.second }
                SortCharacter.Start -> getCharacterStartDatePairComparator()
                SortCharacter.Age -> compareByDescending { it.first.getAge(this, currentDay).days }
                SortCharacter.Cost -> compareByDescending { it.first.statblock.calculateCost(this) }
            })
        .map { it.first }
}

// character template

fun State.sortCharacterTemplates(sort: SortCharacterTemplate = SortCharacterTemplate.Name) =
    sortCharacterTemplates(getCharacterTemplateStorage().getAll(), sort)

fun State.sortCharacterTemplates(
    templates: Collection<CharacterTemplate>,
    sort: SortCharacterTemplate = SortCharacterTemplate.Name,
) = templates
    .sortedWith(
        when (sort) {
            SortCharacterTemplate.Name -> compareBy { it.name.text }
            SortCharacterTemplate.Cost -> compareByDescending { it.statblock.calculateCost(this) }
        })

// color scheme

fun State.sortColorSchemes(sort: SortColorScheme = SortColorScheme.Name) =
    sortColorSchemes(getColorSchemeStorage().getAll(), sort)

fun State.sortColorSchemes(
    battles: Collection<ColorScheme>,
    sort: SortColorScheme = SortColorScheme.Name,
) = battles
    .sortedWith(
        when (sort) {
            SortColorScheme.Name -> compareBy { it.name() }
            SortColorScheme.Equipment -> compareByDescending { countEquipment(it.id) }
        })

// culture

fun State.sortCultures(sort: SortCulture = SortCulture.Name) =
    sortCultures(getCultureStorage().getAll(), sort)

fun State.sortCultures(
    cultures: Collection<Culture>,
    sort: SortCulture = SortCulture.Name,
) = cultures
    .sortedWith(
        when (sort) {
            SortCulture.Name -> compareBy { it.name.text }
        })

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
            SortCurrency.Date -> getStartDateComparator()
            SortCurrency.Realms -> compareByDescending { countRealmsWithCurrencyAtAnyTime(it.id) }
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
            SortCurrencyUnit.Value -> compareByDescending { it.denomination * 1000 + it.number }
            SortCurrencyUnit.Weight -> compareByDescending { calculateWeight(it).value() }
        }
    )

// damage type

fun State.sortDamageTypes(sort: SortDamageType = SortDamageType.Name) =
    sortDamageTypes(getDamageTypeStorage().getAll(), sort)

fun State.sortDamageTypes(
    types: Collection<DamageType>,
    sort: SortDamageType = SortDamageType.Name,
) = types
    .sortedWith(
        when (sort) {
            SortDamageType.Name -> compareBy { it.name.text }
            SortDamageType.Short -> compareBy { it.short?.text }
        })

// data source

fun State.sortDataSources(sort: SortDataSource = SortDataSource.Name) =
    sortDataSources(getDataSourceStorage().getAll(), sort)

fun State.sortDataSources(
    sources: Collection<DataSource>,
    sort: SortDataSource = SortDataSource.Name,
) = sources
    .sortedWith(
        when (sort) {
            SortDataSource.Name -> compareBy { it.name.text }
            SortDataSource.Year -> compareBy { it.year }
            SortDataSource.Edition -> compareBy { it.edition }
        }
    )

// disease

fun State.sortDiseases(sort: SortDisease = SortDisease.Name) =
    sortDiseases(getDiseaseStorage().getAll(), sort)

fun State.sortDiseases(
    spells: Collection<Disease>,
    sort: SortDisease = SortDisease.Name,
) = spells
    .sortedWith(
        when (sort) {
            SortDisease.Name -> compareBy { it.name.text }
            SortDisease.Date -> getStartDateComparator()
        })

// district

fun State.sortDistricts(sort: SortDistrict = SortDistrict.Name) =
    sortDistricts(getDistrictStorage().getAll(), sort)

fun State.sortDistricts(
    districts: Collection<District>,
    sort: SortDistrict = SortDistrict.Name,
) = districts
    .sortedWith(
        when (sort) {
            SortDistrict.Name -> compareBy { it.name.text }
            SortDistrict.Date -> getStartDateComparator()
            SortDistrict.Population -> compareByDescending { it.population.getTotalPopulation() }
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
            SortDomain.Name -> compareBy { it.name.text }
            SortDomain.Spells -> compareByDescending { it.spells.getSize() }
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

// fashion

fun State.sortFashions(sort: SortFashion = SortFashion.Name) =
    sortFashions(getFashionStorage().getAll(), sort)

fun State.sortFashions(
    fashions: Collection<Fashion>,
    sort: SortFashion = SortFashion.Name,
) = fashions
    .sortedWith(
        when (sort) {
            SortFashion.Name -> compareBy { it.name.text }
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
            SortFont.Date -> getStartDateComparator()
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
            SortGod.Believers -> compareByDescending { getBelievers(getCharacterStorage(), it.id).size }
        })

// holiday

fun State.sortHolidays(sort: SortHoliday = SortHoliday.Name) =
    sortHolidays(getHolidayStorage().getAll(), sort)

fun State.sortHolidays(
    holidays: Collection<Holiday>,
    sort: SortHoliday = SortHoliday.Name,
) = holidays
    .sortedWith(
        when (sort) {
            SortHoliday.Name -> compareBy { it.name.text }
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
            SortJob.Name -> compareBy { it.name.text }
            SortJob.EmployerType -> compareBy { it.employerType }
            SortJob.Income -> compareByDescending {
                when (it.income) {
                    UndefinedIncome -> 0
                    is AffordableStandardOfLiving -> it.income.standard.value + 1
                    is Salary -> it.income.yearlySalary.value
                }
            }

            SortJob.Characters -> compareByDescending { countCharactersWithJob(it.id) }
            SortJob.Spells -> compareByDescending { it.spells.getSize() }
        })

// language

fun State.sortLanguages(sort: SortLanguage = SortLanguage.Name) =
    sortLanguages(getLanguageStorage().getAll(), sort)

fun State.sortLanguages(
    planes: Collection<Language>,
    sort: SortLanguage = SortLanguage.Name,
) = planes
    .sortedWith(
        when (sort) {
            SortLanguage.Name -> compareBy { it.name.text }
            SortLanguage.Characters -> compareByDescending { countCharacters(it.id) }
            SortLanguage.Cultures -> compareByDescending { countCultures(it.id) }
        })

// legal code

fun State.sortLegalCodes(sort: SortLegalCode = SortLegalCode.Name) =
    sortLegalCodes(getLegalCodeStorage().getAll(), sort)

fun State.sortLegalCodes(
    realms: Collection<LegalCode>,
    sort: SortLegalCode = SortLegalCode.Name,
) = realms
    .sortedWith(
        when (sort) {
            SortLegalCode.Name -> compareBy { it.name.text }
            SortLegalCode.Date -> getStartDateComparator()
            SortLegalCode.Realms -> compareByDescending { countRealmsWithLegalCodeAtAnyTime(it.id) }
        })

// magic tradition

fun State.sortMagicTraditions(sort: SortMagicTradition = SortMagicTradition.Name) =
    sortMagicTraditions(getMagicTraditionStorage().getAll(), sort)

fun State.sortMagicTraditions(
    traditions: Collection<MagicTradition>,
    sort: SortMagicTradition = SortMagicTradition.Name,
) = traditions
    .sortedWith(
        when (sort) {
            SortMagicTradition.Name -> compareBy { it.name.text }
            SortMagicTradition.Date -> getStartDateComparator()
            SortMagicTradition.Groups -> compareByDescending { it.groups.size }
        })

// material

fun State.sortMaterials(sort: SortMaterial = SortMaterial.Name) =
    sortMaterials(getMaterialStorage().getAll(), sort)

fun State.sortMaterials(
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

// melee weapons

fun State.sortMeleeWeapons(sort: SortMeleeWeapon = SortMeleeWeapon.Name) =
    sortMeleeWeapons(getMeleeWeaponStorage().getAll(), sort)

fun State.sortMeleeWeapons(
    weapons: Collection<MeleeWeapon>,
    sort: SortMeleeWeapon = SortMeleeWeapon.Name,
) = weapons
    .sortedWith(
        when (sort) {
            SortMeleeWeapon.Name -> compareBy { it.name.text }
        })

// moon

fun State.sortMoons(sort: SortMoon = SortMoon.Name) =
    sortMoons(getMoonStorage().getAll(), sort)

fun State.sortMoons(
    regions: Collection<Moon>,
    sort: SortMoon = SortMoon.Name,
) = regions
    .sortedWith(
        when (sort) {
            SortMoon.Name -> compareBy { it.name.text }
        })

// name list

fun State.sortNameLists(sort: SortNameList = SortNameList.Name) =
    sortNameLists(getNameListStorage().getAll(), sort)

fun State.sortNameLists(
    lists: Collection<NameList>,
    sort: SortNameList = SortNameList.Name,
) = lists
    .sortedWith(
        when (sort) {
            SortNameList.Name -> compareBy { it.name.text }
            SortNameList.Size -> compareByDescending { it.names.size }
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
            SortOrganization.Date -> getStartDateComparator()
            SortOrganization.Members -> compareByDescending { it.countAllMembers() }
        })

// pantheon

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
            SortPantheon.Believers -> compareByDescending { getBelievers(getCharacterStorage(), it.id).size }
        })

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
            SortPeriodical.Date -> getStartDateComparator()
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
            SortPeriodicalIssue.Date -> getComplexStartAgeComparator()
        }
    )

// plane

fun State.sortPersonalityTraits(sort: SortPersonalityTrait = SortPersonalityTrait.Name) =
    sortPersonalityTraits(getPersonalityTraitStorage().getAll(), sort)

fun State.sortPersonalityTraits(
    planes: Collection<PersonalityTrait>,
    sort: SortPersonalityTrait = SortPersonalityTrait.Name,
) = planes
    .sortedWith(
        when (sort) {
            SortPersonalityTrait.Name -> compareBy { it.name.text }
        })

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

// quote

fun State.sortQuotes(sort: SortQuote = SortQuote.Name) =
    sortQuotes(getQuoteStorage().getAll(), sort)

fun State.sortQuotes(
    businesses: Collection<Quote>,
    sort: SortQuote = SortQuote.Name,
) = businesses
    .sortedWith(
        when (sort) {
            SortQuote.Name -> compareBy { it.text.text }
            SortQuote.Date -> getStartDateComparator()
        }
    )

// race

fun State.sortRaces(sort: SortRace = SortRace.Name) =
    sortRaces(getRaceStorage().getAll(), sort)

fun State.sortRaces(
    races: Collection<Race>,
    sort: SortRace = SortRace.Name,
) = races
    .sortedWith(
        when (sort) {
            SortRace.Age -> getStartDateComparator()
            SortRace.Height -> compareByDescending { it.height.center.value() }
            SortRace.Weight -> compareByDescending { it.weight.value() }
            SortRace.MaxLifeSpan -> compareByDescending { it.lifeStages.getMaxAge() }
            SortRace.Name -> compareBy { it.name.text }
            SortRace.Population -> compareByDescending { getTotalPopulation(it.id) }
            SortRace.Characters -> compareByDescending { countCharacters(it.id) }
        })

// race appearance

fun State.sortRaceAppearances(sort: SortRaceAppearance = SortRaceAppearance.Name) =
    sortRaceAppearances(getRaceAppearanceStorage().getAll(), sort)

fun State.sortRaceAppearances(
    appearances: Collection<RaceAppearance>,
    sort: SortRaceAppearance = SortRaceAppearance.Name,
) = appearances
    .sortedWith(
        when (sort) {
            SortRaceAppearance.Name -> compareBy { it.name.text }
        })

// realm

fun State.sortRealms(sort: SortRealm = SortRealm.Name) =
    sortRealms(getRealmStorage().getAll(), sort)

fun State.sortRealms(
    realms: Collection<Realm>,
    sort: SortRealm = SortRealm.Name,
) = realms
    .sortedWith(
        when (sort) {
            SortRealm.Name -> compareBy { it.name.text }
            SortRealm.Start -> getStartDateComparator()
            SortRealm.End -> getEndDateComparator()
            SortRealm.Age -> compareByDescending { it.getAgeInYears(this) }
            SortRealm.Population -> compareByDescending { it.population.getTotalPopulation() }
            SortRealm.Towns -> compareByDescending { countOwnedTowns(it.id) }
        })

// region

fun State.sortRegions(sort: SortRegion = SortRegion.Name) =
    sortRegions(getRegionStorage().getAll(), sort)

fun State.sortRegions(
    regions: Collection<Region>,
    sort: SortRegion = SortRegion.Name,
) = regions
    .sortedWith(
        when (sort) {
            SortRegion.Name -> compareBy { it.name.text }
        })

// river

fun State.sortRivers(sort: SortRiver = SortRiver.Name) =
    sortRivers(getRiverStorage().getAll(), sort)

fun State.sortRivers(
    rivers: Collection<River>,
    sort: SortRiver = SortRiver.Name,
) = rivers
    .sortedWith(
        when (sort) {
            SortRiver.Name -> compareBy { it.name.text }
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
            SortSpell.Date -> getStartDateComparator()
        })

// spell group

fun State.sortSpellGroups(sort: SortSpellGroup = SortSpellGroup.Name) =
    sortSpellGroups(getSpellGroupStorage().getAll(), sort)

fun State.sortSpellGroups(
    groups: Collection<SpellGroup>,
    sort: SortSpellGroup = SortSpellGroup.Name,
) = groups
    .sortedWith(
        when (sort) {
            SortSpellGroup.Name -> compareBy { it.name.text }
            SortSpellGroup.Spells -> compareByDescending { it.spells.size }
        })

// statistic

fun State.sortStatistics(sort: SortStatistic = SortStatistic.Name) =
    sortStatistics(getStatisticStorage().getAll(), sort)

fun State.sortStatistics(
    templates: Collection<Statistic>,
    sort: SortStatistic = SortStatistic.Name,
) = templates
    .sortedWith(
        when (sort) {
            SortStatistic.Name -> compareBy { it.name.text }
        })

// street

fun State.sortStreets(sort: SortStreet = SortStreet.Name) =
    sortStreets(getStreetStorage().getAll(), sort)

fun State.sortStreets(
    streets: Collection<Street>,
    sort: SortStreet = SortStreet.Name,
) = streets
    .sortedWith(
        when (sort) {
            SortStreet.Name -> compareBy { it.name.text }
        })

// street template

fun State.sortStreetTemplates(sort: SortStreetTemplate = SortStreetTemplate.Name) =
    sortStreetTemplates(getStreetTemplateStorage().getAll(), sort)

fun State.sortStreetTemplates(
    templates: Collection<StreetTemplate>,
    sort: SortStreetTemplate = SortStreetTemplate.Name,
) = templates
    .sortedWith(
        when (sort) {
            SortStreetTemplate.Name -> compareBy { it.name.text }
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
            SortText.Date -> getStartDateComparator()
            SortText.Pages -> compareByDescending { it.content.pages() }
            SortText.Spells -> compareByDescending { it.content.spells().size }
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

// town

fun State.sortTowns(sort: SortTown = SortTown.Name) =
    sortTowns(getTownStorage().getAll(), sort)

fun State.sortTowns(
    towns: Collection<Town>,
    sort: SortTown = SortTown.Name,
) = towns
    .sortedWith(
        when (sort) {
            SortTown.Name -> compareBy { it.name.text }
            SortTown.Date -> getStartDateComparator()
            SortTown.Population -> compareByDescending { it.population.getTotalPopulation() }
            SortTown.Buildings -> compareByDescending { countBuildings(it.id) }
            SortTown.Residents -> compareByDescending { countResidents(it.id) }
        })

// town map

fun State.sortTownMaps(sort: SortTownMap = SortTownMap.Name) =
    sortTownMaps(getTownMapStorage().getAll(), sort)

fun State.sortTownMaps(
    towns: Collection<TownMap>,
    sort: SortTownMap = SortTownMap.Name,
) = towns
    .sortedWith(
        when (sort) {
            SortTownMap.Name -> compareByDescending<TownMap> { it.name(this) }
                .thenComparing(getStartDateComparator())
        })

// treaty

fun State.sortTreaties(sort: SortTreaty = SortTreaty.Name) =
    sortTreaties(getTreatyStorage().getAll(), sort)

fun State.sortTreaties(
    treaties: Collection<Treaty>,
    sort: SortTreaty = SortTreaty.Name,
) = treaties
    .sortedWith(
        when (sort) {
            SortTreaty.Name -> compareBy { it.name.text }
            SortTreaty.Date -> getStartDateComparator()
            SortTreaty.Participants -> compareByDescending { it.participants.size }
        })

// uniform

fun State.sortUniforms(sort: SortUniform = SortUniform.Name) =
    sortUniforms(getUniformStorage().getAll(), sort)

fun State.sortUniforms(
    uniforms: Collection<Uniform>,
    sort: SortUniform = SortUniform.Name,
) = uniforms
    .sortedWith(
        when (sort) {
            SortUniform.Name -> compareBy { it.name.text }
        })

// war

fun State.sortWars(sort: SortWar = SortWar.Name) =
    sortWars(getWarStorage().getAll(), sort)

fun State.sortWars(
    wars: Collection<War>,
    sort: SortWar = SortWar.Name,
) = wars
    .sortedWith(
        when (sort) {
            SortWar.Name -> compareBy { it.name.text }
            SortWar.Start -> getStartDateComparator()
            SortWar.End -> getEndDateComparator()
            SortWar.Duration -> compareByDescending { it.getDuration(this).days }
        })

// world

fun State.sortWorlds(sort: SortWorld = SortWorld.Name) =
    sortWorlds(getWorldStorage().getAll(), sort)

fun State.sortWorlds(
    worlds: Collection<World>,
    sort: SortWorld = SortWorld.Name,
) = worlds
    .sortedWith(
        when (sort) {
            SortWorld.Name -> compareBy { it.name.text }
        })