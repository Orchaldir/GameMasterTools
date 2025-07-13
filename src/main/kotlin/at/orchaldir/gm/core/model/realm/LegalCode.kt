package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
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
    val name: Name = Name.init(id),
    val creator: Creator = UndefinedCreator,
    val date: Date? = null,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<LegalCodeId>, Creation, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = creator
    override fun sources() = sources
    override fun startDate() = date

}
