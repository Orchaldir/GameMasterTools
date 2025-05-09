package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.load
import at.orchaldir.gm.core.loadStorage
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.title.TITLE_TYPE
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.culture.CULTURE_TYPE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.FASHION_TYPE
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.economy.business.BUSINESS_TYPE
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JOB_TYPE
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.font.FONT_TYPE
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.holiday.HOLIDAY_TYPE
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.item.UNIFORM_TYPE
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EQUIPMENT_TYPE
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.periodical.*
import at.orchaldir.gm.core.model.item.text.TEXT_TYPE
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.language.LANGUAGE_TYPE
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.magic.SPELL_TYPE
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.material.MATERIAL_TYPE
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.name.NAME_LIST_TYPE
import at.orchaldir.gm.core.model.name.NameList
import at.orchaldir.gm.core.model.name.NameListId
import at.orchaldir.gm.core.model.organization.ORGANIZATION_TYPE
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RACE_TYPE
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RACE_APPEARANCE_TYPE
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.religion.*
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.moon.MOON_TYPE
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.plane.PLANE_TYPE
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.street.*
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.TOWN_TYPE
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.reducer.*
import at.orchaldir.gm.core.reducer.character.validateCharacter
import at.orchaldir.gm.core.reducer.culture.validateCulture
import at.orchaldir.gm.core.reducer.culture.validateFashion
import at.orchaldir.gm.core.reducer.economy.validateBusiness
import at.orchaldir.gm.core.reducer.economy.validateCurrency
import at.orchaldir.gm.core.reducer.economy.validateCurrencyUnit
import at.orchaldir.gm.core.reducer.economy.validateJob
import at.orchaldir.gm.core.reducer.item.periodical.validateArticle
import at.orchaldir.gm.core.reducer.item.periodical.validatePeriodical
import at.orchaldir.gm.core.reducer.item.periodical.validatePeriodicalIssue
import at.orchaldir.gm.core.reducer.item.validateEquipment
import at.orchaldir.gm.core.reducer.item.validateText
import at.orchaldir.gm.core.reducer.item.validateUniform
import at.orchaldir.gm.core.reducer.magic.validateSpell
import at.orchaldir.gm.core.reducer.organization.validateOrganization
import at.orchaldir.gm.core.reducer.religion.validateDomain
import at.orchaldir.gm.core.reducer.religion.validateGod
import at.orchaldir.gm.core.reducer.religion.validatePantheon
import at.orchaldir.gm.core.reducer.time.validateCalendar
import at.orchaldir.gm.core.reducer.world.*
import at.orchaldir.gm.core.reducer.world.town.validateTown
import at.orchaldir.gm.core.save
import at.orchaldir.gm.core.saveStorage
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

val ELEMENTS =
    setOf(
        ARCHITECTURAL_STYLE_TYPE,
        ARTICLE_TYPE,
        BUILDING_TYPE,
        BUSINESS_TYPE,
        CALENDAR_TYPE,
        CHARACTER_TYPE,
        CULTURE_TYPE,
        CURRENCY_TYPE,
        CURRENCY_UNIT_TYPE,
        DOMAIN_TYPE,
        EQUIPMENT_TYPE,
        FASHION_TYPE,
        FONT_TYPE,
        GOD_TYPE,
        HOLIDAY_TYPE,
        JOB_TYPE,
        LANGUAGE_TYPE,
        MATERIAL_TYPE,
        MOON_TYPE,
        MOUNTAIN_TYPE,
        NAME_LIST_TYPE,
        ORGANIZATION_TYPE,
        PANTHEON_TYPE,
        PERIODICAL_TYPE,
        PERIODICAL_ISSUE_TYPE,
        PERSONALITY_TRAIT_TYPE,
        PLANE_TYPE,
        RACE_TYPE,
        RACE_APPEARANCE_TYPE,
        RIVER_TYPE,
        SPELL_TYPE,
        STREET_TYPE,
        STREET_TEMPLATE_TYPE,
        TEXT_TYPE,
        TITLE_TYPE,
        TOWN_TYPE,
        UNIFORM_TYPE,
    )
private const val DATA = "Data"

