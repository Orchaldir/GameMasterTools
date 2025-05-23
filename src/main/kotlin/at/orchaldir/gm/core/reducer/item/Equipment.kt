package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateEquipment
import at.orchaldir.gm.core.action.DeleteEquipment
import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.util.render.COLOR_SCHEME_TYPE
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.item.canDelete
import at.orchaldir.gm.core.selector.item.getEquippedBy
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_EQUIPMENT: Reducer<CreateEquipment, State> = { state, _ ->
    val equipment = Equipment(state.getEquipmentStorage().nextId)

    noFollowUps(state.updateStorage(state.getEquipmentStorage().add(equipment)))
}

val DELETE_EQUIPMENT: Reducer<DeleteEquipment, State> = { state, action ->
    state.getEquipmentStorage().require(action.id)
    validateCanDelete(state.canDelete(action.id), action.id)

    noFollowUps(state.updateStorage(state.getEquipmentStorage().remove(action.id)))
}

val UPDATE_EQUIPMENT: Reducer<UpdateEquipment, State> = { state, action ->
    val equipment = action.equipment
    val oldEquipment = state.getEquipmentStorage().getOrThrow(equipment.id)

    validateEquipment(state, equipment)

    if (equipment.data.javaClass != oldEquipment.data.javaClass) {
        require(
            state.getEquippedBy(equipment.id).isEmpty()
        ) { "Cannot change equipment ${equipment.id.value} while it is equipped" }
    }

    noFollowUps(state.updateStorage(state.getEquipmentStorage().update(equipment)))
}

fun validateEquipment(
    state: State,
    equipment: Equipment,
) {
    val requiredSchemaColors = equipment.data.requiredSchemaColors()

    state.getMaterialStorage().require(equipment.data.materials())
    state.getColorSchemeStorage().require(equipment.colorSchemes)

    require(requiredSchemaColors == 0 || equipment.colorSchemes.isNotEmpty()) {
        "Requires at least 1 $COLOR_SCHEME_TYPE"
    }

    state.getColorSchemeStorage().get(equipment.colorSchemes)
        .forEach { scheme ->
            require(scheme.data.count() >= requiredSchemaColors) { "${scheme.id.print()} has too few colors!" }
        }
}
