package at.orchaldir.gm.core.model.organization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class MembershipStatusType {
    Dead,
    Member,
    Outsider,
}

@Serializable
sealed class MembershipStatus {

    fun getType() = when (this) {
        DeadMember -> MembershipStatusType.Dead
        is Member -> MembershipStatusType.Member
        Outsider -> MembershipStatusType.Outsider
    }

}

@Serializable
@SerialName("Dead")
data object DeadMember : MembershipStatus()

@Serializable
@SerialName("Member")
data class Member(val rank: Int) : MembershipStatus()

@Serializable
@SerialName("Outsider")
data object Outsider : MembershipStatus()
