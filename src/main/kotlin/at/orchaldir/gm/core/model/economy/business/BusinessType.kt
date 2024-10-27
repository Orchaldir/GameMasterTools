package at.orchaldir.gm.core.model.economy.business

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BUSINESS_TYPE = "Business Type"

@JvmInline
@Serializable
value class BusinessTypeId(val value: Int) : Id<BusinessTypeId> {

    override fun next() = BusinessTypeId(value + 1)
    override fun type() = BUSINESS_TYPE
    override fun value() = value

}

@Serializable
data class BusinessType(
    val id: BusinessTypeId,
    val name: String = "Business Type ${id.value}",
) : Element<BusinessTypeId> {

    override fun id() = id
    override fun name() = name

}