package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LifeStages {

    abstract fun contains(id: RaceAppearanceId): Boolean
    abstract fun getAppearance(age: Int): RaceAppearanceId
    abstract fun getLifeStageName(age: Int): String?
    abstract fun getRelativeHeight(age: Int): Factor

}

@Serializable
@SerialName("Simple")
data class SimpleAging(
    val appearance: RaceAppearanceId = RaceAppearanceId(0),
    val lifeStages: List<SimpleLifeStage>,
) : LifeStages() {

    override fun contains(id: RaceAppearanceId) = id == appearance

    override fun getAppearance(age: Int) = appearance

    override fun getLifeStageName(age: Int) = getLifeStage(age).name

    override fun getRelativeHeight(age: Int) = getRelativeHeight(age, lifeStages)

    fun getLifeStage(age: Int) = getLifeStage(age, lifeStages)

}

@Serializable
@SerialName("Complex")
data class ComplexAging(
    val lifeStages: List<ComplexLifeStage>,
) : LifeStages() {

    override fun contains(id: RaceAppearanceId) = lifeStages.any { it.appearance == id }

    override fun getAppearance(age: Int) = getLifeStage(age).appearance

    override fun getLifeStageName(age: Int) = getLifeStage(age).name

    override fun getRelativeHeight(age: Int) = getRelativeHeight(age, lifeStages)

    fun getLifeStage(age: Int) = getLifeStage(age, lifeStages)

}

@Serializable
@SerialName("Immutable")
data class ImmutableLifeStage(
    val appearance: RaceAppearanceId = RaceAppearanceId(0),
) : LifeStages() {

    override fun contains(id: RaceAppearanceId) = id == appearance

    override fun getAppearance(age: Int) = appearance

    override fun getLifeStageName(age: Int) = null
    override fun getRelativeHeight(age: Int) = FULL
}

private fun <T : LifeStage> getLifeStage(age: Int, lifeStages: List<T>) = lifeStages
    .firstOrNull { age <= it.maxAge() } ?: lifeStages.last()

private fun <T : LifeStage> getRelativeHeight(age: Int, lifeStages: List<T>): Factor {
    var previousAge = 0
    var previousHeight = lifeStages.first().relativeHeight() * 0.5f

    lifeStages.forEach { stage ->
        if (age <= stage.maxAge()) {
            val ageDiff = age - previousAge
            val maxAgeDiff = stage.maxAge() - previousAge
            val factor = Factor(ageDiff / maxAgeDiff.toFloat())

            return previousHeight.interpolate(stage.relativeHeight(), factor)
        }

        previousAge = stage.maxAge()
        previousHeight = stage.relativeHeight()
    }

    return previousHeight
}
