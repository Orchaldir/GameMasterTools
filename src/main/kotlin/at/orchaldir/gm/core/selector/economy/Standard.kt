package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.Income
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun State.getRequiredStandards() = getMaxStandardUsedByJob()?.let { it + 1 }

fun State.getMaxStandardUsedByJob(): Int? {
    val incomes = mutableListOf<Income>()

    addIncomes(incomes, getDistrictStorage())
    incomes.addAll(getJobStorage().getAll().map { it.income })
    addIncomes(incomes, getRealmStorage())
    addIncomes(incomes, getTownStorage())

    return incomes
        .maxOfOrNull {
            if (it is AffordableStandardOfLiving) {
                it.standard.value
            } else {
                -1
            }
        }
}

private fun <ID : Id<ID>, ELEMENT> addIncomes(
    incomes: MutableList<Income>,
    storage: Storage<ID, ELEMENT>,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    incomes.addAll(storage.getAll().mapNotNull { it.population().income() })
}




