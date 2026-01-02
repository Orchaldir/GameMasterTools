package at.orchaldir.gm.core.model.culture.language

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasOrigin
import at.orchaldir.gm.core.model.util.HasSimpleStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginType
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.model.util.origin.validateOriginType
import at.orchaldir.gm.core.reducer.util.validateDate
import at.orchaldir.gm.core.reducer.util.validateOrigin
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val LANGUAGE_TYPE = "Language"
val ALLOWED_LANGUAGE_ORIGINS = listOf(
    OriginType.Combined,
    OriginType.Created,
    OriginType.Evolved,
    OriginType.Original,
    OriginType.Planar,
    OriginType.Undefined,
)

@JvmInline
@Serializable
value class LanguageId(val value: Int) : Id<LanguageId> {

    override fun next() = LanguageId(value + 1)
    override fun type() = LANGUAGE_TYPE
    override fun value() = value

}

@Serializable
data class Language(
    val id: LanguageId,
    val name: Name = Name.init(id),
    val title: NotEmptyString? = null,
    val date: Date? = null,
    val origin: Origin = UndefinedOrigin,
) : ElementWithSimpleName<LanguageId>, Creation, HasOrigin, HasSimpleStartDate {

    init {
        validateOriginType(origin, ALLOWED_LANGUAGE_ORIGINS)
    }

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun origin() = origin
    override fun startDate() = date

    override fun validate(state: State) {
        validateDate(state, date, "Language")
        validateOrigin(state, id, origin, date, ::LanguageId)

        // no duplicate name?
        // no circle? (time travel?)
    }


}