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
    val lifeStages: List<SimpleLifeStage>,
) : Aging() {

    override fun getAppearance(age: Int) = appearance

    fun getLifeStage(age: Int) = lifeStages
        .firstOrNull { it.maxAge == null || age <= it.maxAge } ?: lifeStages.last()

}

@Serializable
@SerialName("Complex")
data class ComplexAging(
    val lifeStages: List<ComplexLifeStage>,
) : Aging() {
    override fun getAppearance(age: Int) = getLifeStage(age).appearance

    fun getLifeStage(age: Int) = lifeStages
        .firstOrNull { it.maxAge == null || age <= it.maxAge } ?: lifeStages.last()

}
