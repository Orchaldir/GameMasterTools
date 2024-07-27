package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateFashion
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.ClothingSet.*
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_FASHION: Reducer<CreateFashion, State> = { state, _ ->
    val fashion = Fashion(state.fashion.nextId)

    noFollowUps(state.copy(fashion = state.fashion.add(fashion)))
}

val DELETE_FASHION: Reducer<DeleteFashion, State> = { state, action ->
    state.fashion.require(action.id)
    require(state.canDelete(action.id)) { "Fashion ${action.id.value} is used" }

    noFollowUps(state.copy(fashion = state.fashion.remove(action.id)))
}

val UPDATE_FASHION: Reducer<UpdateFashion, State> = { state, action ->
    val fashion = action.fashion

    state.fashion.require(fashion.id)
    fashion.getAllItemTemplates().forEach { state.itemTemplates.require(it) }

    if (fashion.clothingSets.isAvailable(Dress)) {
        require(fashion.dresses.isNotEmpty()) { "Clothing set Dress requires at least one dress!" }
    }

    if (fashion.clothingSets.isAvailable(PantsAndShirt)) {
        require(fashion.pants.isNotEmpty()) { "Clothing set PantsAndShirt requires at least one pants!" }
        require(fashion.shirts.isNotEmpty()) { "Clothing set PantsAndShirt requires at least one shirt!" }
    }

    if (fashion.clothingSets.isAvailable(ShirtAndSkirt)) {
        require(fashion.shirts.isNotEmpty()) { "Clothing set ShirtAndSkirt requires at least one shirt!" }
        require(fashion.skirts.isNotEmpty()) { "Clothing set ShirtAndSkirt requires at least one skirt!" }
    }

    noFollowUps(state.copy(fashion = state.fashion.update(fashion)))
}
