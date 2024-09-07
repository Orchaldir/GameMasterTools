package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.AppearanceOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Aging {

    abstract fun getAppearance(age: Int): AppearanceOptions

}

@Serializable
@SerialName("Simple")
data class SimpleAging(
    val appearance: AppearanceOptions = AppearanceOptions(),
    val ageCategory: List<SimpleAgeCategory>,
) : Aging() {

    override fun getAppearance(age: Int) = appearance

    fun getAgeCategory(age: Int) = ageCategory
        .firstOrNull { it.maxAge == null || age <= it.maxAge } ?: ageCategory.last()

}

@Serializable
@SerialName("Complex")
data class ComplexAging(
    val ageCategory: List<ComplexAgeCategory>,
) : Aging() {
    override fun getAppearance(age: Int) = getAgeCategory(age).appearance

    fun getAgeCategory(age: Int) = ageCategory
        .firstOrNull { it.maxAge == null || age <= it.maxAge } ?: ageCategory.last()

}
