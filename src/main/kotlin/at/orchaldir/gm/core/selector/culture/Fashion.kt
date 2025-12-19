package at.orchaldir.gm.core.selector.culture

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId

fun State.canDeleteFashion(fashion: FashionId) = DeleteResult(fashion)
    .addElements(getCultures(fashion))

fun State.getFashions(id: EquipmentId): List<Fashion> {
    val equipment = getEquipmentStorage().getOrThrow(id)

    return getFashionStorage().getAll()
        .filter { it.clothing.getOptions(equipment.data.getType()).isAvailable(id) }
}

fun State.getFashion(character: Character): Fashion? {
    val culture = getCultureStorage().getOptional(character.culture) ?: return null

    return getFashionStorage().getOptional(culture.getFashion(character))
}

fun State.getAppearanceFashion(gender: Gender, culture: CultureId?): AppearanceFashion {
    val culture = getCultureStorage().getOptional(culture) ?: return AppearanceFashion()

    return getFashionStorage()
        .getOptional(culture.fashion.get(gender))
        ?.appearance ?: AppearanceFashion()
}
