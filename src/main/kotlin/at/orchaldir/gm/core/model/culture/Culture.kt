package at.orchaldir.gm.core.model.culture

import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.culture.name.NamingConvention
import at.orchaldir.gm.core.model.culture.name.NoNamingConvention
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CULTURE_TYPE = "Culture"

@JvmInline
@Serializable
value class CultureId(val value: Int) : Id<CultureId> {

    override fun next() = CultureId(value + 1)
    override fun type() = CULTURE_TYPE
    override fun value() = value

}

@Serializable
data class Culture(
    val id: CultureId,
    val name: Name = Name.init("Culture ${id.value}"),
    val calendar: CalendarId = CalendarId(0),
    val languages: SomeOf<LanguageId> = SomeOf(emptyMap()),
    val namingConvention: NamingConvention = NoNamingConvention,
    val fashion: GenderMap<FashionId?> = GenderMap(null),
    val holidays: Set<HolidayId> = emptySet(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<CultureId>, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = null

    fun getFashion(character: Character) = fashion.get(character.gender)

}