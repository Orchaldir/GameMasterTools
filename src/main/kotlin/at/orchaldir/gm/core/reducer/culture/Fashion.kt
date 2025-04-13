package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.core.action.CreateFashion
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.AppearanceStyle
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.culture.fashion.ClothingStyle
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.selector.culture.canDelete
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

    checkAppearanceStyle(fashion.appearance)
    checkClothingStyle(state, fashion.clothing)

    val cleanClothingStyle = fashion.clothing
        .copy(equipmentRarityMap = fashion.clothing.equipmentRarityMap.filter { it.value.isNotEmpty() })
    val clean = fashion.copy(clothing = cleanClothingStyle)

    noFollowUps(state.updateStorage(state.getFashionStorage().update(clean)))
}

private fun checkAppearanceStyle(
    style: AppearanceStyle,
) {
    if (style.hasGoatee()) {
        require(style.goateeStyles.isNotEmpty()) { "Available beard styles requires at least 1 goatee!" }
    }
}

private fun checkClothingStyle(
    state: State,
    style: ClothingStyle,
) {
    style.getAllEquipment().forEach { state.getEquipmentStorage().require(it) }

    style.clothingSets.getValidValues().forEach { set ->
        set.getTypes().forEach { type ->
            check(style, set, type)
        }
    }

    checkCorrectType(style, state)
}

private fun checkCorrectType(
    style: ClothingStyle,
    state: State,
) {
    EquipmentDataType.entries.forEach { type ->
        style.getOptions(type).getValidValues().forEach { id ->
            val equipment = state.getEquipmentStorage().getOrThrow(id)
            require(equipment.data.isType(type)) { "Type $type has item ${id.value} of wrong type!" }
        }
    }
}

private fun check(style: ClothingStyle, set: ClothingSet, type: EquipmentDataType) {
    require(style.getOptions(type).isNotEmpty()) { "Clothing set $set requires at least one $type!" }
}
