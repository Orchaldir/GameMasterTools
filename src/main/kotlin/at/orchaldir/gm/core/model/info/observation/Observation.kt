package at.orchaldir.gm.core.model.info.observation

import at.orchaldir.gm.core.model.util.HasPosition
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.util.PositionType
import at.orchaldir.gm.core.model.util.UndefinedPosition
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val OBSERVATION_TYPE = "Observation"
val ALLOWED_OBSERVATION_POSITIONS = PositionType.entries - PositionType.Home - PositionType.Homeless - PositionType.LongTermCare

@JvmInline
@Serializable
value class ObservationId(val value: Int) : Id<ObservationId> {

    override fun next() = ObservationId(value + 1)
    override fun type() = OBSERVATION_TYPE
    override fun value() = value

}

@Serializable
data class Observation(
    val id: ObservationId,
    val data: ObservationData = UndefinedObservationData,
    val position: Position = UndefinedPosition,
) : ElementWithSimpleName<ObservationId>, HasPosition {

    override fun id() = id
    override fun name() = id.print()
    override fun position() = position

}