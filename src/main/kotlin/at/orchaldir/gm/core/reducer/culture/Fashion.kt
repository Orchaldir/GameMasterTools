package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyle
import at.orchaldir.gm.core.model.culture.fashion.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.utils.redux.noFollowUps

fun updateFashion(state: State, fashion: Fashion): Pair<State, List<Action>> {
    state.getFashionStorage().require(fashion.id)

    fashion.validate(state)

    val cleanClothingStyle = fashion.clothing
        .copy(equipmentRarityMap = fashion.clothing.equipmentRarityMap.filter { it.value.isNotEmpty() })
    val clean = fashion.copy(clothing = cleanClothingStyle)

    return noFollowUps(state.updateStorage(state.getFashionStorage().update(clean)))
}

fun validateAppearanceFashion(
    style: AppearanceFashion,
) {
    validateBeardFashion(style.beard)
    validateHairFashion(style.hair)
}

private fun validateBeardFashion(fashion: BeardFashion) {
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

private fun validateHairFashion(fashion: HairFashion) {
    validateHairStyle(fashion, HairStyle.Bun, fashion.bunStyles, "bun style")
    validateHairStyle(fashion, HairStyle.Long, fashion.longHairStyles, "long hair style")
    validateHairStyle(fashion, HairStyle.Ponytail, fashion.ponytailStyles, "ponytail style")
    validateHairStyle(fashion, HairStyle.Ponytail, fashion.ponytailPositions, "ponytail position")
    validateHairStyle(fashion, HairStyle.Short, fashion.shortHairStyles, "short hair style")

    if (fashion.hasLongHair()) {
        require(fashion.hairLengths.isNotEmpty()) { "Available hair styles require at least 1 hair length!" }
    }
}

private fun <T> validateHairStyle(fashion: HairFashion, hairStyle: HairStyle, list: OneOrNone<T>, text: String) {
    if (fashion.hairStyles.contains(hairStyle)) {
        require(list.isNotEmpty()) { "Requires at least 1 $text!" }
    }
}

fun validateClothingFashion(
    state: State,
    style: ClothingFashion,
) {
    style.getAllEquipment().forEach { state.getEquipmentStorage().require(it) }

    style.clothingSets.getValidValues().forEach { set ->
        set.getTypes().forEach { type ->
            validate(style, set, type)
        }
    }

    validateCorrectType(style, state)
}

private fun validateCorrectType(
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

private fun validate(style: ClothingFashion, set: ClothingSet, type: EquipmentDataType) {
    require(style.getOptions(type).isNotEmpty()) { "Clothing set $set requires at least one $type!" }
}
