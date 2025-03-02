package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateEquipment
import at.orchaldir.gm.core.action.DeleteEquipment
import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.selector.item.canDelete
import at.orchaldir.gm.core.selector.item.getEquippedBy
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ITEM_TEMPLATE: Reducer<CreateEquipment, State> = { state, _ ->
    val equipment = Equipment(state.getEquipmentStorage().nextId)

    noFollowUps(state.updateStorage(state.getEquipmentStorage().add(equipment)))
}

val DELETE_ITEM_TEMPLATE: Reducer<DeleteEquipment, State> = { state, action ->
    state.getEquipmentStorage().require(action.id)
    require(state.canDelete(action.id)) { "Equipment ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getEquipmentStorage().remove(action.id)))
}

val UPDATE_ITEM_TEMPLATE: Reducer<UpdateEquipment, State> = { state, action ->
    val template = action.equipment

    val oldTemplate = state.getEquipmentStorage().getOrThrow(template.id)
    template.data.getMaterials().forEach { state.getMaterialStorage().require(it) }

    if (template.data.javaClass != oldTemplate.data.javaClass) {
        require(
            state.getEquippedBy(template.id).isEmpty()
        ) { "Cannot change equipment ${template.id.value} while it is equipped" }
    }

    noFollowUps(state.updateStorage(state.getEquipmentStorage().update(template)))
}
