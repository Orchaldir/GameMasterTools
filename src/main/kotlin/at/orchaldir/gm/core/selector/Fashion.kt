package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId

fun State.canDelete(fashion: FashionId) = getCultures(fashion).isEmpty()

fun State.getFashions(id: EquipmentId): List<Fashion> {
    val equipment = getEquipmentStorage().getOrThrow(id)

    return getFashionStorage().getAll()
        .filter { it.clothing.getOptions(equipment.data.getType()).isAvailable(id) }
}
