package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateFashion
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
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
    fashion.getAllEquipment().forEach { state.getEquipmentStorage().require(it) }

    fashion.clothingSets.getValidValues().forEach { set ->
        set.getTypes().forEach { type ->
            check(fashion, set, type)
        }
    }

    EquipmentDataType.entries.forEach { type ->
        fashion.getOptions(type).getValidValues().forEach { id ->
            val equipment = state.getEquipmentStorage().getOrThrow(id)
            require(equipment.data.isType(type)) { "Type $type has item ${id.value} of wrong type!" }
        }
    }

    val clean = fashion.copy(equipmentRarityMap = fashion.equipmentRarityMap.filter { it.value.isNotEmpty() })

    noFollowUps(state.updateStorage(state.getFashionStorage().update(clean)))
}

private fun check(fashion: Fashion, set: ClothingSet, type: EquipmentDataType) {
    require(fashion.getOptions(type).isNotEmpty()) { "Clothing set $set requires at least one $type!" }
}
