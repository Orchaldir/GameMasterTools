package at.orchaldir.gm.core.model.character

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CharacterName

@Serializable
@SerialName("Mononym")
data class Mononym(val name: String) : CharacterName()

@Serializable
@SerialName("Family")
data class FamilyName(
    val given: String,
    val middle: String?,
    val family: String,
) : CharacterName()

@Serializable
@SerialName("Patronym")
data class Patronym(val given: String) : CharacterName()

@Serializable
@SerialName("Matronym")
data class Matronym(val given: String) : CharacterName()
