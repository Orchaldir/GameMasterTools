package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.Gender.*
import kotlinx.serialization.Serializable

@Serializable
data class GenderMap<T>(
    val female: T,
    val genderless: T,
    val male: T,
) {
    constructor(value: T) : this(value, value, value)

    fun get(gender: Gender) = when (gender) {
        Female -> female
        Genderless -> genderless
        Male -> male
    }

    fun getMap() = mapOf(Female to female, Genderless to genderless, Male to male)

    fun getValues() = setOf(female, genderless, male)

    fun contains(value: T) = female == value || genderless == value || male == value
}