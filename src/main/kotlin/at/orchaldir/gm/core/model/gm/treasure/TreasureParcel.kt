package at.orchaldir.gm.core.model.gm.treasure

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val TREASURE_PARCEL_TYPE = "Treasure Parcel"

@JvmInline
@Serializable
value class TreasureParcelId(val value: Int) : Id<TreasureParcelId> {

    override fun next() = TreasureParcelId(value + 1)
    override fun type() = TREASURE_PARCEL_TYPE
    override fun value() = value

}

@Serializable
data class TreasureParcel(
    val id: TreasureParcelId,
    val name: Name = Name.init(id),
    val entry: TreasureEntry = NoTreasure,
) : ElementWithSimpleName<TreasureParcelId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        entry.validate(state, id)
    }
}