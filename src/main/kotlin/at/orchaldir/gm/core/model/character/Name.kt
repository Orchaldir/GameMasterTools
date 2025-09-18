package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.util.name.Name
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CharacterName {
    
    fun toSortString() = when (this) {
        is FamilyName -> family.text + given.text + middle?.text
        is Genonym -> given.text
        is Mononym -> name.text
    }.lowercase()
    
}

@Serializable
@SerialName("Mononym")
data class Mononym(val name: Name) : CharacterName() {

    companion object {
        fun init(name: String) = Mononym(Name.init(name))
    }

}

@Serializable
@SerialName("Family")
data class FamilyName(
    val given: Name,
    val middle: Name?,
    val family: Name,
) : CharacterName()

@Serializable
@SerialName("Genonym")
data class Genonym(val given: Name) : CharacterName()
