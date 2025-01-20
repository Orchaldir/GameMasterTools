package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.loadData
import at.orchaldir.gm.core.loadStorage
import at.orchaldir.gm.core.model.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.CULTURE_TYPE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.business.BUSINESS_TYPE
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JOB_TYPE
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.fashion.FASHION_TYPE
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.font.FONT_TYPE
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.holiday.HOLIDAY_TYPE
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.item.ITEM_TEMPLATE_TYPE
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.item.text.TEXT_TYPE
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.language.LANGUAGE_TYPE
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.MATERIAL_TYPE
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.name.NAME_LIST_TYPE
import at.orchaldir.gm.core.model.name.NameList
import at.orchaldir.gm.core.model.name.NameListId
import at.orchaldir.gm.core.model.race.RACE_TYPE
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RACE_APPEARANCE_TYPE
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.moon.MOON_TYPE
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.street.*
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.TOWN_TYPE
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.saveData
import at.orchaldir.gm.core.saveStorage
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

val ELEMENTS =
    setOf(
        ARCHITECTURAL_STYLE_TYPE,
        BUILDING_TYPE,
        BUSINESS_TYPE,
        CALENDAR_TYPE,
        CHARACTER_TYPE,
        CULTURE_TYPE,
        FASHION_TYPE,
        FONT_TYPE,
        HOLIDAY_TYPE,
        ITEM_TEMPLATE_TYPE,
        JOB_TYPE,
        LANGUAGE_TYPE,
        MATERIAL_TYPE,
        MOON_TYPE,
        MOUNTAIN_TYPE,
        NAME_LIST_TYPE,
        PERSONALITY_TRAIT_TYPE,
        RACE_TYPE,
        RACE_APPEARANCE_TYPE,
        RIVER_TYPE,
        STREET_TYPE,
        STREET_TEMPLATE_TYPE,
        TEXT_TYPE,
        TOWN_TYPE,
    )
private const val TIME = "Time"

data class State(
    val storageMap: Map<String, Storage<*, *>> = emptyMap(),
    val path: String = "data",
    val time: Time = Time(),
    val rarityGenerator: RarityGenerator = RarityGenerator.empty(5),
) {
    constructor(
        storage: Storage<*, *>,
        path: String = "data",
        time: Time = Time(),
        rarityGenerator: RarityGenerator = RarityGenerator.empty(5),
    ) : this(mapOf(storage.getType() to storage), path, time, rarityGenerator)

    constructor(
        storageList: List<Storage<*, *>>,
        path: String = "data",
        time: Time = Time(),
        rarityGenerator: RarityGenerator = RarityGenerator.empty(5),
    ) : this(storageList.associateBy { it.getType() }, path, time, rarityGenerator)

    fun getArchitecturalStyleStorage() = getStorage<ArchitecturalStyleId, ArchitecturalStyle>(ARCHITECTURAL_STYLE_TYPE)
    fun getBuildingStorage() = getStorage<BuildingId, Building>(BUILDING_TYPE)
    fun getBusinessStorage() = getStorage<BusinessId, Business>(BUSINESS_TYPE)
    fun getCalendarStorage() = getStorage<CalendarId, Calendar>(CALENDAR_TYPE)
    fun getCharacterStorage() = getStorage<CharacterId, Character>(CHARACTER_TYPE)
    fun getCultureStorage() = getStorage<CultureId, Culture>(CULTURE_TYPE)
    fun getFashionStorage() = getStorage<FashionId, Fashion>(FASHION_TYPE)
    fun getFontStorage() = getStorage<FontId, Font>(FONT_TYPE)
    fun getHolidayStorage() = getStorage<HolidayId, Holiday>(HOLIDAY_TYPE)
    fun getItemTemplateStorage() = getStorage<ItemTemplateId, ItemTemplate>(ITEM_TEMPLATE_TYPE)
    fun getJobStorage() = getStorage<JobId, Job>(JOB_TYPE)
    fun getLanguageStorage() = getStorage<LanguageId, Language>(LANGUAGE_TYPE)
    fun getMaterialStorage() = getStorage<MaterialId, Material>(MATERIAL_TYPE)
    fun getMoonStorage() = getStorage<MoonId, Moon>(MOON_TYPE)
    fun getMountainStorage() = getStorage<MountainId, Mountain>(MOUNTAIN_TYPE)
    fun getNameListStorage() = getStorage<NameListId, NameList>(NAME_LIST_TYPE)
    fun getPersonalityTraitStorage() = getStorage<PersonalityTraitId, PersonalityTrait>(PERSONALITY_TRAIT_TYPE)
    fun getRaceStorage() = getStorage<RaceId, Race>(RACE_TYPE)
    fun getRaceAppearanceStorage() = getStorage<RaceAppearanceId, RaceAppearance>(RACE_APPEARANCE_TYPE)
    fun getRiverStorage() = getStorage<RiverId, River>(RIVER_TYPE)
    fun getStreetStorage() = getStorage<StreetId, Street>(STREET_TYPE)
    fun getStreetTemplateStorage() = getStorage<StreetTemplateId, StreetTemplate>(STREET_TEMPLATE_TYPE)
    fun getTextStorage() = getStorage<TextId, Text>(TEXT_TYPE)
    fun getTownStorage() = getStorage<TownId, Town>(TOWN_TYPE)

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

        fun load(path: String) = State(
            ELEMENTS.associateWith { loadStorageForType(path, it) },
            path,
            loadData(path, TIME)
        )
    }

    fun save() {
        saveStorage(path, getArchitecturalStyleStorage())
        saveStorage(path, getBuildingStorage())
        saveStorage(path, getBusinessStorage())
        saveStorage(path, getCalendarStorage())
        saveStorage(path, getCharacterStorage())
        saveStorage(path, getCultureStorage())
        saveStorage(path, getFashionStorage())
        saveStorage(path, getFontStorage())
        saveStorage(path, getHolidayStorage())
        saveStorage(path, getItemTemplateStorage())
        saveStorage(path, getJobStorage())
        saveStorage(path, getLanguageStorage())
        saveStorage(path, getMaterialStorage())
        saveStorage(path, getMoonStorage())
        saveStorage(path, getMountainStorage())
        saveStorage(path, getNameListStorage())
        saveStorage(path, getPersonalityTraitStorage())
        saveStorage(path, getRaceStorage())
        saveStorage(path, getRaceAppearanceStorage())
        saveStorage(path, getRiverStorage())
        saveStorage(path, getStreetStorage())
        saveStorage(path, getStreetTemplateStorage())
        saveStorage(path, getTextStorage())
        saveStorage(path, getTownStorage())
        saveData(path, TIME, time)
    }
}

