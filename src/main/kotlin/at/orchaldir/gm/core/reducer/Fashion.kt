package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateFashion
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.NOT_NONE
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

    fashion.clothingSets.getValidValues().forEach { set ->
        set.getTypes().forEach { type ->
            check(fashion, set, type)
        }
    }

    NOT_NONE.forEach { type ->
        fashion.getOptions(type).getValidValues().forEach { id ->
            val template = state.itemTemplates.getOrThrow(id)
            require(template.equipment.isType(type)) { "Type $type has item ${id.value} of wrong type!" }
        }
    }

    val clean = fashion.copy(itemRarityMap = fashion.itemRarityMap.filter { it.value.isNotEmpty() })

    noFollowUps(state.copy(fashion = state.fashion.update(clean)))
}

private fun check(fashion: Fashion, set: ClothingSet, type: EquipmentType) {
    require(fashion.getOptions(type).isNotEmpty()) { "Clothing set $set requires at least one $type!" }
}
