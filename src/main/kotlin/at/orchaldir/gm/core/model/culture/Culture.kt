package at.orchaldir.gm.core.model.culture

import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.appearance.SomeOf
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.name.NamingConvention
import at.orchaldir.gm.core.model.culture.name.NoNamingConvention
import at.orchaldir.gm.core.model.culture.style.AppearanceStyle
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CULTURE = "Culture"

@JvmInline
@Serializable
value class CultureId(val value: Int) : Id<CultureId> {

    override fun next() = CultureId(value + 1)
    override fun type() = CULTURE
    override fun value() = value

}

@Serializable
data class Culture(
    val id: CultureId,
    val name: String = "Culture ${id.value}",
    val calendar: CalendarId = CalendarId(0),
    val languages: SomeOf<LanguageId> = SomeOf(emptyMap()),
    val namingConvention: NamingConvention = NoNamingConvention,
    val appearanceStyle: AppearanceStyle = AppearanceStyle(),
    val clothingStyles: GenderMap<FashionId> = GenderMap(FashionId(0)),
) : Element<CultureId> {

    override fun id() = id
    override fun name() = name

    fun getFashion(character: Character) = clothingStyles.get(character.gender)

}