data class State(
    val storageMap: Map<String, Storage<*, *>> = emptyMap(),
    val path: String = "data",
    val data: Data = Data(),
    val rarityGenerator: RarityGenerator = RarityGenerator.empty(5),
) {
    constructor(
        storage: Storage<*, *>,
        path: String = "data",
        data: Data = Data(),
        rarityGenerator: RarityGenerator = RarityGenerator.empty(5),
    ) : this(mapOf(storage.getType() to storage), path, data, rarityGenerator)

    constructor(
        storageList: List<Storage<*, *>>,
        path: String = "data",
        data: Data = Data(),
        rarityGenerator: RarityGenerator = RarityGenerator.empty(5),
    ) : this(storageList.associateBy { it.getType() }, path, data, rarityGenerator)

    fun getArchitecturalStyleStorage() = getStorage<ArchitecturalStyleId, ArchitecturalStyle>(ARCHITECTURAL_STYLE_TYPE)
    fun getArticleStorage() = getStorage<ArticleId, Article>(ARTICLE_TYPE)
    fun getBuildingStorage() = getStorage<BuildingId, Building>(BUILDING_TYPE)
    fun getBusinessStorage() = getStorage<BusinessId, Business>(BUSINESS_TYPE)
    fun getCalendarStorage() = getStorage<CalendarId, Calendar>(CALENDAR_TYPE)
    fun getCharacterStorage() = getStorage<CharacterId, Character>(CHARACTER_TYPE)
    fun getCultureStorage() = getStorage<CultureId, Culture>(CULTURE_TYPE)
    fun getCurrencyStorage() = getStorage<CurrencyId, Currency>(CURRENCY_TYPE)
    fun getCurrencyUnitStorage() = getStorage<CurrencyUnitId, CurrencyUnit>(CURRENCY_UNIT_TYPE)
    fun getDomainStorage() = getStorage<DomainId, Domain>(DOMAIN_TYPE)
    fun getEquipmentStorage() = getStorage<EquipmentId, Equipment>(EQUIPMENT_TYPE)
    fun getFashionStorage() = getStorage<FashionId, Fashion>(FASHION_TYPE)
    fun getFontStorage() = getStorage<FontId, Font>(FONT_TYPE)
    fun getGodStorage() = getStorage<GodId, God>(GOD_TYPE)
    fun getHolidayStorage() = getStorage<HolidayId, Holiday>(HOLIDAY_TYPE)
    fun getJobStorage() = getStorage<JobId, Job>(JOB_TYPE)
    fun getLanguageStorage() = getStorage<LanguageId, Language>(LANGUAGE_TYPE)
    fun getMaterialStorage() = getStorage<MaterialId, Material>(MATERIAL_TYPE)
    fun getMoonStorage() = getStorage<MoonId, Moon>(MOON_TYPE)
    fun getMountainStorage() = getStorage<MountainId, Mountain>(MOUNTAIN_TYPE)
    fun getNameListStorage() = getStorage<NameListId, NameList>(NAME_LIST_TYPE)
    fun getOrganizationStorage() = getStorage<OrganizationId, Organization>(ORGANIZATION_TYPE)
    fun getPantheonStorage() = getStorage<PantheonId, Pantheon>(PANTHEON_TYPE)
    fun getPeriodicalStorage() = getStorage<PeriodicalId, Periodical>(PERIODICAL_TYPE)
    fun getPeriodicalIssueStorage() = getStorage<PeriodicalIssueId, PeriodicalIssue>(PERIODICAL_ISSUE_TYPE)
    fun getPersonalityTraitStorage() = getStorage<PersonalityTraitId, PersonalityTrait>(PERSONALITY_TRAIT_TYPE)
    fun getPlaneStorage() = getStorage<PlaneId, Plane>(PLANE_TYPE)
    fun getRaceStorage() = getStorage<RaceId, Race>(RACE_TYPE)
    fun getRaceAppearanceStorage() = getStorage<RaceAppearanceId, RaceAppearance>(RACE_APPEARANCE_TYPE)
    fun getRiverStorage() = getStorage<RiverId, River>(RIVER_TYPE)
    fun getSpellStorage() = getStorage<SpellId, Spell>(SPELL_TYPE)
    fun getStreetStorage() = getStorage<StreetId, Street>(STREET_TYPE)
    fun getStreetTemplateStorage() = getStorage<StreetTemplateId, StreetTemplate>(STREET_TEMPLATE_TYPE)
    fun getTextStorage() = getStorage<TextId, Text>(TEXT_TYPE)
    fun getTitleStorage() = getStorage<TitleId, Title>(TITLE_TYPE)
    fun getTownStorage() = getStorage<TownId, Town>(TOWN_TYPE)
    fun getUniformStorage() = getStorage<UniformId, Uniform>(UNIFORM_TYPE)

    fun <ID : Id<ID>, ELEMENT : Element<ID>> getStorage(id: ID) = getStorage<ID, ELEMENT>(id.type())

    private fun <ID : Id<ID>, ELEMENT : Element<ID>> getStorage(type: String): Storage<ID, ELEMENT> {
        val storage = storageMap[type]

        if (storage != null) {
            @Suppress("UNCHECKED_CAST")
            return storage as Storage<ID, ELEMENT>
        }

        @Suppress("UNCHECKED_CAST")
        return createStorage(type) as Storage<ID, ELEMENT>
    }

    fun <ID : Id<ID>> getElementName(id: ID): String {
        val storage = storageMap[id.type()]

        if (storage != null) {
            @Suppress("UNCHECKED_CAST")
            val element = (storage as Storage<ID, Element<ID>>).get(id)

            if (element != null) {
                return element.name(this)
            }
        } else if (id is StandardOfLivingId) {
            return data.economy.getStandardOfLiving(id).name()
        }

        return "Unknown"
    }

    fun <ID : Id<ID>> removeStorage(id: ID) = removeStorage(id.type())

    fun removeStorage(type: String): State {
        val newMap = storageMap.toMutableMap()
        newMap.remove(type)

        return copy(storageMap = newMap)
    }

    fun updateStorage(storage: Storage<*, *>): State {
        val newMap = storageMap.toMutableMap()
        newMap[storage.getType()] = storage

        return copy(storageMap = newMap)
    }

    fun updateStorage(storages: Collection<Storage<*, *>>): State {
        val newMap = storageMap.toMutableMap()

        storages.forEach {
            newMap[it.getType()] = it
        }

        return copy(storageMap = newMap)
    }

    companion object {

        fun load(path: String): State {
            logger.info { "Load $path" }

            return State(
                ELEMENTS.associateWith { loadStorageForType(path, it) },
                path,
                load(path, DATA)
            )
        }
    }

    fun validate() {
        logger.info { "Validate state" }

        require(ELEMENTS.size == storageMap.size) { "Wrong number element storages!" }

        validate(getArchitecturalStyleStorage()) { validateArchitecturalStyle(this, it) }
        validate(getArticleStorage()) { validateArticle(this, it) }
        validate(getBuildingStorage()) { validateBuilding(this, it) }
        validate(getBusinessStorage()) { validateBusiness(this, it) }
        validate(getCalendarStorage()) { validateCalendar(this, it) }
        validate(getCharacterStorage()) { validateCharacter(this, it) }
        validate(getCultureStorage()) { validateCulture(this, it) }
        validate(getCurrencyStorage()) { validateCurrency(this, it) }
        validate(getCurrencyUnitStorage()) { validateCurrencyUnit(this, it) }
        validate(getDomainStorage()) { validateDomain(this, it) }
        validate(getEquipmentStorage()) { validateEquipment(this, it) }
        validate(getFashionStorage()) { validateFashion(this, it) }
        validate(getFontStorage()) { validateFont(this, it) }
        validate(getGodStorage()) { validateGod(this, it) }
        validate(getHolidayStorage()) { validateHoliday(this, it) }
        validate(getJobStorage()) { validateJob(this, it) }
        validate(getLanguageStorage()) { validateLanguage(this, it) }
        validate(getMoonStorage()) { validateMoon(this, it) }
        validate(getMountainStorage()) { validateMountain(this, it) }
        validate(getOrganizationStorage()) { validateOrganization(this, it) }
        validate(getPantheonStorage()) { validatePantheon(this, it) }
        validate(getPeriodicalStorage()) { validatePeriodical(this, it) }
        validate(getPeriodicalIssueStorage()) { validatePeriodicalIssue(this, it) }
        validate(getPlaneStorage()) { validatePlane(this, it) }
        validate(getRaceStorage()) { validateRace(this, it) }
        validate(getRaceAppearanceStorage()) { validateRaceAppearance(it) }
        validate(getSpellStorage()) { validateSpell(this, it) }
        validate(getStreetTemplateStorage()) { validateStreetTemplate(this, it) }
        validate(getTextStorage()) { validateText(this, it) }
        validate(getTownStorage()) { validateTown(this, it) }
        validate(getUniformStorage()) { validateUniform(this, it) }

        validateData(this, data)
    }

    fun save() {
        saveStorage(path, getArchitecturalStyleStorage())
        saveStorage(path, getArticleStorage())
        saveStorage(path, getBuildingStorage())
        saveStorage(path, getBusinessStorage())
        saveStorage(path, getCalendarStorage())
        saveStorage(path, getCharacterStorage())
        saveStorage(path, getCultureStorage())
        saveStorage(path, getCurrencyStorage())
        saveStorage(path, getCurrencyUnitStorage())
        saveStorage(path, getDomainStorage())
        saveStorage(path, getEquipmentStorage())
        saveStorage(path, getFashionStorage())
        saveStorage(path, getFontStorage())
        saveStorage(path, getGodStorage())
        saveStorage(path, getHolidayStorage())
        saveStorage(path, getJobStorage())
        saveStorage(path, getLanguageStorage())
        saveStorage(path, getMaterialStorage())
        saveStorage(path, getMoonStorage())
        saveStorage(path, getMountainStorage())
        saveStorage(path, getNameListStorage())
        saveStorage(path, getOrganizationStorage())
        saveStorage(path, getPantheonStorage())
        saveStorage(path, getPeriodicalStorage())
        saveStorage(path, getPeriodicalIssueStorage())
        saveStorage(path, getPersonalityTraitStorage())
        saveStorage(path, getPlaneStorage())
        saveStorage(path, getRaceStorage())
        saveStorage(path, getRaceAppearanceStorage())
        saveStorage(path, getRiverStorage())
        saveStorage(path, getSpellStorage())
        saveStorage(path, getStreetStorage())
        saveStorage(path, getStreetTemplateStorage())
        saveStorage(path, getTextStorage())
        saveStorage(path, getTitleStorage())
        saveStorage(path, getTownStorage())
        saveStorage(path, getUniformStorage())
        save(path, DATA, data)
    }
}

