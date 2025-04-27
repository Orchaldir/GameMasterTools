package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CloneRaceAppearance
import at.orchaldir.gm.core.action.CreateRaceAppearance
import at.orchaldir.gm.core.action.DeleteRaceAppearance
import at.orchaldir.gm.core.action.UpdateRaceAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.FeatureColorType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RACE_APPEARANCE: Reducer<CreateRaceAppearance, State> = { state, _ ->
    val character = RaceAppearance(state.getRaceAppearanceStorage().nextId)

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().add(character)))
}

val CLONE_RACE_APPEARANCE: Reducer<CloneRaceAppearance, State> = { state, action ->
    val original = state.getRaceAppearanceStorage().getOrThrow(action.id)
    val cloneId = state.getRaceAppearanceStorage().nextId
    val clone = original.copy(id = cloneId, name = Name.init("Clone ${cloneId.value}"))

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().add(clone)))
}

val DELETE_RACE_APPEARANCE: Reducer<DeleteRaceAppearance, State> = { state, action ->
    state.getRaceAppearanceStorage().require(action.id)
    require(state.canDelete(action.id)) { "Race Appearance ${action.id.value} cannot be deleted" }

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().remove(action.id)))
}

val UPDATE_RACE_APPEARANCE: Reducer<UpdateRaceAppearance, State> = { state, action ->
    val appearance = action.appearance
    state.getRaceAppearanceStorage().require(appearance.id)

    validateRaceAppearance(state, appearance)

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().update(appearance)))
}

fun validateRaceAppearance(state: State, appearance: RaceAppearance) {
    checkTails(appearance)
}

private fun checkTails(appearance: RaceAppearance) {
    val options = appearance.tail
    options.simpleShapes.getValidValues().forEach {
        require(options.simpleOptions.containsKey(it)) { "No options for $it tail!" }
    }

    if (!appearance.hair.hairTypes.contains(HairType.Normal)) {
        options.simpleOptions.forEach { (shape, shapeOptions) ->
            require(shapeOptions.types != FeatureColorType.Hair) { "Tail options for $shape require hair!" }
        }
    }
}
