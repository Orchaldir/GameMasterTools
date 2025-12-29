package at.orchaldir.gm.utils.math.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AreaLookupType {
    Calculated,
    UserDefined,
}

@Serializable
sealed class AreaLookup {
    fun getType() = when (this) {
        is CalculatedArea -> AreaLookupType.Calculated
        is UserDefinedArea -> AreaLookupType.UserDefined
    }
}

@Serializable
@SerialName("Calculated")
data object CalculatedArea : AreaLookup()

@Serializable
@SerialName("User")
data class UserDefinedArea(
    val area: Area,
) : AreaLookup()

