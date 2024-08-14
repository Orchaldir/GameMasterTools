package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.loadData
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
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.saveData
import at.orchaldir.gm.core.saveStorage
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

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

    fun getCalendarStorage() = getStorage<CalendarId, Calendar>(CALENDAR)
    fun getCharacterStorage() = getStorage<CharacterId, Character>(CHARACTER)
    fun getCultureStorage() = getStorage<CultureId, Culture>(CULTURE)
    fun getFashionStorage() = getStorage<FashionId, Fashion>(FASHION)
    fun getItemTemplateStorage() = getStorage<ItemTemplateId, ItemTemplate>(ITEM_TEMPLATE)
    fun getLanguageStorage() = getStorage<LanguageId, Language>(LANGUAGE)
    fun getMaterialStorage() = getStorage<MaterialId, Material>(MATERIAL)
    fun getNameListStorage() = getStorage<NameListId, NameList>(NAME_LIST)
    fun getPersonalityTraitStorage() = getStorage<PersonalityTraitId, PersonalityTrait>(PERSONALITY_TRAIT)
    fun getRaceStorage() = getStorage<RaceId, Race>(RACE)

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

        error("fail")
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

        fun init(
            storage: Storage<*, *>,
            path: String = "data",
            time: Time = Time(),
            rarityGenerator: RarityGenerator = RarityGenerator.empty(5),
        ) = State(mapOf(storage.getType() to storage), path, time, rarityGenerator)

        fun load(path: String) = State(
            ELEMENTS.associateWith { loadStorage(path, it) },
            path,
            loadData(path, TIME)
        )
    }

    fun save() {
        storageMap.values.forEach {
            saveStorage(path, it)
        }
        saveData(path, TIME, time)
    }
}