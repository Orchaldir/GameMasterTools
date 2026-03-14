package at.orchaldir.gm.core.model.realm.population

import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.job.Income
import at.orchaldir.gm.core.model.economy.job.UndefinedIncome
import at.orchaldir.gm.core.model.race.RaceId
import kotlinx.serialization.Serializable

@Serializable
data class PopulationUnit<T>(
    val value: T,
    val race: RaceId,
    val culture: CultureId,
    val income: Income = UndefinedIncome,
)
