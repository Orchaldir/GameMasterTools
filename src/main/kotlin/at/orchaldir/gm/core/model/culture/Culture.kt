package at.orchaldir.gm.core.model.culture

import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.appearance.SomeOf
import at.orchaldir.gm.core.model.culture.name.NamingConvention
import at.orchaldir.gm.core.model.culture.name.NoNamingConvention
import at.orchaldir.gm.core.model.culture.style.AppearanceStyle
import at.orchaldir.gm.core.model.culture.style.ClothingStyle
import at.orchaldir.gm.core.model.language.LanguageId
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
    val languages: SomeOf<LanguageId> = SomeOf(emptyMap()),
    val namingConvention: NamingConvention = NoNamingConvention,
    val appearanceStyle: AppearanceStyle = AppearanceStyle(),
    val clothingStyles: GenderMap<ClothingStyle> = GenderMap(ClothingStyle()),
) : Element<CultureId> {

    override fun id() = id

}