package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.loadData
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
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
    val path: String = "data",
    val storageMap: Map<ElementType, Storage<*, *>> = ElementType.entries.associateWith { it.createStorage() },
    val time: Time = Time(),
    val rarityGenerator: RarityGenerator = RarityGenerator.empty(5),
) {
    fun getCalendarStorage() = getStorage<CalendarId, Calendar>(ElementType.CALENDAR)
    fun getCharacterStorage() = getStorage<CharacterId, Character>(ElementType.CHARACTER)
    fun getCultureStorage() = getStorage<CultureId, Culture>(ElementType.CULTURE)
    fun getFashionStorage() = getStorage<FashionId, Fashion>(ElementType.FASHION)
    fun getItemTemplateStorage() = getStorage<ItemTemplateId, ItemTemplate>(ElementType.ITEM_TEMPLATE)
    fun getLanguageStorage() = getStorage<LanguageId, Language>(ElementType.LANGUAGE)
    fun getMaterialStorage() = getStorage<MaterialId, Material>(ElementType.MATERIAL)
    fun getNameListStorage() = getStorage<NameListId, NameList>(ElementType.NAME_LIST)
    fun getPersonalityTraitStorage() = getStorage<PersonalityTraitId, PersonalityTrait>(ElementType.PERSONALITY_TRAIT)
    fun getRaceStorage() = getStorage<RaceId, Race>(ElementType.RACE)

    private fun <ID : Id<ID>, ELEMENT : Element<ID>> getStorage(type: ElementType): Storage<ID, ELEMENT> {
        val storage = storageMap[type]

        if (storage != null) {
            @Suppress("UNCHECKED_CAST")
            return storage as Storage<ID, ELEMENT>
        }

        error("fail")
    }

    fun updateStorage(type: ElementType, storage: Storage<*, *>): State {
        val newMap = storageMap.toMutableMap()
        newMap[type] = storage

        return copy(storageMap = newMap)
    }

    companion object {
        fun load(path: String) = State(
            path,
            ElementType.entries.associateWith { it.loadStorage(path) },
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