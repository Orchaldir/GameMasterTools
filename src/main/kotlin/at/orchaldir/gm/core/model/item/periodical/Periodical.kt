package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.name.ComplexName
import at.orchaldir.gm.core.model.name.SimpleName
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val PERIODICAL_TYPE = "Periodical"

@JvmInline
@Serializable
value class PeriodicalId(val value: Int) : Id<PeriodicalId> {

    override fun next() = PeriodicalId(value + 1)
    override fun type() = PERIODICAL_TYPE
    override fun value() = value

}

@Serializable
data class Periodical(
    val id: PeriodicalId,
    val name: ComplexName = SimpleName("Periodical ${id.value}"),
    private val startDate: Date? = null,
    val founder: Creator = UndefinedCreator,
    val ownership: History<Owner> = History(UndefinedOwner),
    val language: LanguageId = LanguageId(0),
) : Element<PeriodicalId>, Created, HasStartDate {

    override fun id() = id
    override fun name(state: State) = name.resolve(state)

    override fun creator() = founder
    override fun startDate() = startDate

}