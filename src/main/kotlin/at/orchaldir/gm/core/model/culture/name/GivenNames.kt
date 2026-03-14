package at.orchaldir.gm.core.model.culture.name

import at.orchaldir.gm.core.model.util.name.NameListId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class GivenNamesType {
    NonGendered,
    MaleAndFemale,
}

@Serializable
sealed class GivenNames {

    abstract fun contains(id: NameListId): Boolean

    abstract fun getNameLists(): Set<NameListId>

    fun getType() = when (this) {
        is NonGenderedGivenNames -> GivenNamesType.NonGendered
        is MaleAndFemaleGivenNames -> GivenNamesType.MaleAndFemale
    }
}

@Serializable
@SerialName("NonGendered")
data class NonGenderedGivenNames(
    val list: NameListId,
) : GivenNames() {

    override fun contains(id: NameListId) = id == list

    override fun getNameLists() = setOf(list)
}

@Serializable
@SerialName("MaleAndFemale")
data class MaleAndFemaleGivenNames(
    val male: NameListId,
    val female: NameListId,
    val unisex: NameListId? = null,
) : GivenNames() {

    override fun contains(id: NameListId) = male == id || female == id || unisex == id

    override fun getNameLists() = setOfNotNull(male, female, unisex)
}
