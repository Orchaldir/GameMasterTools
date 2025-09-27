package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.character.CHARACTER_REDUCER
import at.orchaldir.gm.core.reducer.economy.ECONOMY_REDUCER
import at.orchaldir.gm.core.reducer.item.ITEM_REDUCER
import at.orchaldir.gm.core.reducer.magic.MAGIC_REDUCER
import at.orchaldir.gm.core.reducer.organization.ORGANIZATION_REDUCER
import at.orchaldir.gm.core.reducer.realm.REALM_REDUCER
import at.orchaldir.gm.core.reducer.religion.RELIGION_REDUCER
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
        is UpdateAction<*, *> -> reduceUpdateElement(state, action.element)
        is LoadData -> LOAD_DATA(state, action)
        // calendar
        is DeleteCalendar -> deleteElement(state, action.id, State::canDeleteCalendar)
        // color schemes
        is DeleteColorScheme -> deleteElement(state, action.id, State::canDeleteColorScheme)
        // culture
        is DeleteCulture -> deleteElement(state, action.id, State::canDeleteCulture)
        // data
        is UpdateData -> UPDATE_DATA(state, action)
        // data source
        is DeleteDataSource -> deleteElement(state, action.id, State::canDeleteDataSource)
        // disease
        is DeleteDisease -> deleteElement(state, action.id, State::canDeleteDisease)
        // fashion
        is DeleteFashion -> deleteElement(state, action.id, State::canDeleteFashion)
        // font
        is DeleteFont -> deleteElement(state, action.id, State::canDeleteFont)
        // holiday
        is DeleteHoliday -> deleteElement(state, action.id, State::canDeleteHoliday)
        // language
        is DeleteLanguage -> deleteElement(state, action.id, State::canDeleteLanguage)
        // material
        is DeleteMaterial -> deleteElement(state, action.id, State::canDeleteMaterial)
        // name list
        is DeleteNameList -> deleteElement(state, action.id, State::canDeleteNameList)
        // personality
        is DeletePersonalityTrait -> deleteElement(state, action.id, State::canDeletePersonalityTrait)
        // quote
        is DeleteQuote -> deleteElement(state, action.id, State::canDeleteQuote)
        // race
        is DeleteRace -> deleteElement(state, action.id, State::canDeleteRace)
        // race appearance
        is DeleteRaceAppearance -> deleteElement(state, action.id, State::canDeleteRaceAppearance)
        // title
        is DeleteTitle -> deleteElement(state, action.id, State::canDeleteTitle)
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
