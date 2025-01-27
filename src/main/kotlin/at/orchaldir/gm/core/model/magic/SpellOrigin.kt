package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SpellOriginType {
    Invented,
    Modified,
    Undefined,
}

@Serializable
sealed class SpellOrigin {

    fun getType() = when (this) {
        is InventedSpell -> SpellOriginType.Invented
        is ModifiedSpell -> SpellOriginType.Modified
        UndefinedSpellOrigin -> SpellOriginType.Undefined
    }

    fun <ID : Id<ID>> wasCreatedBy(id: ID) = when (this) {
        is InventedSpell -> inventor.isId(id)
        is ModifiedSpell -> inventor.isId(id)
        UndefinedSpellOrigin -> false
    }

}

@Serializable
@SerialName("Invented")
data class InventedSpell(val inventor: Creator) : SpellOrigin()

@Serializable
@SerialName("Modified")
data class ModifiedSpell(
    val inventor: Creator,
    val original: SpellId,
) : SpellOrigin()

@Serializable
@SerialName("Undefined")
data object UndefinedSpellOrigin : SpellOrigin()
