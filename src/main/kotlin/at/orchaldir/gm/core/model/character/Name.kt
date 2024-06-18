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
@SerialName("Genonym")
data class Genonym(val given: String) : CharacterName()
