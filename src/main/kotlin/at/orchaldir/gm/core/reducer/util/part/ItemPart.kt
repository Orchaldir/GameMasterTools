package at.orchaldir.gm.core.reducer.util.part

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.*
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.core.selector.economy.getMaterialIds

fun validateItemPart(
    state: State,
    part: ItemPart,
    allowedTypes: Collection<ItemPartType>,
) {
    val type = part.getType()

    require(allowedTypes.contains(type)) {
        "Type $type is not allowed ($allowedTypes)!"
    }

    val categories = when (part) {
        is MadeFromCord -> CATEGORIES_FOR_CLOTHING
        is MadeFromFabric -> setOf(MaterialCategoryType.Fiber)
        is MadeFromGem -> CATEGORIES_FOR_GEM
        is MadeFromGlass -> setOf(MaterialCategoryType.Glass)
        is MadeFromLeather -> setOf(MaterialCategoryType.Leather)
        is MadeFromMetal -> ALLOYS_OR_METALS
        is MadeFromPaper -> setOf(MaterialCategoryType.Paper)
        is MadeFromStone -> CATEGORIES_FOR_STONE
        is MadeFromWood -> setOf(MaterialCategoryType.Wood)
    }

    val material = part.material()

    state.getMaterialStorage().require(material)
    require(state.getMaterialIds(categories).contains(material)) {
        "${material.print()} is not of the correct category!"
    }
}