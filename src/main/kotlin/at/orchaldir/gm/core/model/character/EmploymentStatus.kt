package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.world.town.TownId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EmploymentStatusType {
    Undefined,
    Unemployed,
    Employed,
    EmployedByTown,
}

@Serializable
sealed class EmploymentStatus {

    fun getType() = when (this) {
        UndefinedEmploymentStatus -> EmploymentStatusType.Undefined
        Unemployed -> EmploymentStatusType.Unemployed
        is Employed -> EmploymentStatusType.Employed
        is EmployedByTown -> EmploymentStatusType.EmployedByTown
    }

    fun getBusiness() = when (this) {
        is Employed -> business
        is EmployedByTown -> optionalBusiness
        else -> null
    }

    fun getJob() = when (this) {
        is Employed -> job
        is EmployedByTown -> job
        else -> null
    }

    fun hasJob(job: JobId) = when (this) {
        is Employed -> job == this.job
        is EmployedByTown -> job == this.job
        else -> false
    }

    fun isEmployedAt(business: BusinessId) = when (this) {
        is Employed -> business == this.business
        is EmployedByTown -> business == this.optionalBusiness
        else -> false
    }

    fun isEmployedAt(town: TownId) = when (this) {
        is EmployedByTown -> town == this.town
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
@SerialName("ByTown")
data class EmployedByTown(
    val job: JobId,
    val town: TownId,
    val optionalBusiness: BusinessId?,
) : EmploymentStatus()

@Serializable
@SerialName("Unemployed")
data object Unemployed : EmploymentStatus()

@Serializable
@SerialName("Undefined")
data object UndefinedEmploymentStatus : EmploymentStatus()
