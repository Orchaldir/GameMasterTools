package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.character.CHARACTER_REDUCER
import at.orchaldir.gm.core.reducer.character.UPDATE_PERSONALITY_TRAIT
import at.orchaldir.gm.core.reducer.character.UPDATE_TITLE
import at.orchaldir.gm.core.reducer.culture.UPDATE_CULTURE
import at.orchaldir.gm.core.reducer.culture.UPDATE_FASHION
import at.orchaldir.gm.core.reducer.culture.UPDATE_LANGUAGE
import at.orchaldir.gm.core.reducer.economy.ECONOMY_REDUCER
import at.orchaldir.gm.core.reducer.economy.UPDATE_MATERIAL
import at.orchaldir.gm.core.reducer.health.UPDATE_DISEASE
import at.orchaldir.gm.core.reducer.item.ITEM_REDUCER
import at.orchaldir.gm.core.reducer.magic.MAGIC_REDUCER
import at.orchaldir.gm.core.reducer.organization.ORGANIZATION_REDUCER
import at.orchaldir.gm.core.reducer.race.UPDATE_RACE
import at.orchaldir.gm.core.reducer.race.UPDATE_RACE_APPEARANCE
import at.orchaldir.gm.core.reducer.realm.REALM_REDUCER
import at.orchaldir.gm.core.reducer.religion.RELIGION_REDUCER
import at.orchaldir.gm.core.reducer.time.UPDATE_CALENDAR
import at.orchaldir.gm.core.reducer.time.UPDATE_HOLIDAY
import at.orchaldir.gm.core.reducer.util.color.UPDATE_COLOR_SCHEME
import at.orchaldir.gm.core.reducer.util.font.UPDATE_FONT
import at.orchaldir.gm.core.reducer.util.name.UPDATE_NAME_LIST
import at.orchaldir.gm.core.reducer.util.quote.UPDATE_QUOTE
import at.orchaldir.gm.core.reducer.util.source.UPDATE_DATA_SOURCE
import at.orchaldir.gm.core.reducer.world.WORLD_REDUCER
import at.orchaldir.gm.core.selector.character.canDeletePersonalityTrait
import at.orchaldir.gm.core.selector.character.canDeleteTitle
import at.orchaldir.gm.core.selector.culture.canDeleteCulture
import at.orchaldir.gm.core.selector.culture.canDeleteFashion
import at.orchaldir.gm.core.selector.culture.canDeleteLanguage
import at.orchaldir.gm.core.selector.economy.canDeleteMaterial
import at.orchaldir.gm.core.selector.health.canDeleteDisease
import at.orchaldir.gm.core.selector.race.canDeleteRace
import at.orchaldir.gm.core.selector.race.canDeleteRaceAppearance
import at.orchaldir.gm.core.selector.time.canDeleteCalendar
import at.orchaldir.gm.core.selector.time.canDeleteHoliday
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.utils.redux.Reducer

val REDUCER: Reducer<Action, State> = { state, action ->
    when (action) {
        // meta
        is CreateAction<*> -> reduceCreateElement(state, action.id)
        is CloneAction<*> -> reduceCloneElement(state, action.id)
        is UpdateAction<*,*> -> reduceUpdateElement(state, action.element)
        is LoadData -> LOAD_DATA(state, action)
        // calendar
        is DeleteCalendar -> deleteElement(state, action.id, State::canDeleteCalendar)
        is UpdateCalendar -> UPDATE_CALENDAR(state, action)
        // color schemes
        is DeleteColorScheme -> deleteElement(state, action.id, State::canDeleteColorScheme)
        is UpdateColorScheme -> UPDATE_COLOR_SCHEME(state, action)
        // culture
        is DeleteCulture -> deleteElement(state, action.id, State::canDeleteCulture)
        is UpdateCulture -> UPDATE_CULTURE(state, action)
        // data
        is UpdateData -> UPDATE_DATA(state, action)
        // data source
        is DeleteDataSource -> deleteElement(state, action.id, State::canDeleteDataSource)
        is UpdateDataSource -> UPDATE_DATA_SOURCE(state, action)
        // disease
        is DeleteDisease -> deleteElement(state, action.id, State::canDeleteDisease)
        is UpdateDisease -> UPDATE_DISEASE(state, action)
        // fashion
        is DeleteFashion -> deleteElement(state, action.id, State::canDeleteFashion)
        is UpdateFashion -> UPDATE_FASHION(state, action)
        // font
        is DeleteFont -> deleteElement(state, action.id, State::canDeleteFont)
        is UpdateFont -> UPDATE_FONT(state, action)
        // holiday
        is DeleteHoliday -> deleteElement(state, action.id, State::canDeleteHoliday)
        is UpdateHoliday -> UPDATE_HOLIDAY(state, action)
        // language
        is DeleteLanguage -> deleteElement(state, action.id, State::canDeleteLanguage)
        is UpdateLanguage -> UPDATE_LANGUAGE(state, action)
        // material
        is DeleteMaterial -> deleteElement(state, action.id, State::canDeleteMaterial)
        is UpdateMaterial -> UPDATE_MATERIAL(state, action)
        // name list
        is DeleteNameList -> deleteElement(state, action.id, State::canDeleteNameList)
        is UpdateNameList -> UPDATE_NAME_LIST(state, action)
        // personality
        is DeletePersonalityTrait -> deleteElement(state, action.id, State::canDeletePersonalityTrait)
        is UpdatePersonalityTrait -> UPDATE_PERSONALITY_TRAIT(state, action)
        // quote
        is DeleteQuote -> deleteElement(state, action.id, State::canDeleteQuote)
        is UpdateQuote -> UPDATE_QUOTE(state, action)
        // race
        is DeleteRace -> deleteElement(state, action.id, State::canDeleteRace)
        is UpdateRace -> UPDATE_RACE(state, action)
        // race appearance
        is DeleteRaceAppearance -> deleteElement(state, action.id, State::canDeleteRaceAppearance)
        is UpdateRaceAppearance -> UPDATE_RACE_APPEARANCE(state, action)
        // title
        is DeleteTitle -> deleteElement(state, action.id, State::canDeleteTitle)
        is UpdateTitle -> UPDATE_TITLE(state, action)
        // sub reducers
        is CharacterAction -> CHARACTER_REDUCER(state, action)
        is ItemAction -> ITEM_REDUCER(state, action)
        is EconomyAction -> ECONOMY_REDUCER(state, action)
        is MagicAction -> MAGIC_REDUCER(state, action)
        is OrganizationAction -> ORGANIZATION_REDUCER(state, action)
        is RealmAction -> REALM_REDUCER(state, action)
        is ReligionAction -> RELIGION_REDUCER(state, action)
        is WorldAction -> WORLD_REDUCER(state, action)
    }
}
