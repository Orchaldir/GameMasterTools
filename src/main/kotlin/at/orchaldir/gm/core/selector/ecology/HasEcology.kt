package at.orchaldir.gm.core.selector.ecology

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.HasEcology
import at.orchaldir.gm.core.model.ecology.plant.PlantId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun State.canDeleteEcologyParticipant(plant: PlantId, result: DeleteResult) =
    canDeleteEcologyParticipant(result) { hasEcology ->
        hasEcology.ecology().contains(plant)
    }

fun State.canDeleteEcologyParticipant(
    result: DeleteResult,
    check: (HasEcology) -> Boolean,
) = result
    .addElements(getEconomies(getRegionStorage(), check))

fun <ID : Id<ID>, ELEMENT> getEconomies(
    storage: Storage<ID, ELEMENT>,
    check: (HasEcology) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasEcology = storage
    .getAll()
    .filter { check(it) }
