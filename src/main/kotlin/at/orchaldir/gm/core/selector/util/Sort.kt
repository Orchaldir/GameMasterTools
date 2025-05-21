package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.character.Genonym
import at.orchaldir.gm.core.model.character.Mononym
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.Salary
import at.orchaldir.gm.core.model.economy.job.UndefinedIncome
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.illness.Illness
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
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.region.Region
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.character.*
import at.orchaldir.gm.core.selector.culture.countCultures
import at.orchaldir.gm.core.selector.economy.money.calculateWeight
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.realm.countOwnedTowns
import at.orchaldir.gm.core.selector.realm.countRealmsWithCurrencyAtAnyTime
import at.orchaldir.gm.core.selector.realm.countRealmsWithLegalCodeAtAnyTime
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.createSorter
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.world.countBuildings

// generic

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
            SortBusiness.Date -> getStartDateComparator()
            SortBusiness.Employees -> compareByDescending { getEmployees(it.id).size }
        }
    )

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
                SortCharacter.Start -> getCharacterStartDatePairComparator()
                SortCharacter.Age -> compareByDescending { it.first.getAge(this, currentDay).day }
            })
        .map { it.first }
}

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

// quote

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
            SortGod.Believers -> compareByDescending { getBelievers(it.id).size }
        })

// holiday

fun State.sortHolidays() = sortHolidays(getHolidayStorage().getAll())

fun State.sortHolidays(
    holidays: Collection<Holiday>,
) = holidays.sortedBy { it.name.text }

// illness

fun State.sortIllnesses(sort: SortIllness = SortIllness.Name) =
    sortIllnesses(getIllnessStorage().getAll(), sort)

fun State.sortIllnesses(
    illnesses: Collection<Illness>,
    sort: SortIllness = SortIllness.Name,
) = illnesses
    .sortedWith(
        when (sort) {
            SortIllness.Name -> compareBy { it.name.text }
            SortIllness.Date -> getStartDateComparator()
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
            SortPantheon.Believers -> compareByDescending { getBelievers(it.id).size }
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
            SortRealm.Towns -> compareByDescending { countOwnedTowns(it.id) }
        })

// region

fun State.sortRegions(sort: SortRegion = SortRegion.Name) =
    sortRegions(getRegionStorage().getAll(), sort)

fun State.sortRegions(
    planes: Collection<Region>,
    sort: SortRegion = SortRegion.Name,
) = planes
    .sortedWith(
        when (sort) {
            SortRegion.Name -> compareBy { it.name.text }
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
            SortTown.Residents -> compareByDescending { countResident(it.id) }
            SortTown.Buildings -> compareByDescending { countBuildings(it.id) }
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
        })