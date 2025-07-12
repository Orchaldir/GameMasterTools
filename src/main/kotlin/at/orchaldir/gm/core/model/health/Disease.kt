package at.orchaldir.gm.core.model.health

import at.orchaldir.gm.core.model.time.calendar.ALLOWED_CALENDAR_ORIGINS
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginType
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.model.util.origin.validateOriginType
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val DISEASE_TYPE = "Disease"
val ALLOWED_DISEASE_ORIGINS = OriginType.entries - OriginType.Translated

@JvmInline
@Serializable
value class DiseaseId(val value: Int) : Id<DiseaseId> {

    override fun next() = DiseaseId(value + 1)
    override fun type() = DISEASE_TYPE
    override fun value() = value

}

@Serializable
data class Disease(
    val id: DiseaseId,
    val name: Name = Name.init(id),
    val date: Date? = null,
    val origin: Origin = UndefinedOrigin(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<DiseaseId>, Creation, HasDataSources, HasStartDate {

    init {
        validateOriginType(origin, ALLOWED_DISEASE_ORIGINS)
    }

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun sources() = sources
    override fun startDate() = date

}