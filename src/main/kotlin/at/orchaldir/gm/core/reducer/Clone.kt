package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.noFollowUps

fun reduceCloneElement(
    state: State,
    id: Id<*>,
): Pair<State, List<Action>> = when (id) {
    is CharacterTemplateId -> cloneElement(state, id)
    is CultureId -> cloneElement(state, id)
    is RaceAppearanceId -> cloneElement(state, id)
    is RaceId -> cloneElement(state, id)
    else -> error("Cloning is not supported!")
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, Action> cloneElement(
    state: State,
    id: ID,
): Pair<State, List<Action>> {
    val storage = state.getStorage<ID, ELEMENT>(id)
    val original = storage.getOrThrow(id)
    val cloneId = storage.nextId
    val clone = original.clone(cloneId) as ELEMENT

    return noFollowUps(state.updateStorage(storage.add(clone)))
}
