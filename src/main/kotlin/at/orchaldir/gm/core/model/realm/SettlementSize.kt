package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val SETTLEMENT_SIZE_TYPE = "Settlement Size"

@JvmInline
@Serializable
value class SettlementSizeId(val value: Int) : Id<SettlementSizeId> {

    override fun next() = SettlementSizeId(value + 1)
    override fun type() = SETTLEMENT_SIZE_TYPE
    override fun value() = value

}

@Serializable
data class SettlementSize(
    val id: SettlementSizeId,
    val name: Name = Name.init(id),
    val maxPopulation: Int = 1000,
) : ElementWithSimpleName<SettlementSizeId> {

    override fun id() = id
    override fun name() = name.text

    override fun toSortString(state: State) = id.value.toString()
    override fun validate(state: State) = doNothing()

}