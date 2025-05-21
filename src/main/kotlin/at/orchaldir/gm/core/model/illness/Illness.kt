package at.orchaldir.gm.core.model.illness

import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val LANGUAGE_TYPE = "Illness"

@JvmInline
@Serializable
value class IllnessId(val value: Int) : Id<IllnessId> {

    override fun next() = IllnessId(value + 1)
    override fun type() = LANGUAGE_TYPE
    override fun plural() = "Illnesses"
    override fun value() = value

}

@Serializable
data class Illness(
    val id: IllnessId,
    val name: Name = Name.init("Illness ${id.value}"),
    val title: NotEmptyString? = null,
    val origin: IllnessOrigin = NaturalIllness,
) : ElementWithSimpleName<IllnessId>, Creation, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun startDate() = when (origin) {
        is InventedIllness -> origin.date
        else -> null
    }

}