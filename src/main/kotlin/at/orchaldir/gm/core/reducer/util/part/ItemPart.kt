package at.orchaldir.gm.core.reducer.util.part

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.ALLOYS_OR_METALS
import at.orchaldir.gm.core.model.economy.material.CATEGORIES_FOR_CLOTHING
import at.orchaldir.gm.core.model.economy.material.CATEGORIES_FOR_GEM
import at.orchaldir.gm.core.model.economy.material.MaterialCategoryType
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.util.part.MadeFromCord
import at.orchaldir.gm.core.model.util.part.MadeFromFabric
import at.orchaldir.gm.core.model.util.part.MadeFromGem
import at.orchaldir.gm.core.model.util.part.MadeFromGlass
import at.orchaldir.gm.core.model.util.part.MadeFromLeather
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.util.part.MadeFromPaper
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.selector.economy.getMaterialIds
import at.orchaldir.gm.core.selector.economy.getMaterials
import at.orchaldir.gm.utils.doNothing

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
        is MadeFromWood -> setOf(MaterialCategoryType.Wood)
    }

    state.getMaterialIds(categories).contains(part.material())
}