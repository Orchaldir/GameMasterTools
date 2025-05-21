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
import at.orchaldir.gm.core.model.culture.language.LANGUAGE_TYPE
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.economy.business.BUSINESS_TYPE
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JOB_TYPE
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.economy.material.MATERIAL_TYPE
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.illness.ILLNESS_TYPE
import at.orchaldir.gm.core.model.illness.Illness
import at.orchaldir.gm.core.model.illness.IllnessId
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
import at.orchaldir.gm.core.model.magic.*
import at.orchaldir.gm.core.model.organization.ORGANIZATION_TYPE
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RACE_TYPE
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RACE_APPEARANCE_TYPE
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.*
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.holiday.HOLIDAY_TYPE
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.font.FONT_TYPE
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.name.NAME_LIST_TYPE
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.util.quote.QUOTE_TYPE
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.source.DATA_SOURCE_TYPE
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.moon.MOON_TYPE
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.plane.PLANE_TYPE
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.street.*
import at.orchaldir.gm.core.model.world.region.*
import at.orchaldir.gm.core.model.world.town.TOWN_MAP_TYPE
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.reducer.character.validateCharacter
import at.orchaldir.gm.core.reducer.culture.validateCulture
import at.orchaldir.gm.core.reducer.culture.validateFashion
import at.orchaldir.gm.core.reducer.culture.validateLanguage
import at.orchaldir.gm.core.reducer.economy.validateBusiness
import at.orchaldir.gm.core.reducer.economy.validateCurrency
import at.orchaldir.gm.core.reducer.economy.validateCurrencyUnit
import at.orchaldir.gm.core.reducer.economy.validateJob
import at.orchaldir.gm.core.reducer.illness.validateIllness
import at.orchaldir.gm.core.reducer.item.periodical.validateArticle
import at.orchaldir.gm.core.reducer.item.periodical.validatePeriodical
import at.orchaldir.gm.core.reducer.item.periodical.validatePeriodicalIssue
import at.orchaldir.gm.core.reducer.item.validateEquipment
import at.orchaldir.gm.core.reducer.item.validateText
import at.orchaldir.gm.core.reducer.item.validateUniform
import at.orchaldir.gm.core.reducer.magic.validateMagicTradition
import at.orchaldir.gm.core.reducer.magic.validateSpell
import at.orchaldir.gm.core.reducer.magic.validateSpellGroup
import at.orchaldir.gm.core.reducer.organization.validateOrganization
import at.orchaldir.gm.core.reducer.quote.validateQuote
import at.orchaldir.gm.core.reducer.race.validateRace
import at.orchaldir.gm.core.reducer.race.validateRaceAppearance
import at.orchaldir.gm.core.reducer.realm.*
import at.orchaldir.gm.core.reducer.religion.validateDomain
import at.orchaldir.gm.core.reducer.religion.validateGod
import at.orchaldir.gm.core.reducer.religion.validatePantheon
import at.orchaldir.gm.core.reducer.time.validateCalendar
import at.orchaldir.gm.core.reducer.time.validateHoliday
import at.orchaldir.gm.core.reducer.util.validateFont
import at.orchaldir.gm.core.reducer.validateData
import at.orchaldir.gm.core.reducer.world.*
import at.orchaldir.gm.core.reducer.world.town.validateTownMap
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
        BATTLE_TYPE,
        BUILDING_TYPE,
        BUSINESS_TYPE,
        CALENDAR_TYPE,
        CATASTROPHE_TYPE,
        CHARACTER_TYPE,
        CULTURE_TYPE,
        CURRENCY_TYPE,
        CURRENCY_UNIT_TYPE,
        DATA_SOURCE_TYPE,
        DOMAIN_TYPE,
        EQUIPMENT_TYPE,
        FASHION_TYPE,
        FONT_TYPE,
        GOD_TYPE,
        HOLIDAY_TYPE,
        ILLNESS_TYPE,
        JOB_TYPE,
        LANGUAGE_TYPE,
        LEGAL_CODE_TYPE,
        MAGIC_TRADITION_TYPE,
        MATERIAL_TYPE,
        MOON_TYPE,
        NAME_LIST_TYPE,
        ORGANIZATION_TYPE,
        PANTHEON_TYPE,
        PERIODICAL_TYPE,
        PERIODICAL_ISSUE_TYPE,
        PERSONALITY_TRAIT_TYPE,
        PLANE_TYPE,
        QUOTE_TYPE,
        RACE_TYPE,
        RACE_APPEARANCE_TYPE,
        REALM_TYPE,
        REGION_TYPE,
        RIVER_TYPE,
        SPELL_TYPE,
        SPELL_GROUP_TYPE,
        STREET_TYPE,
        STREET_TEMPLATE_TYPE,
        TEXT_TYPE,
        TITLE_TYPE,
        TOWN_TYPE,
        TOWN_MAP_TYPE,
        TREATY_TYPE,
        UNIFORM_TYPE,
        WAR_TYPE,
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
    fun getBattleStorage() = getStorage<BattleId, Battle>(BATTLE_TYPE)
    fun getBuildingStorage() = getStorage<BuildingId, Building>(BUILDING_TYPE)
    fun getBusinessStorage() = getStorage<BusinessId, Business>(BUSINESS_TYPE)
    fun getCalendarStorage() = getStorage<CalendarId, Calendar>(CALENDAR_TYPE)
    fun getCatastropheStorage() = getStorage<CatastropheId, Catastrophe>(CATASTROPHE_TYPE)
    fun getCharacterStorage() = getStorage<CharacterId, Character>(CHARACTER_TYPE)
    fun getCultureStorage() = getStorage<CultureId, Culture>(CULTURE_TYPE)
    fun getCurrencyStorage() = getStorage<CurrencyId, Currency>(CURRENCY_TYPE)
    fun getCurrencyUnitStorage() = getStorage<CurrencyUnitId, CurrencyUnit>(CURRENCY_UNIT_TYPE)
    fun getDataSourceStorage() = getStorage<DataSourceId, DataSource>(DATA_SOURCE_TYPE)
    fun getDomainStorage() = getStorage<DomainId, Domain>(DOMAIN_TYPE)
    fun getEquipmentStorage() = getStorage<EquipmentId, Equipment>(EQUIPMENT_TYPE)
    fun getFashionStorage() = getStorage<FashionId, Fashion>(FASHION_TYPE)
    fun getFontStorage() = getStorage<FontId, Font>(FONT_TYPE)
    fun getGodStorage() = getStorage<GodId, God>(GOD_TYPE)
    fun getHolidayStorage() = getStorage<HolidayId, Holiday>(HOLIDAY_TYPE)
    fun getIllnessStorage() = getStorage<IllnessId, Illness>(ILLNESS_TYPE)
    fun getJobStorage() = getStorage<JobId, Job>(JOB_TYPE)
    fun getLanguageStorage() = getStorage<LanguageId, Language>(LANGUAGE_TYPE)
    fun getLegalCodeStorage() = getStorage<LegalCodeId, LegalCode>(LEGAL_CODE_TYPE)
    fun getMagicTraditionStorage() = getStorage<MagicTraditionId, MagicTradition>(MAGIC_TRADITION_TYPE)
    fun getMaterialStorage() = getStorage<MaterialId, Material>(MATERIAL_TYPE)
    fun getMoonStorage() = getStorage<MoonId, Moon>(MOON_TYPE)
    fun getNameListStorage() = getStorage<NameListId, NameList>(NAME_LIST_TYPE)
    fun getOrganizationStorage() = getStorage<OrganizationId, Organization>(ORGANIZATION_TYPE)
    fun getPantheonStorage() = getStorage<PantheonId, Pantheon>(PANTHEON_TYPE)
    fun getPeriodicalStorage() = getStorage<PeriodicalId, Periodical>(PERIODICAL_TYPE)
    fun getPeriodicalIssueStorage() = getStorage<PeriodicalIssueId, PeriodicalIssue>(PERIODICAL_ISSUE_TYPE)
    fun getPersonalityTraitStorage() = getStorage<PersonalityTraitId, PersonalityTrait>(PERSONALITY_TRAIT_TYPE)
    fun getPlaneStorage() = getStorage<PlaneId, Plane>(PLANE_TYPE)
    fun getQuoteStorage() = getStorage<QuoteId, Quote>(QUOTE_TYPE)
    fun getRaceStorage() = getStorage<RaceId, Race>(RACE_TYPE)
    fun getRaceAppearanceStorage() = getStorage<RaceAppearanceId, RaceAppearance>(RACE_APPEARANCE_TYPE)
    fun getRealmStorage() = getStorage<RealmId, Realm>(REALM_TYPE)
    fun getRegionStorage() = getStorage<RegionId, Region>(REGION_TYPE)
    fun getRiverStorage() = getStorage<RiverId, River>(RIVER_TYPE)
    fun getSpellStorage() = getStorage<SpellId, Spell>(SPELL_TYPE)
    fun getSpellGroupStorage() = getStorage<SpellGroupId, SpellGroup>(SPELL_GROUP_TYPE)
    fun getStreetStorage() = getStorage<StreetId, Street>(STREET_TYPE)
    fun getStreetTemplateStorage() = getStorage<StreetTemplateId, StreetTemplate>(STREET_TEMPLATE_TYPE)
    fun getTextStorage() = getStorage<TextId, Text>(TEXT_TYPE)
    fun getTitleStorage() = getStorage<TitleId, Title>(TITLE_TYPE)
    fun getTownStorage() = getStorage<TownId, Town>(TOWN_TYPE)
    fun getTownMapStorage() = getStorage<TownMapId, TownMap>(TOWN_MAP_TYPE)
    fun getTreatyStorage() = getStorage<TreatyId, Treaty>(TREATY_TYPE)
    fun getUniformStorage() = getStorage<UniformId, Uniform>(UNIFORM_TYPE)
    fun getWarStorage() = getStorage<WarId, War>(WAR_TYPE)

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
        validate(getBattleStorage()) { validateBattle(this, it) }
        validate(getBuildingStorage()) { validateBuilding(this, it) }
        validate(getBusinessStorage()) { validateBusiness(this, it) }
        validate(getCalendarStorage()) { validateCalendar(this, it) }
        validate(getCatastropheStorage()) { validateCatastrophe(this, it) }
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
        validate(getIllnessStorage()) { validateIllness(this, it) }
        validate(getJobStorage()) { validateJob(this, it) }
        validate(getLanguageStorage()) { validateLanguage(this, it) }
        validate(getLegalCodeStorage()) { validateLegalCode(this, it) }
        validate(getMagicTraditionStorage()) { validateMagicTradition(this, it) }
        validate(getMoonStorage()) { validateMoon(this, it) }
        validate(getOrganizationStorage()) { validateOrganization(this, it) }
        validate(getPantheonStorage()) { validatePantheon(this, it) }
        validate(getPeriodicalStorage()) { validatePeriodical(this, it) }
        validate(getPeriodicalIssueStorage()) { validatePeriodicalIssue(this, it) }
        validate(getPlaneStorage()) { validatePlane(this, it) }
        validate(getQuoteStorage()) { validateQuote(this, it) }
        validate(getRaceStorage()) { validateRace(this, it) }
        validate(getRaceAppearanceStorage()) { validateRaceAppearance(it) }
        validate(getRealmStorage()) { validateRealm(this, it) }
        validate(getRegionStorage()) { validateRegion(this, it) }
        validate(getSpellStorage()) { validateSpell(this, it) }
        validate(getSpellGroupStorage()) { validateSpellGroup(this, it) }
        validate(getStreetTemplateStorage()) { validateStreetTemplate(this, it) }
        validate(getTextStorage()) { validateText(this, it) }
        validate(getTownStorage()) { validateTown(this, it) }
        validate(getTownMapStorage()) { validateTownMap(this, it) }
        validate(getTreatyStorage()) { validateTreaty(this, it) }
        validate(getUniformStorage()) { validateUniform(this, it) }
        validate(getWarStorage()) { validateWar(this, it) }

        validateData(this, data)
    }

    fun save() {
        saveStorage(path, getArchitecturalStyleStorage())
        saveStorage(path, getArticleStorage())
        saveStorage(path, getBattleStorage())
        saveStorage(path, getBuildingStorage())
        saveStorage(path, getBusinessStorage())
        saveStorage(path, getCalendarStorage())
        saveStorage(path, getCatastropheStorage())
        saveStorage(path, getCharacterStorage())
        saveStorage(path, getCultureStorage())
        saveStorage(path, getCurrencyStorage())
        saveStorage(path, getCurrencyUnitStorage())
        saveStorage(path, getDataSourceStorage())
        saveStorage(path, getDomainStorage())
        saveStorage(path, getEquipmentStorage())
        saveStorage(path, getFashionStorage())
        saveStorage(path, getFontStorage())
        saveStorage(path, getGodStorage())
        saveStorage(path, getHolidayStorage())
        saveStorage(path, getIllnessStorage())
        saveStorage(path, getJobStorage())
        saveStorage(path, getLanguageStorage())
        saveStorage(path, getLegalCodeStorage())
        saveStorage(path, getMagicTraditionStorage())
        saveStorage(path, getMaterialStorage())
        saveStorage(path, getMoonStorage())
        saveStorage(path, getNameListStorage())
        saveStorage(path, getOrganizationStorage())
        saveStorage(path, getPantheonStorage())
        saveStorage(path, getPeriodicalStorage())
        saveStorage(path, getPeriodicalIssueStorage())
        saveStorage(path, getPersonalityTraitStorage())
        saveStorage(path, getPlaneStorage())
        saveStorage(path, getQuoteStorage())
        saveStorage(path, getRaceStorage())
        saveStorage(path, getRaceAppearanceStorage())
        saveStorage(path, getRealmStorage())
        saveStorage(path, getRegionStorage())
        saveStorage(path, getRiverStorage())
        saveStorage(path, getSpellStorage())
        saveStorage(path, getSpellGroupStorage())
        saveStorage(path, getStreetStorage())
        saveStorage(path, getStreetTemplateStorage())
        saveStorage(path, getTextStorage())
        saveStorage(path, getTitleStorage())
        saveStorage(path, getTownStorage())
        saveStorage(path, getTownMapStorage())
        saveStorage(path, getTreatyStorage())
        saveStorage(path, getUniformStorage())
        saveStorage(path, getWarStorage())
        save(path, DATA, data)
    }
}

