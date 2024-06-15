package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.core.model.character.Gender
import kotlinx.serialization.Serializable

@Serializable
data class GenderMap<T>(
    val female: T,
    val genderless: T,
    val male: T,
) {
    fun get(gender: Gender) = when (gender) {
        Gender.Female -> female
        Gender.Genderless -> genderless
        Gender.Male -> male
    }
}