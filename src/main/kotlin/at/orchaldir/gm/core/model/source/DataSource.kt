package at.orchaldir.gm.core.model.source

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val DATA_SOURCE_TYPE = "Data Source"

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
    val name: Name = Name.init("DataSource ${id.value}"),
    val year: Int = 0,
    val edition: Int? = null,
) : ElementWithSimpleName<DataSourceId> {

    override fun id() = id
    override fun name() = name.text

}