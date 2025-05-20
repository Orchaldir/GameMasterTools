package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val FASHION_TYPE = "Fashion"

@JvmInline
@Serializable
value class FashionId(val value: Int) : Id<FashionId> {

    override fun next() = FashionId(value + 1)
    override fun type() = FASHION_TYPE
    override fun value() = value

}

@Serializable
data class Fashion(
    val id: FashionId,
    val name: Name = Name.init("Fashion ${id.value}"),
    val appearance: AppearanceFashion = AppearanceFashion(),
    val clothing: ClothingFashion = ClothingFashion(),
) : ElementWithSimpleName<FashionId> {

    override fun id() = id
    override fun name() = name.text

}