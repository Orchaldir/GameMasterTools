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
    val fashion = Fashion(state.getFashionStorage().nextId)

    noFollowUps(state.updateStorage(state.getFashionStorage().add(fashion)))
}

val DELETE_FASHION: Reducer<DeleteFashion, State> = { state, action ->
    state.getFashionStorage().require(action.id)
    require(state.canDelete(action.id)) { "Fashion ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getFashionStorage().remove(action.id)))
}

val UPDATE_FASHION: Reducer<UpdateFashion, State> = { state, action ->
    val fashion = action.fashion

    state.getFashionStorage().require(fashion.id)
    fashion.getAllItemTemplates().forEach { state.getItemTemplateStorage().require(it) }

    fashion.clothingSets.getValidValues().forEach { set ->
        set.getTypes().forEach { type ->
            check(fashion, set, type)
        }
    }

    NOT_NONE.forEach { type ->
        fashion.getOptions(type).getValidValues().forEach { id ->
            val template = state.getItemTemplateStorage().getOrThrow(id)
            require(template.equipment.isType(type)) { "Type $type has item ${id.value} of wrong type!" }
        }
    }

    val clean = fashion.copy(itemRarityMap = fashion.itemRarityMap.filter { it.value.isNotEmpty() })

    noFollowUps(state.updateStorage(state.getFashionStorage().update(clean)))
}

private fun check(fashion: Fashion, set: ClothingSet, type: EquipmentType) {
    require(fashion.getOptions(type).isNotEmpty()) { "Clothing set $set requires at least one $type!" }
}
