package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.character.*
import at.orchaldir.gm.core.reducer.culture.*
import at.orchaldir.gm.core.reducer.economy.ECONOMY_REDUCER
import at.orchaldir.gm.core.reducer.item.ITEM_REDUCER
import at.orchaldir.gm.core.reducer.magic.MAGIC_REDUCER
import at.orchaldir.gm.core.reducer.organization.ORGANIZATION_REDUCER
import at.orchaldir.gm.core.reducer.religion.RELIGION_REDUCER
import at.orchaldir.gm.core.reducer.time.CREATE_CALENDAR
import at.orchaldir.gm.core.reducer.time.DELETE_CALENDAR
import at.orchaldir.gm.core.reducer.time.UPDATE_CALENDAR
import at.orchaldir.gm.core.reducer.time.UPDATE_TIME
import at.orchaldir.gm.core.reducer.world.WORLD_REDUCER
import at.orchaldir.gm.utils.redux.Reducer

val REDUCER: Reducer<Action, State> = { state, action ->
    when (action) {
        // meta
        is LoadData -> LOAD_DATA(state, action)
        // character
        is CreateCharacter -> CREATE_CHARACTER(state, action)
        is DeleteCharacter -> DELETE_CHARACTER(state, action)
        is UpdateCharacter -> UPDATE_CHARACTER(state, action)
        is UpdateAppearance -> UPDATE_APPEARANCE(state, action)
        is UpdateEquipmentOfCharacter -> UPDATE_EQUIPMENT_MAP(state, action)
        is UpdateRelationships -> UPDATE_RELATIONSHIPS(state, action)
        // character's languages
        is AddLanguage -> ADD_LANGUAGE(state, action)
        is RemoveLanguages -> REMOVE_LANGUAGES(state, action)
        // calendar
        is CreateCalendar -> CREATE_CALENDAR(state, action)
        is DeleteCalendar -> DELETE_CALENDAR(state, action)
        is UpdateCalendar -> UPDATE_CALENDAR(state, action)
        // culture
        is CreateCulture -> CREATE_CULTURE(state, action)
        is CloneCulture -> CLONE_CULTURE(state, action)
        is DeleteCulture -> DELETE_CULTURE(state, action)
        is UpdateCulture -> UPDATE_CULTURE(state, action)
        // fashion
        is CreateFashion -> CREATE_FASHION(state, action)
        is DeleteFashion -> DELETE_FASHION(state, action)
        is UpdateFashion -> UPDATE_FASHION(state, action)
        // font
        is CreateFont -> CREATE_FONT(state, action)
        is DeleteFont -> DELETE_FONT(state, action)
        is UpdateFont -> UPDATE_FONT(state, action)
        // holiday
        is CreateHoliday -> CREATE_HOLIDAY(state, action)
        is DeleteHoliday -> DELETE_HOLIDAY(state, action)
        is UpdateHoliday -> UPDATE_HOLIDAY(state, action)
        // language
        is CreateLanguage -> CREATE_LANGUAGE(state, action)
        is DeleteLanguage -> DELETE_LANGUAGE(state, action)
        is UpdateLanguage -> UPDATE_LANGUAGE(state, action)
        // material
        is CreateMaterial -> CREATE_MATERIAL(state, action)
        is DeleteMaterial -> DELETE_MATERIAL(state, action)
        is UpdateMaterial -> UPDATE_MATERIAL(state, action)
        // name list
        is CreateNameList -> CREATE_NAME_LIST(state, action)
        is DeleteNameList -> DELETE_NAME_LIST(state, action)
        is UpdateNameList -> UPDATE_NAME_LIST(state, action)
        // personality
        is CreatePersonalityTrait -> CREATE_PERSONALITY_TRAIT(state, action)
        is DeletePersonalityTrait -> DELETE_PERSONALITY_TRAIT(state, action)
        is UpdatePersonalityTrait -> UPDATE_PERSONALITY_TRAIT(state, action)
        // race
        is CreateRace -> CREATE_RACE(state, action)
        is CloneRace -> CLONE_RACE(state, action)
        is DeleteRace -> DELETE_RACE(state, action)
        is UpdateRace -> UPDATE_RACE(state, action)
        // race appearance
        is CreateRaceAppearance -> CREATE_RACE_APPEARANCE(state, action)
        is CloneRaceAppearance -> CLONE_RACE_APPEARANCE(state, action)
        is DeleteRaceAppearance -> DELETE_RACE_APPEARANCE(state, action)
        is UpdateRaceAppearance -> UPDATE_RACE_APPEARANCE(state, action)
        // time
        is UpdateTime -> UPDATE_TIME(state, action)
        // sub reducers
        is ItemAction -> ITEM_REDUCER(state, action)
        is EconomyAction -> ECONOMY_REDUCER(state, action)
        is MagicAction -> MAGIC_REDUCER(state, action)
        is OrganizationAction -> ORGANIZATION_REDUCER(state, action)
        is ReligionAction -> RELIGION_REDUCER(state, action)
        is WorldAction -> WORLD_REDUCER(state, action)
    }
}