fun createStorage(type: String) = when (type) {
    ARCHITECTURAL_STYLE_TYPE -> Storage(ArchitecturalStyleId(0))
    ARTICLE_TYPE -> Storage(ArticleId(0))
    BATTLE_TYPE -> Storage(BattleId(0))
    BUILDING_TYPE -> Storage(BuildingId(0))
    BUSINESS_TYPE -> Storage(BusinessId(0))
    CALENDAR_TYPE -> Storage(CalendarId(0))
    CATASTROPHE_TYPE -> Storage(CatastropheId(0))
    CHARACTER_TYPE -> Storage(CharacterId(0))
    CULTURE_TYPE -> Storage(CultureId(0))
    CURRENCY_TYPE -> Storage(CurrencyId(0))
    CURRENCY_UNIT_TYPE -> Storage(CurrencyUnitId(0))
    DATA_SOURCE_TYPE -> Storage(DataSourceId(0))
    DOMAIN_TYPE -> Storage(DomainId(0))
    EQUIPMENT_TYPE -> Storage(EquipmentId(0))
    FASHION_TYPE -> Storage(FashionId(0))
    FONT_TYPE -> Storage(FontId(0))
    GOD_TYPE -> Storage(GodId(0))
    HOLIDAY_TYPE -> Storage(HolidayId(0))
    ILLNESS_TYPE -> Storage(IllnessId(0))
    JOB_TYPE -> Storage(JobId(0))
    LANGUAGE_TYPE -> Storage(LanguageId(0))
    LEGAL_CODE_TYPE -> Storage(LegalCodeId(0))
    MAGIC_TRADITION_TYPE -> Storage(MagicTraditionId(0))
    MATERIAL_TYPE -> Storage(MaterialId(0))
    MOON_TYPE -> Storage(MoonId(0))
    NAME_LIST_TYPE -> Storage(NameListId(0))
    ORGANIZATION_TYPE -> Storage(OrganizationId(0))
    PANTHEON_TYPE -> Storage(PantheonId(0))
    PERIODICAL_TYPE -> Storage(PeriodicalId(0))
    PERIODICAL_ISSUE_TYPE -> Storage(PeriodicalIssueId(0))
    PERSONALITY_TRAIT_TYPE -> Storage(PersonalityTraitId(0))
    PLANE_TYPE -> Storage(PlaneId(0))
    QUOTE_TYPE -> Storage(QuoteId(0))
    RACE_TYPE -> Storage(RaceId(0))
    RACE_APPEARANCE_TYPE -> Storage(RaceAppearanceId(0))
    REALM_TYPE -> Storage(RealmId(0))
    REGION_TYPE -> Storage(RegionId(0))
    RIVER_TYPE -> Storage(RiverId(0))
    SPELL_TYPE -> Storage(SpellId(0))
    SPELL_GROUP_TYPE -> Storage(SpellGroupId(0))
    STREET_TYPE -> Storage(StreetId(0))
    STREET_TEMPLATE_TYPE -> Storage(StreetTemplateId(0))
    TEXT_TYPE -> Storage(TextId(0))
    TITLE_TYPE -> Storage(TitleId(0))
    TOWN_TYPE -> Storage(TownId(0))
    TOWN_MAP_TYPE -> Storage(TownMapId(0))
    TREATY_TYPE -> Storage(TreatyId(0))
    UNIFORM_TYPE -> Storage(UniformId(0))
    WAR_TYPE -> Storage(WarId(0))
    else -> throw IllegalArgumentException("Unknown type $type")
}

