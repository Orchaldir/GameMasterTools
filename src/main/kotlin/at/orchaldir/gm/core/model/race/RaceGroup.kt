package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val RACE_GROUP_TYPE = "Race Group"

@JvmInline
@Serializable
value class RaceGroupId(val value: Int) : Id<RaceGroupId> {

    override fun next() = RaceGroupId(value + 1)
    override fun type() = RACE_GROUP_TYPE
    override fun value() = value

}

@Serializable
data class RaceGroup(
    val id: RaceGroupId,
    val name: Name = Name.init(id),
    val races: Set<RaceId> = emptySet(),
) : ElementWithSimpleName<RaceGroupId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        state.getRaceStorage().require(races)
    }

}