package at.orchaldir.gm.core.model.illness

import at.orchaldir.gm.core.model.IllnessId
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasOrigin
import at.orchaldir.gm.core.model.util.NaturalOrigin
import at.orchaldir.gm.core.model.util.Origin
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ILLNESS_TYPE = "Illness"

@Serializable
data class Illness(
    val id: IllnessId,
    val name: Name = Name.init("Illness ${id.value}"),
    val title: NotEmptyString? = null,
    val origin: Origin<IllnessId> = NaturalOrigin(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<IllnessId>, HasDataSources, HasOrigin<IllnessId> {

    override fun id() = id
    override fun name() = name.text
    override fun origin() = origin
    override fun sources() = sources

}