fun loadStorageForType(path: String, type: String): Storage<*, *> = when (type) {
    ARCHITECTURAL_STYLE_TYPE -> loadStorage<ArchitecturalStyleId, ArchitecturalStyle>(path, ArchitecturalStyleId(0))
    ARTICLE_TYPE -> loadStorage<ArticleId, Article>(path, ArticleId(0))
    BATTLE_TYPE -> loadStorage<BattleId, Battle>(path, BattleId(0))
    BUILDING_TYPE -> loadStorage<BuildingId, Building>(path, BuildingId(0))
    BUSINESS_TYPE -> loadStorage<BusinessId, Business>(path, BusinessId(0))
    CALENDAR_TYPE -> loadStorage<CalendarId, Calendar>(path, CalendarId(0))
    CATASTROPHE_TYPE -> loadStorage<CatastropheId, Catastrophe>(path, CatastropheId(0))
    CHARACTER_TYPE -> loadStorage<CharacterId, Character>(path, CharacterId(0))
    CULTURE_TYPE -> loadStorage<CultureId, Culture>(path, CultureId(0))
    CURRENCY_TYPE -> loadStorage<CurrencyId, Currency>(path, CurrencyId(0))
    CURRENCY_UNIT_TYPE -> loadStorage<CurrencyUnitId, CurrencyUnit>(path, CurrencyUnitId(0))
    DATA_SOURCE_TYPE -> loadStorage<DataSourceId, DataSource>(path, DataSourceId(0))
    DOMAIN_TYPE -> loadStorage<DomainId, Domain>(path, DomainId(0))
    EQUIPMENT_TYPE -> loadStorage<EquipmentId, Equipment>(path, EquipmentId(0))
    FASHION_TYPE -> loadStorage<FashionId, Fashion>(path, FashionId(0))
    FONT_TYPE -> loadStorage<FontId, Font>(path, FontId(0))
    GOD_TYPE -> loadStorage<GodId, God>(path, GodId(0))
    HOLIDAY_TYPE -> loadStorage<HolidayId, Holiday>(path, HolidayId(0))
    ILLNESS_TYPE -> loadStorage<IllnessId, Illness>(path, IllnessId(0))
    JOB_TYPE -> loadStorage<JobId, Job>(path, JobId(0))
    LANGUAGE_TYPE -> loadStorage<LanguageId, Language>(path, LanguageId(0))
    LEGAL_CODE_TYPE -> loadStorage<LegalCodeId, LegalCode>(path, LegalCodeId(0))
    MAGIC_TRADITION_TYPE -> loadStorage<MagicTraditionId, MagicTradition>(path, MagicTraditionId(0))
    MATERIAL_TYPE -> loadStorage<MaterialId, Material>(path, MaterialId(0))
    MOON_TYPE -> loadStorage<MoonId, Moon>(path, MoonId(0))
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
    QUOTE_TYPE -> loadStorage<QuoteId, Quote>(path, QuoteId(0))
    RACE_TYPE -> loadStorage<RaceId, Race>(path, RaceId(0))
    RACE_APPEARANCE_TYPE -> loadStorage<RaceAppearanceId, RaceAppearance>(path, RaceAppearanceId(0))
    REALM_TYPE -> loadStorage<RealmId, Realm>(path, RealmId(0))
    REGION_TYPE -> loadStorage<RegionId, Region>(path, RegionId(0))
    RIVER_TYPE -> loadStorage<RiverId, River>(path, RiverId(0))
    SPELL_TYPE -> loadStorage<SpellId, Spell>(path, SpellId(0))
    SPELL_GROUP_TYPE -> loadStorage<SpellGroupId, SpellGroup>(path, SpellGroupId(0))
    STREET_TYPE -> loadStorage<StreetId, Street>(path, StreetId(0))
    STREET_TEMPLATE_TYPE -> loadStorage<StreetTemplateId, StreetTemplate>(path, StreetTemplateId(0))
    TEXT_TYPE -> loadStorage<TextId, Text>(path, TextId(0))
    TITLE_TYPE -> loadStorage<TitleId, Title>(path, TitleId(0))
    TOWN_TYPE -> loadStorage<TownId, Town>(path, TownId(0))
    TOWN_MAP_TYPE -> loadStorage<TownMapId, TownMap>(path, TownMapId(0))
    TREATY_TYPE -> loadStorage<TreatyId, Treaty>(path, TreatyId(0))
    UNIFORM_TYPE -> loadStorage<UniformId, Uniform>(path, UniformId(0))
    WAR_TYPE -> loadStorage<WarId, War>(path, WarId(0))
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