fun createStorage(type: String) = when (type) {
    ARCHITECTURAL_STYLE_TYPE -> Storage(ArchitecturalStyleId(0))
    ARTICLE_TYPE -> Storage(ArticleId(0))
    BUILDING_TYPE -> Storage(BuildingId(0))
    BUSINESS_TYPE -> Storage(BusinessId(0))
    CALENDAR_TYPE -> Storage(CalendarId(0))
    CHARACTER_TYPE -> Storage(CharacterId(0))
    CULTURE_TYPE -> Storage(CultureId(0))
    CURRENCY_TYPE -> Storage(CurrencyId(0))
    CURRENCY_UNIT_TYPE -> Storage(CurrencyUnitId(0))
    DOMAIN_TYPE -> Storage(DomainId(0))
    EQUIPMENT_TYPE -> Storage(EquipmentId(0))
    FASHION_TYPE -> Storage(FashionId(0))
    FONT_TYPE -> Storage(FontId(0))
    GOD_TYPE -> Storage(GodId(0))
    HOLIDAY_TYPE -> Storage(HolidayId(0))
    JOB_TYPE -> Storage(JobId(0))
    LANGUAGE_TYPE -> Storage(LanguageId(0))
    MATERIAL_TYPE -> Storage(MaterialId(0))
    MOON_TYPE -> Storage(MoonId(0))
    MOUNTAIN_TYPE -> Storage(MountainId(0))
    NAME_LIST_TYPE -> Storage(NameListId(0))
    ORGANIZATION_TYPE -> Storage(OrganizationId(0))
    PANTHEON_TYPE -> Storage(PantheonId(0))
    PERIODICAL_TYPE -> Storage(PeriodicalId(0))
    PERIODICAL_ISSUE_TYPE -> Storage(PeriodicalIssueId(0))
    PERSONALITY_TRAIT_TYPE -> Storage(PersonalityTraitId(0))
    PLANE_TYPE -> Storage(PlaneId(0))
    RACE_TYPE -> Storage(RaceId(0))
    RACE_APPEARANCE_TYPE -> Storage(RaceAppearanceId(0))
    RIVER_TYPE -> Storage(RiverId(0))
    SPELL_TYPE -> Storage(SpellId(0))
    STREET_TYPE -> Storage(StreetId(0))
    STREET_TEMPLATE_TYPE -> Storage(StreetTemplateId(0))
    TEXT_TYPE -> Storage(TextId(0))
    TITLE_TYPE -> Storage(TitleId(0))
    TOWN_TYPE -> Storage(TownId(0))
    UNIFORM_TYPE -> Storage(UniformId(0))
    else -> throw IllegalArgumentException("Unknown type $type")
}

