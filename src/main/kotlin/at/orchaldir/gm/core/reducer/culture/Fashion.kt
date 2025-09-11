package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.core.action.CreateFashion
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyle
import at.orchaldir.gm.core.model.culture.fashion.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_FASHION: Reducer<CreateFashion, State> = { state, _ ->
    val fashion = Fashion(state.getFashionStorage().nextId)

    noFollowUps(state.updateStorage(state.getFashionStorage().add(fashion)))
}

val UPDATE_FASHION: Reducer<UpdateFashion, State> = { state, action ->
    val fashion = action.fashion

    state.getFashionStorage().require(fashion.id)

    validateFashion(state, fashion)

    val cleanClothingStyle = fashion.clothing
        .copy(equipmentRarityMap = fashion.clothing.equipmentRarityMap.filter { it.value.isNotEmpty() })
    val clean = fashion.copy(clothing = cleanClothingStyle)

    noFollowUps(state.updateStorage(state.getFashionStorage().update(clean)))
}

fun validateFashion(
    state: State,
    fashion: Fashion,
) {
    checkAppearanceStyle(fashion.appearance)
    checkClothingStyle(state, fashion.clothing)
}

private fun checkAppearanceStyle(
    style: AppearanceFashion,
) {
    checkBeardFashion(style.beard)
    checkHairFashion(style.hair)
}

private fun checkBeardFashion(fashion: BeardFashion) {
    if (fashion.beardStyles.contains(BeardStyleType.Full)) {
        require(fashion.fullBeardStyles.isNotEmpty()) { "Available beard styles require at least 1 full beard style!" }
        require(fashion.beardLength.isNotEmpty()) { "Available beard styles require at least 1 beard length!" }
    }
    if (fashion.hasGoatee()) {
        require(fashion.goateeStyles.isNotEmpty()) { "Available beard styles require at least 1 goatee style!" }
    }
    if (fashion.hasMoustache()) {
        require(fashion.moustacheStyles.isNotEmpty()) { "Available beard styles require at least 1 moustache style!" }
    }
}

private fun checkHairFashion(fashion: HairFashion) {
    checkHairStyle(fashion, HairStyle.Bun, fashion.bunStyles, "bun style")
    checkHairStyle(fashion, HairStyle.Long, fashion.longHairStyles, "long hair style")
    checkHairStyle(fashion, HairStyle.Ponytail, fashion.ponytailStyles, "ponytail style")
    checkHairStyle(fashion, HairStyle.Ponytail, fashion.ponytailPositions, "ponytail position")
    checkHairStyle(fashion, HairStyle.Short, fashion.shortHairStyles, "short hair style")

    if (fashion.hasLongHair()) {
        require(fashion.hairLengths.isNotEmpty()) { "Available hair styles require at least 1 hair length!" }
    }
}

private fun <T> checkHairStyle(fashion: HairFashion, hairStyle: HairStyle, list: OneOrNone<T>, text: String) {
    if (fashion.hairStyles.contains(hairStyle)) {
        require(list.isNotEmpty()) { "Requires at least 1 $text!" }
    }
}

private fun checkClothingStyle(
    state: State,
    style: ClothingFashion,
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
    style: ClothingFashion,
    state: State,
) {
    EquipmentDataType.entries.forEach { type ->
        style.getOptions(type).getValidValues().forEach { id ->
            val equipment = state.getEquipmentStorage().getOrThrow(id)
            require(equipment.data.isType(type)) { "Type $type has item ${id.value} of wrong type!" }
        }
    }
}

private fun check(style: ClothingFashion, set: ClothingSet, type: EquipmentDataType) {
    require(style.getOptions(type).isNotEmpty()) { "Clothing set $set requires at least one $type!" }
}
