package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SpellOriginType {
    Invented,
    Modified,
    Translated,
    Undefined,
}

@Serializable
sealed class SpellOrigin : Creation {

    fun getType() = when (this) {
        is InventedSpell -> SpellOriginType.Invented
        is ModifiedSpell -> SpellOriginType.Modified
        is TranslatedSpell -> SpellOriginType.Translated
        UndefinedSpellOrigin -> SpellOriginType.Undefined
    }

    fun wasBasedOn(id: SpellId) = when (this) {
        is ModifiedSpell -> original == id
        is TranslatedSpell -> original == id
        else -> false
    }

    override fun creator() = when (this) {
        is InventedSpell -> inventor
        is ModifiedSpell -> inventor
        is TranslatedSpell -> inventor
        UndefinedSpellOrigin -> UndefinedCreator
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
@SerialName("Translated")
data class TranslatedSpell(
    val inventor: Creator,
    val original: SpellId,
) : SpellOrigin()

@Serializable
@SerialName("Undefined")
data object UndefinedSpellOrigin : SpellOrigin()
