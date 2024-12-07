package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EmploymentStatusType {
    Undefined,
    Unemployed,
    Employed,
}

@Serializable
sealed class EmploymentStatus {

    fun getType() = when (this) {
        UndefinedEmploymentStatus -> EmploymentStatusType.Undefined
        Unemployed -> EmploymentStatusType.Unemployed
        is Employed -> EmploymentStatusType.Employed
    }

    fun getBusiness() = when (this) {
        is Employed -> business
        else -> null
    }

    fun getJob() = when (this) {
        is Employed -> job
        else -> null
    }

    fun hasJob(job: JobId) = when (this) {
        is Employed -> job == this.job
        else -> false
    }

    fun isEmployedAt(business: BusinessId) = when (this) {
        is Employed -> business == this.business
        else -> false
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

@Serializable
@SerialName("Undefined")
data object UndefinedEmploymentStatus : EmploymentStatus()
