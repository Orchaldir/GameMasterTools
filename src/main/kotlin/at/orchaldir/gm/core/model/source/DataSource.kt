package at.orchaldir.gm.core.model.source

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val DATA_SOURCE_TYPE = "DataSource"

@JvmInline
@Serializable
value class DataSourceId(val value: Int) : Id<DataSourceId> {

    override fun next() = DataSourceId(value + 1)
    override fun type() = DATA_SOURCE_TYPE
    override fun value() = value

}

@Serializable
data class DataSource(
    val id: DataSourceId,
    val text: Name = Name.init("DataSource ${id.value}"),
    val year: Int,
) : ElementWithSimpleName<DataSourceId> {

    override fun id() = id
    override fun name() = text.text

}