fun createStorage(type: String) = when (type) {
    ARCHITECTURAL_STYLE_TYPE -> Storage(ArchitecturalStyleId(0))
    BUILDING_TYPE -> Storage(BuildingId(0))
    BUSINESS_TYPE -> Storage(BusinessId(0))
    CALENDAR_TYPE -> Storage(CalendarId(0))
    CHARACTER_TYPE -> Storage(CharacterId(0))
    CULTURE_TYPE -> Storage(CultureId(0))
    FASHION_TYPE -> Storage(FashionId(0))
    FONT_TYPE -> Storage(FontId(0))
    HOLIDAY_TYPE -> Storage(HolidayId(0))
    ITEM_TEMPLATE_TYPE -> Storage(ItemTemplateId(0))
    JOB_TYPE -> Storage(JobId(0))
    LANGUAGE_TYPE -> Storage(LanguageId(0))
    MATERIAL_TYPE -> Storage(MaterialId(0))
    MOON_TYPE -> Storage(MoonId(0))
    MOUNTAIN_TYPE -> Storage(MountainId(0))
    NAME_LIST_TYPE -> Storage(NameListId(0))
    PERSONALITY_TRAIT_TYPE -> Storage(PersonalityTraitId(0))
    RACE_TYPE -> Storage(RaceId(0))
    RACE_APPEARANCE_TYPE -> Storage(RaceAppearanceId(0))
    RIVER_TYPE -> Storage(RiverId(0))
    STREET_TYPE -> Storage(StreetId(0))
    STREET_TEMPLATE_TYPE -> Storage(StreetTemplateId(0))
    TEXT_TYPE -> Storage(TextId(0))
    TOWN_TYPE -> Storage(TownId(0))
    else -> throw IllegalArgumentException("Unknown type $type")
}

fun loadStorageForType(path: String, type: String): Storage<*, *> = when (type) {
    ARCHITECTURAL_STYLE_TYPE -> loadStorage<ArchitecturalStyleId, ArchitecturalStyle>(path, ArchitecturalStyleId(0))
    BUILDING_TYPE -> loadStorage<BuildingId, Building>(path, BuildingId(0))
    BUSINESS_TYPE -> loadStorage<BusinessId, Business>(path, BusinessId(0))
    CALENDAR_TYPE -> loadStorage<CalendarId, Calendar>(path, CalendarId(0))
    CHARACTER_TYPE -> loadStorage<CharacterId, Character>(path, CharacterId(0))
    CULTURE_TYPE -> loadStorage<CultureId, Culture>(path, CultureId(0))
    FASHION_TYPE -> loadStorage<FashionId, Fashion>(path, FashionId(0))
    FONT_TYPE -> loadStorage<FontId, Font>(path, FontId(0))
    HOLIDAY_TYPE -> loadStorage<HolidayId, Holiday>(path, HolidayId(0))
    ITEM_TEMPLATE_TYPE -> loadStorage<ItemTemplateId, ItemTemplate>(path, ItemTemplateId(0))
    JOB_TYPE -> loadStorage<JobId, Job>(path, JobId(0))
    LANGUAGE_TYPE -> loadStorage<LanguageId, Language>(path, LanguageId(0))
    MATERIAL_TYPE -> loadStorage<MaterialId, Material>(path, MaterialId(0))
    MOON_TYPE -> loadStorage<MoonId, Moon>(path, MoonId(0))
    MOUNTAIN_TYPE -> loadStorage<MountainId, Mountain>(path, MountainId(0))
    NAME_LIST_TYPE -> loadStorage<NameListId, NameList>(path, NameListId(0))
    PERSONALITY_TRAIT_TYPE -> loadStorage<PersonalityTraitId, PersonalityTrait>(
        path,
        PersonalityTraitId(0)
    )

    RACE_TYPE -> loadStorage<RaceId, Race>(path, RaceId(0))
    RACE_APPEARANCE_TYPE -> loadStorage<RaceAppearanceId, RaceAppearance>(path, RaceAppearanceId(0))
    RIVER_TYPE -> loadStorage<RiverId, River>(path, RiverId(0))
    STREET_TYPE -> loadStorage<StreetId, Street>(path, StreetId(0))
    STREET_TEMPLATE_TYPE -> loadStorage<StreetTemplateId, StreetTemplate>(path, StreetTemplateId(0))
    TEXT_TYPE -> loadStorage<TextId, Text>(path, TextId(0))
    TOWN_TYPE -> loadStorage<TownId, Town>(path, TownId(0))
    else -> throw IllegalArgumentException("Unknown type $type")
}
