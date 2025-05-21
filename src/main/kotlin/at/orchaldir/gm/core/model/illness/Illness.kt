package at.orchaldir.gm.core.model.illness

import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.NaturalOrigin
import at.orchaldir.gm.core.model.util.Origin
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ILLNESS_TYPE = "Illness"

@JvmInline
@Serializable
value class IllnessId(val value: Int) : Id<IllnessId> {

    override fun next() = IllnessId(value + 1)
    override fun type() = ILLNESS_TYPE
    override fun plural() = "Illnesses"
    override fun value() = value

}

@Serializable
data class Illness(
    val id: IllnessId,
    val name: Name = Name.init("Illness ${id.value}"),
    val title: NotEmptyString? = null,
    val origin: Origin<IllnessId> = NaturalOrigin(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<IllnessId>, Creation, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun startDate() = origin.date()

}