fun loadStorageForType(path: String, type: String): Storage<*, *> = when (type) {
    ARCHITECTURAL_STYLE_TYPE -> loadStorage<ArchitecturalStyleId, ArchitecturalStyle>(path, ArchitecturalStyleId(0))
    ARTICLE_TYPE -> loadStorage<ArticleId, Article>(path, ArticleId(0))
    BUILDING_TYPE -> loadStorage<BuildingId, Building>(path, BuildingId(0))
    BUSINESS_TYPE -> loadStorage<BusinessId, Business>(path, BusinessId(0))
    CALENDAR_TYPE -> loadStorage<CalendarId, Calendar>(path, CalendarId(0))
    CHARACTER_TYPE -> loadStorage<CharacterId, Character>(path, CharacterId(0))
    CULTURE_TYPE -> loadStorage<CultureId, Culture>(path, CultureId(0))
    CURRENCY_TYPE -> loadStorage<CurrencyId, Currency>(path, CurrencyId(0))
    CURRENCY_UNIT_TYPE -> loadStorage<CurrencyUnitId, CurrencyUnit>(path, CurrencyUnitId(0))
    DOMAIN_TYPE -> loadStorage<DomainId, Domain>(path, DomainId(0))
    EQUIPMENT_TYPE -> loadStorage<EquipmentId, Equipment>(path, EquipmentId(0))
    FASHION_TYPE -> loadStorage<FashionId, Fashion>(path, FashionId(0))
    FONT_TYPE -> loadStorage<FontId, Font>(path, FontId(0))
    GOD_TYPE -> loadStorage<GodId, God>(path, GodId(0))
    HOLIDAY_TYPE -> loadStorage<HolidayId, Holiday>(path, HolidayId(0))
    JOB_TYPE -> loadStorage<JobId, Job>(path, JobId(0))
    LANGUAGE_TYPE -> loadStorage<LanguageId, Language>(path, LanguageId(0))
    MATERIAL_TYPE -> loadStorage<MaterialId, Material>(path, MaterialId(0))
    MOON_TYPE -> loadStorage<MoonId, Moon>(path, MoonId(0))
    MOUNTAIN_TYPE -> loadStorage<MountainId, Mountain>(path, MountainId(0))
    NAME_LIST_TYPE -> loadStorage<NameListId, NameList>(path, NameListId(0))
    ORGANIZATION_TYPE -> loadStorage<OrganizationId, Organization>(path, OrganizationId(0))
    PANTHEON_TYPE -> loadStorage<PantheonId, Pantheon>(path, PantheonId(0))
    PERIODICAL_TYPE -> loadStorage<PeriodicalId, Periodical>(path, PeriodicalId(0))
    PERIODICAL_ISSUE_TYPE -> loadStorage<PeriodicalIssueId, PeriodicalIssue>(path, PeriodicalIssueId(0))
    PERSONALITY_TRAIT_TYPE -> loadStorage<PersonalityTraitId, PersonalityTrait>(
        path,
        PersonalityTraitId(0)
    )

    PLANE_TYPE -> loadStorage<PlaneId, Plane>(path, PlaneId(0))
    RACE_TYPE -> loadStorage<RaceId, Race>(path, RaceId(0))
    RACE_APPEARANCE_TYPE -> loadStorage<RaceAppearanceId, RaceAppearance>(path, RaceAppearanceId(0))
    RIVER_TYPE -> loadStorage<RiverId, River>(path, RiverId(0))
    SPELL_TYPE -> loadStorage<SpellId, Spell>(path, SpellId(0))
    STREET_TYPE -> loadStorage<StreetId, Street>(path, StreetId(0))
    STREET_TEMPLATE_TYPE -> loadStorage<StreetTemplateId, StreetTemplate>(path, StreetTemplateId(0))
    TEXT_TYPE -> loadStorage<TextId, Text>(path, TextId(0))
    TITLE_TYPE -> loadStorage<TitleId, Title>(path, TitleId(0))
    TOWN_TYPE -> loadStorage<TownId, Town>(path, TownId(0))
    UNIFORM_TYPE -> loadStorage<UniformId, Uniform>(path, UniformId(0))
    else -> throw IllegalArgumentException("Unknown type $type")
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> validate(
    storage: Storage<ID, ELEMENT>,
    validate: (ELEMENT) -> Unit,
) {
    storage.getAll().forEach {
        try {
            validate(it)
        } catch (e: Exception) {
            logger.error { "${storage.getType()} ${it.id().value()} is invalid: ${e.message}" }
            throw e
        }
    }
}
