package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.core.action.CloneRaceAppearance
import at.orchaldir.gm.core.action.CreateRaceAppearance
import at.orchaldir.gm.core.action.DeleteRaceAppearance
import at.orchaldir.gm.core.action.UpdateRaceAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.FeatureColorType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.WingOptions
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.race.canDelete
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
    validateCanDelete(state.canDelete(action.id), action.id)

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().remove(action.id)))
}

val UPDATE_RACE_APPEARANCE: Reducer<UpdateRaceAppearance, State> = { state, action ->
    val appearance = action.appearance
    state.getRaceAppearanceStorage().require(appearance.id)

    validateRaceAppearance(appearance)

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().update(appearance)))
}

fun validateRaceAppearance(appearance: RaceAppearance) {
    checkTails(appearance)
    checkWings(appearance.wing)
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

private fun checkWings(options: WingOptions) {
    if (options.hasWings()) {
        require(options.types.isNotEmpty()) { "Having wings requires wing types!" }
    }
}
