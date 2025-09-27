package at.orchaldir.gm.core.model.util.quote

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val QUOTE_TYPE = "Quote"

@JvmInline
@Serializable
value class QuoteId(val value: Int) : Id<QuoteId> {

    override fun next() = QuoteId(value + 1)
    override fun type() = QUOTE_TYPE
    override fun value() = value

}

@Serializable
data class Quote(
    val id: QuoteId,
    val text: NotEmptyString = NotEmptyString.init("Quote ${id.value}"),
    val description: NotEmptyString? = null,
    val type: QuoteType = QuoteType.Quote,
    val source: Reference = UndefinedReference,
    val date: Date? = null,
) : ElementWithSimpleName<QuoteId>, Creation, HasStartDate {

    override fun id() = id
    override fun name() = text.text
    override fun creator() = source
    override fun startDate() = date
    override fun validate(state: State) {
        validateCreator(state, source, id, date, "Source")
    }

}