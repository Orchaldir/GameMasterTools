package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EmploymentStatusType {
    Unemployed,
    Employed,
}

@Serializable
sealed class EmploymentStatus {

    fun getType() = when (this) {
        Unemployed -> EmploymentStatusType.Unemployed
        is Employed -> EmploymentStatusType.Employed
    }

}

@Serializable
@SerialName("Employed")
data class Employed(
    val business: BusinessId,
    val job: JobId,
) : EmploymentStatus()

@Serializable
@SerialName("Unemployed")
data object Unemployed : EmploymentStatus()
