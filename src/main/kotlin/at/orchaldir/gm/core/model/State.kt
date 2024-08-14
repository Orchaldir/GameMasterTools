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
    fun getCalendarStorage() = getStorage<CalendarId, Calendar>(ElementType.Calendar)
    fun getCharacterStorage() = getStorage<CharacterId, Character>(ElementType.Character)
    fun getCultureStorage() = getStorage<CultureId, Culture>(ElementType.Culture)
    fun getFashionStorage() = getStorage<FashionId, Fashion>(ElementType.Fashion)
    fun getItemTemplateStorage() = getStorage<ItemTemplateId, ItemTemplate>(ElementType.ItemTemplate)
    fun getLanguageStorage() = getStorage<LanguageId, Language>(ElementType.Language)
    fun getMaterialStorage() = getStorage<MaterialId, Material>(ElementType.Material)
    fun getNameListStorage() = getStorage<NameListId, NameList>(ElementType.NameList)
    fun getPersonalityTraitStorage() = getStorage<PersonalityTraitId, PersonalityTrait>(ElementType.PersonalityTrait)
    fun getRaceStorage() = getStorage<RaceId, Race>(ElementType.Race)

    private fun <ID : Id<ID>, ELEMENT : Element<ID>> getStorage(type: ElementType): Storage<ID, ELEMENT> {
        val storage = storageMap[type]

        if (storage != null) {
            @Suppress("UNCHECKED_CAST")
            return storage as Storage<ID, ELEMENT>
        }

        error("fail")
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