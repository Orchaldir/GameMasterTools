package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.loadData
import at.orchaldir.gm.core.loadStorage
import at.orchaldir.gm.core.model.calendar.CALENDAR
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.CULTURE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.FASHION
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.holiday.HOLIDAY
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.item.ITEM_TEMPLATE
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.LANGUAGE
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.MATERIAL
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.RACE
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RACE_APPEARANCE
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.moon.MOON
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.street.*
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.TOWN
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.saveData
import at.orchaldir.gm.core.saveStorage
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

val ELEMENTS =
    setOf(
        ARCHITECTURAL_STYLE,
        BUILDING,
        CALENDAR,
        CHARACTER,
        CULTURE,
        FASHION,
        HOLIDAY,
        ITEM_TEMPLATE,
        LANGUAGE,
        MATERIAL,
        MOON,
        MOUNTAIN,
        NAME_LIST,
        PERSONALITY_TRAIT,
        RACE,
        RACE_APPEARANCE,
        RIVER,
        STREET,
        STREET_TYPE,
        TOWN,
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

    fun getArchitecturalStyleStorage() = getStorage<ArchitecturalStyleId, ArchitecturalStyle>(BUILDING)
    fun getBuildingStorage() = getStorage<BuildingId, Building>(BUILDING)
    fun getCalendarStorage() = getStorage<CalendarId, Calendar>(CALENDAR)
    fun getCharacterStorage() = getStorage<CharacterId, Character>(CHARACTER)
    fun getCultureStorage() = getStorage<CultureId, Culture>(CULTURE)
    fun getFashionStorage() = getStorage<FashionId, Fashion>(FASHION)
    fun getHolidayStorage() = getStorage<HolidayId, Holiday>(HOLIDAY)
    fun getItemTemplateStorage() = getStorage<ItemTemplateId, ItemTemplate>(ITEM_TEMPLATE)
    fun getLanguageStorage() = getStorage<LanguageId, Language>(LANGUAGE)
    fun getMaterialStorage() = getStorage<MaterialId, Material>(MATERIAL)
    fun getMoonStorage() = getStorage<MoonId, Moon>(MOON)
    fun getMountainStorage() = getStorage<MountainId, Mountain>(MOUNTAIN)
    fun getNameListStorage() = getStorage<NameListId, NameList>(NAME_LIST)
    fun getPersonalityTraitStorage() = getStorage<PersonalityTraitId, PersonalityTrait>(PERSONALITY_TRAIT)
    fun getRaceStorage() = getStorage<RaceId, Race>(RACE)
    fun getRaceAppearanceStorage() = getStorage<RaceAppearanceId, RaceAppearance>(RACE_APPEARANCE)
    fun getRiverStorage() = getStorage<RiverId, River>(RIVER)
    fun getStreetStorage() = getStorage<StreetId, Street>(STREET)
    fun getStreetTypeStorage() = getStorage<StreetTypeId, StreetType>(STREET_TYPE)
    fun getTownStorage() = getStorage<TownId, Town>(TOWN)

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
            return (storage as Storage<ID, Element<ID>>).get(id)?.name() ?: "Unknown"
        }

        return "Unknown"
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
        saveStorage(path, getCalendarStorage())
        saveStorage(path, getCharacterStorage())
        saveStorage(path, getCultureStorage())
        saveStorage(path, getFashionStorage())
        saveStorage(path, getHolidayStorage())
        saveStorage(path, getItemTemplateStorage())
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
        saveStorage(path, getStreetTypeStorage())
        saveStorage(path, getTownStorage())
        saveData(path, TIME, time)
    }
}

fun createStorage(type: String) = when (type) {
    ARCHITECTURAL_STYLE -> Storage(ArchitecturalStyleId(0))
    BUILDING -> Storage(BuildingId(0))
    CALENDAR -> Storage(CalendarId(0))
    CHARACTER -> Storage(CharacterId(0))
    CULTURE -> Storage(CultureId(0))
    FASHION -> Storage(FashionId(0))
    HOLIDAY -> Storage(HolidayId(0))
    ITEM_TEMPLATE -> Storage(ItemTemplateId(0))
    LANGUAGE -> Storage(LanguageId(0))
    MATERIAL -> Storage(MaterialId(0))
    MOON -> Storage(MoonId(0))
    MOUNTAIN -> Storage(MountainId(0))
    NAME_LIST -> Storage(NameListId(0))
    PERSONALITY_TRAIT -> Storage(PersonalityTraitId(0))
    RACE -> Storage(RaceId(0))
    RACE_APPEARANCE -> Storage(RaceAppearanceId(0))
    RIVER -> Storage(RiverId(0))
    STREET -> Storage(StreetId(0))
    STREET_TYPE -> Storage(StreetTypeId(0))
    TOWN -> Storage(TownId(0))
    else -> throw IllegalArgumentException("Unknown type $type")
}

fun loadStorageForType(path: String, type: String): Storage<*, *> = when (type) {
    ARCHITECTURAL_STYLE -> loadStorage<ArchitecturalStyleId, ArchitecturalStyle>(path, ArchitecturalStyleId(0))
    BUILDING -> loadStorage<BuildingId, Building>(path, BuildingId(0))
    CALENDAR -> loadStorage<CalendarId, Calendar>(path, CalendarId(0))
    CHARACTER -> loadStorage<CharacterId, Character>(path, CharacterId(0))
    CULTURE -> loadStorage<CultureId, Culture>(path, CultureId(0))
    FASHION -> loadStorage<FashionId, Fashion>(path, FashionId(0))
    HOLIDAY -> loadStorage<HolidayId, Holiday>(path, HolidayId(0))
    ITEM_TEMPLATE -> loadStorage<ItemTemplateId, ItemTemplate>(path, ItemTemplateId(0))
    LANGUAGE -> loadStorage<LanguageId, Language>(path, LanguageId(0))
    MATERIAL -> loadStorage<MaterialId, Material>(path, MaterialId(0))
    MOON -> loadStorage<MoonId, Moon>(path, MoonId(0))
    MOUNTAIN -> loadStorage<MountainId, Mountain>(path, MountainId(0))
    NAME_LIST -> loadStorage<NameListId, NameList>(path, NameListId(0))
    PERSONALITY_TRAIT -> loadStorage<PersonalityTraitId, PersonalityTrait>(
        path,
        PersonalityTraitId(0)
    )

    RACE -> loadStorage<RaceId, Race>(path, RaceId(0))
    RACE_APPEARANCE -> loadStorage<RaceAppearanceId, RaceAppearance>(path, RaceAppearanceId(0))
    RIVER -> loadStorage<RiverId, River>(path, RiverId(0))
    STREET -> loadStorage<StreetId, Street>(path, StreetId(0))
    STREET_TYPE -> loadStorage<StreetTypeId, StreetType>(path, StreetTypeId(0))
    TOWN -> loadStorage<TownId, Town>(path, TownId(0))
    else -> throw IllegalArgumentException("Unknown type $type")
}
