package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginType
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.model.util.origin.validateOriginType
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.util.validateDate
import at.orchaldir.gm.core.reducer.util.validateOrigin
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val SPELL_TYPE = "Spell"
val ALLOWED_SPELL_ORIGINS = listOf(
    OriginType.Created,
    OriginType.Modified,
    OriginType.Translated,
    OriginType.Undefined,
)

@JvmInline
@Serializable
value class SpellId(val value: Int) : Id<SpellId> {

    override fun next() = SpellId(value + 1)
    override fun type() = SPELL_TYPE
    override fun value() = value

}

@Serializable
data class Spell(
    val id: SpellId,
    val name: Name = Name.init(id),
    val date: Date? = null,
    val language: LanguageId? = null,
    val origin: Origin = UndefinedOrigin,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<SpellId>, Creation, HasDataSources, HasStartDate {

    init {
        validateOriginType(origin, ALLOWED_SPELL_ORIGINS)
    }

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun sources() = sources
    override fun startDate() = date

    override fun validate(state: State) {
        validateDate(state, date, "Spell")
        validateOrigin(state, id, origin, date, ::SpellId)
        state.getLanguageStorage().requireOptional(language)
        state.getDataSourceStorage().require(sources)
    }

}