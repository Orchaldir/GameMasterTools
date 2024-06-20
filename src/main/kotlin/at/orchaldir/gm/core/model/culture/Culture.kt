package at.orchaldir.gm.core.model.culture

import at.orchaldir.gm.core.model.culture.name.NamingConvention
import at.orchaldir.gm.core.model.culture.name.NoNamingConvention
import at.orchaldir.gm.core.model.culture.style.StyleOptions
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CultureId(val value: Int) : Id<CultureId> {

    override fun next() = CultureId(value + 1)
    override fun value() = value

}

@Serializable
data class Culture(
    val id: CultureId,
    val name: String = "Culture ${id.value}",
    val namingConvention: NamingConvention = NoNamingConvention,
    val styleOptions: StyleOptions = StyleOptions(),
) : Element<CultureId> {

    override fun id() = id

}