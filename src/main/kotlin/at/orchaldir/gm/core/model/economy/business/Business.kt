package at.orchaldir.gm.core.model.economy.business

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.ALLOWED_CAUSES_OF_DEATH_FOR_REALM
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BUSINESS_TYPE = "Business"
val ALLOWED_VITAL_STATUS_FOR_BUSINESS = setOf(
    VitalStatusType.Alive,
    VitalStatusType.Closed,
    VitalStatusType.Destroyed,
)
val ALLOWED_CAUSES_OF_DEATH_FOR_BUSINESS = ALLOWED_CAUSES_OF_DEATH_FOR_REALM
val ALLOWED_BUSINESS_POSITIONS = listOf(
    PositionType.Undefined,
    PositionType.Building,
    PositionType.District,
    PositionType.Moon,
    PositionType.Plane,
    PositionType.Realm,
    PositionType.Region,
    PositionType.Town,
    PositionType.World,
)

@JvmInline
@Serializable
value class BusinessId(val value: Int) : Id<BusinessId> {

    override fun next() = BusinessId(value + 1)
    override fun type() = BUSINESS_TYPE
    override fun plural() = "Businesses"
    override fun value() = value

}

@Serializable
data class Business(
    val id: BusinessId,
    val name: Name = Name.init(id),
    val templates: Set<BusinessTemplateId> = emptySet(),
    val date: Date? = null,
    val status: VitalStatus = Alive,
    val founder: Reference = UndefinedReference,
    val ownership: History<Reference> = History(UndefinedReference),
    val position: Position = UndefinedPosition,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<BusinessId>, Creation, HasDataSources, HasOwner, HasPosition, HasVitalStatus {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun owner() = ownership
    override fun position() = position
    override fun startDate() = date
    override fun vitalStatus() = status

    override fun validate(state: State) {
        validateVitalStatus(
            state,
            id,
            status,
            date,
            ALLOWED_VITAL_STATUS_FOR_BUSINESS,
            ALLOWED_CAUSES_OF_DEATH_FOR_BUSINESS,
        )
        validateHasStartAndEnd(state, this)
        validateCreator(state, founder, id, date, "Founder")
        checkPosition(state, position, "position", date, ALLOWED_BUSINESS_POSITIONS)
        checkOwnership(state, ownership, date)
    }

}