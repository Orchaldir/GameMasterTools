package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.character.*
import at.orchaldir.gm.core.reducer.world.CREATE_MOON
import at.orchaldir.gm.core.reducer.world.DELETE_MOON
import at.orchaldir.gm.core.reducer.world.UPDATE_MOON
import at.orchaldir.gm.core.reducer.world.WORLD_REDUCER
import at.orchaldir.gm.utils.redux.Reducer

val REDUCER: Reducer<Action, State> = { state, action ->
    when (action) {
        // character
        is CreateCharacter -> CREATE_CHARACTER(state, action)
        is DeleteCharacter -> DELETE_CHARACTER(state, action)
        is UpdateCharacter -> UPDATE_CHARACTER(state, action)
        is UpdateAppearance -> UPDATE_APPEARANCE(state, action)
        is UpdateEquipment -> UPDATE_EQUIPMENT(state, action)
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
        is DeleteCulture -> DELETE_CULTURE(state, action)
        is UpdateCulture -> UPDATE_CULTURE(state, action)
        // fashion
        is CreateFashion -> CREATE_FASHION(state, action)
        is DeleteFashion -> DELETE_FASHION(state, action)
        is UpdateFashion -> UPDATE_FASHION(state, action)
        // fashion
        is CreateHoliday -> CREATE_HOLIDAY(state, action)
        is DeleteHoliday -> DELETE_HOLIDAY(state, action)
        is UpdateHoliday -> UPDATE_HOLIDAY(state, action)
        // item template
        is CreateItemTemplate -> CREATE_ITEM_TEMPLATE(state, action)
        is DeleteItemTemplate -> DELETE_ITEM_TEMPLATE(state, action)
        is UpdateItemTemplate -> UPDATE_ITEM_TEMPLATE(state, action)
        // job
        is CreateJob -> CREATE_JOB(state, action)
        is DeleteJob -> DELETE_JOB(state, action)
        is UpdateJob -> UPDATE_JOB(state, action)
        // language
        is CreateLanguage -> CREATE_LANGUAGE(state, action)
        is DeleteLanguage -> DELETE_LANGUAGE(state, action)
        is UpdateLanguage -> UPDATE_LANGUAGE(state, action)
        // material
        is CreateMaterial -> CREATE_MATERIAL(state, action)
        is DeleteMaterial -> DELETE_MATERIAL(state, action)
        is UpdateMaterial -> UPDATE_MATERIAL(state, action)
        // moon
        is CreateMoon -> CREATE_MOON(state, action)
        is DeleteMoon -> DELETE_MOON(state, action)
        is UpdateMoon -> UPDATE_MOON(state, action)
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
        is DeleteRace -> DELETE_RACE(state, action)
        is UpdateRace -> UPDATE_RACE(state, action)
        // race appearance
        is CreateRaceAppearance -> CREATE_RACE_APPEARANCE(state, action)
        is DeleteRaceAppearance -> DELETE_RACE_APPEARANCE(state, action)
        is UpdateRaceAppearance -> UPDATE_RACE_APPEARANCE(state, action)
        // time
        is UpdateTime -> UPDATE_TIME(state, action)
        // world
        is WorldAction -> WORLD_REDUCER(state, action)
    }
}
