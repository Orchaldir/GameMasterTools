package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val LEGAL_CODE_TYPE = "Legal Code"

@JvmInline
@Serializable
value class LegalCodeId(val value: Int) : Id<LegalCodeId> {

    override fun next() = LegalCodeId(value + 1)
    override fun type() = LEGAL_CODE_TYPE
    override fun value() = value

}

@Serializable
data class LegalCode(
    val id: LegalCodeId,
    val name: Name = Name.init("$LEGAL_CODE_TYPE ${id.value}"),
    val founder: Creator = UndefinedCreator,
    val date: Date? = null,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<LegalCodeId>, Created, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun startDate() = date

}
