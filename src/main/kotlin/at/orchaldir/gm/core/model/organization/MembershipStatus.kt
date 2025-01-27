package at.orchaldir.gm.core.model.organization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class MembershipStatusType {
    Outsider,
    Member,
}

@Serializable
sealed class MembershipStatus {

    fun getType() = when (this) {
        Outsider -> MembershipStatusType.Outsider
        is Member -> MembershipStatusType.Member
    }

}

@Serializable
@SerialName("Outsider")
data object Outsider : MembershipStatus()

@Serializable
@SerialName("Member")
data class Member(val rank: Int) : MembershipStatus()

