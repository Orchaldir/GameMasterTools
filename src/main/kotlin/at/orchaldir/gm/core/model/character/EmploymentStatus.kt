package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.SettlementId
import at.orchaldir.gm.core.model.util.History
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EmploymentStatusType {
    Undefined,
    Unemployed,
    Employed,
    EmployedByRealm,
    EmployedByTown,
    Retired,
}

@Serializable
sealed class EmploymentStatus {

    fun getType() = when (this) {
        UndefinedEmploymentStatus -> EmploymentStatusType.Undefined
        Unemployed -> EmploymentStatusType.Unemployed
        is Employed -> EmploymentStatusType.Employed
        is EmployedByRealm -> EmploymentStatusType.EmployedByRealm
        is EmployedBySettlement -> EmploymentStatusType.EmployedByTown
        Retired -> EmploymentStatusType.Retired
    }

    fun getBusiness() = when (this) {
        is Employed -> business
        is EmployedBySettlement -> optionalBusiness
        else -> null
    }

    fun getJob() = when (this) {
        is Employed -> job
        is EmployedByRealm -> job
        is EmployedBySettlement -> job
        else -> null
    }

    fun hasJob(job: JobId) = when (this) {
        is Employed -> job == this.job
        is EmployedByRealm -> job == this.job
        is EmployedBySettlement -> job == this.job
        else -> false
    }

    fun isEmployedAt(business: BusinessId) = when (this) {
        is Employed -> business == this.business
        is EmployedBySettlement -> business == this.optionalBusiness
        else -> false
    }

    fun isEmployedAt(realm: RealmId) = when (this) {
        is EmployedByRealm -> realm == this.realm
        else -> false
    }

    fun isEmployedAt(town: SettlementId) = when (this) {
        is EmployedBySettlement -> town == this.settlement
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
@SerialName("ByRealm")
data class EmployedByRealm(
    val job: JobId,
    val realm: RealmId,
) : EmploymentStatus()

@Serializable
@SerialName("BySettlement")
data class EmployedBySettlement(
    val job: JobId,
    val settlement: SettlementId,
    val optionalBusiness: BusinessId? = null,
) : EmploymentStatus()

@Serializable
@SerialName("Retired")
data object Retired : EmploymentStatus()

@Serializable
@SerialName("Unemployed")
data object Unemployed : EmploymentStatus()

@Serializable
@SerialName("Undefined")
data object UndefinedEmploymentStatus : EmploymentStatus()

fun History<EmploymentStatus>.wasEmployedAt(realm: RealmId) = previousEntries
    .any { it.entry.isEmployedAt(realm) }

fun History<EmploymentStatus>.isOrWasEmployedAt(realm: RealmId) = current.isEmployedAt(realm) || wasEmployedAt(realm)

fun History<EmploymentStatus>.wasEmployedAt(town: SettlementId) = previousEntries
    .any { it.entry.isEmployedAt(town) }

fun History<EmploymentStatus>.isOrWasEmployedAt(town: SettlementId) = current.isEmployedAt(town) || wasEmployedAt(town)
