package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LifeStages {

    abstract fun getAppearance(age: Int): RaceAppearanceId

}

@Serializable
@SerialName("Simple")
data class SimpleAging(
    val appearance: RaceAppearanceId = RaceAppearanceId(0),
    val lifeStages: List<SimpleLifeStage>,
) : LifeStages() {

    override fun getAppearance(age: Int) = appearance

    fun getLifeStage(age: Int) = lifeStages
        .firstOrNull { it.maxAge == null || age <= it.maxAge } ?: lifeStages.last()

}

@Serializable
@SerialName("Complex")
data class ComplexAging(
    val lifeStages: List<ComplexLifeStage>,
) : LifeStages() {
    override fun getAppearance(age: Int) = getLifeStage(age).appearance

    fun getLifeStage(age: Int) = lifeStages
        .firstOrNull { it.maxAge == null || age <= it.maxAge } ?: lifeStages.last()

}

@Serializable
@SerialName("Immutable")
data class ImmutableLifeStage(
    val appearance: RaceAppearanceId = RaceAppearanceId(0),
) : LifeStages() {

    override fun getAppearance(age: Int) = appearance

}
