package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.character.*
import at.orchaldir.gm.core.reducer.culture.*
import at.orchaldir.gm.core.reducer.economy.CREATE_MATERIAL
import at.orchaldir.gm.core.reducer.economy.DELETE_MATERIAL
import at.orchaldir.gm.core.reducer.economy.ECONOMY_REDUCER
import at.orchaldir.gm.core.reducer.economy.UPDATE_MATERIAL
import at.orchaldir.gm.core.reducer.health.CREATE_DISEASE
import at.orchaldir.gm.core.reducer.health.DELETE_DISEASE
import at.orchaldir.gm.core.reducer.health.UPDATE_DISEASE
import at.orchaldir.gm.core.reducer.item.ITEM_REDUCER
import at.orchaldir.gm.core.reducer.magic.MAGIC_REDUCER
import at.orchaldir.gm.core.reducer.organization.ORGANIZATION_REDUCER
import at.orchaldir.gm.core.reducer.race.*
import at.orchaldir.gm.core.reducer.realm.REALM_REDUCER
import at.orchaldir.gm.core.reducer.religion.RELIGION_REDUCER
import at.orchaldir.gm.core.reducer.time.*
import at.orchaldir.gm.core.reducer.util.color.CREATE_COLOR_SCHEME
import at.orchaldir.gm.core.reducer.util.color.DELETE_COLOR_SCHEME
import at.orchaldir.gm.core.reducer.util.color.UPDATE_COLOR_SCHEME
import at.orchaldir.gm.core.reducer.util.font.CREATE_FONT
import at.orchaldir.gm.core.reducer.util.font.DELETE_FONT
import at.orchaldir.gm.core.reducer.util.font.UPDATE_FONT
import at.orchaldir.gm.core.reducer.util.name.CREATE_NAME_LIST
import at.orchaldir.gm.core.reducer.util.name.DELETE_NAME_LIST
import at.orchaldir.gm.core.reducer.util.name.UPDATE_NAME_LIST
import at.orchaldir.gm.core.reducer.util.quote.CREATE_QUOTE
import at.orchaldir.gm.core.reducer.util.quote.DELETE_QUOTE
import at.orchaldir.gm.core.reducer.util.quote.UPDATE_QUOTE
import at.orchaldir.gm.core.reducer.util.source.CREATE_DATA_SOURCE
import at.orchaldir.gm.core.reducer.util.source.DELETE_DATA_SOURCE
import at.orchaldir.gm.core.reducer.util.source.UPDATE_DATA_SOURCE
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
        // color schemes
        is CreateColorScheme -> CREATE_COLOR_SCHEME(state, action)
        is DeleteColorScheme -> DELETE_COLOR_SCHEME(state, action)
        is UpdateColorScheme -> UPDATE_COLOR_SCHEME(state, action)
        // culture
        is CreateCulture -> CREATE_CULTURE(state, action)
        is CloneCulture -> CLONE_CULTURE(state, action)
        is DeleteCulture -> DELETE_CULTURE(state, action)
        is UpdateCulture -> UPDATE_CULTURE(state, action)
        // data
        is UpdateData -> UPDATE_DATA(state, action)
        // data source
        is CreateDataSource -> CREATE_DATA_SOURCE(state, action)
        is DeleteDataSource -> DELETE_DATA_SOURCE(state, action)
        is UpdateDataSource -> UPDATE_DATA_SOURCE(state, action)
        // disease
        is CreateDisease -> CREATE_DISEASE(state, action)
        is DeleteDisease -> DELETE_DISEASE(state, action)
        is UpdateDisease -> UPDATE_DISEASE(state, action)
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
        // quote
        is CreateQuote -> CREATE_QUOTE(state, action)
        is DeleteQuote -> DELETE_QUOTE(state, action)
        is UpdateQuote -> UPDATE_QUOTE(state, action)
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
        // title
        is CreateTitle -> CREATE_TITLE(state, action)
        is DeleteTitle -> DELETE_TITLE(state, action)
        is UpdateTitle -> UPDATE_TITLE(state, action)
        // sub reducers
        is ItemAction -> ITEM_REDUCER(state, action)
        is EconomyAction -> ECONOMY_REDUCER(state, action)
        is MagicAction -> MAGIC_REDUCER(state, action)
        is OrganizationAction -> ORGANIZATION_REDUCER(state, action)
        is RealmAction -> REALM_REDUCER(state, action)
        is ReligionAction -> RELIGION_REDUCER(state, action)
        is WorldAction -> WORLD_REDUCER(state, action)
    }
}
