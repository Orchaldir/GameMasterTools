package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val DEFAULT_MAX_AGES = listOf(2, 5, 12, 18, 45, 60, 90, 120)
val DEFAULT_OLD_AGE_HAIR_COLOR = Color.LightGray
val DEFAULT_VENERABLE_AGE_HAIR_COLOR = Color.White

private val immutable = LifeStage(Name.init("Immutable"), Int.MAX_VALUE)
private val defaultRelativeSizes = listOf(20, 40, 60, 95, 100, 100, 95, 90)
private val defaultLifeStagesMap = mutableMapOf<DefaultAging, List<LifeStage>>()

enum class LifeStagesType {
    ImmutableLifeStage,
    DefaultAging,
    SimpleAging,
}

@Serializable
sealed class LifeStages {

    fun getType() = when (this) {
        is ImmutableLifeStage -> LifeStagesType.ImmutableLifeStage
        is DefaultAging -> LifeStagesType.DefaultAging
        is SimpleAging -> LifeStagesType.SimpleAging
    }

    abstract fun contains(id: RaceAppearanceId): Boolean

    fun getRaceAppearance() = when (this) {
        is ImmutableLifeStage -> this.appearance
        is DefaultAging -> this.appearance
        is SimpleAging -> this.appearance
    }

    fun getMaxAge() = when (this) {
        is ImmutableLifeStage -> null
        is DefaultAging -> maxAges.last()
        is SimpleAging -> lifeStages.last().maxAge
    }

    fun countLifeStages() = when (this) {
        is ImmutableLifeStage -> 1
        is DefaultAging -> DefaultLifeStages.entries.size
        is SimpleAging -> lifeStages.size
    }

    fun statblock() = when (this) {
        is DefaultAging -> statblock
        is ImmutableLifeStage -> statblock
        is SimpleAging -> statblock
    }

    abstract fun getAllLifeStages(): List<LifeStage>
    abstract fun getLifeStage(age: Int): LifeStage?
    abstract fun getLifeStageStartAge(age: Int): Int
    abstract fun getRelativeSize(age: Int): Factor

}

@Serializable
@SerialName("Default")
data class DefaultAging(
    val appearance: RaceAppearanceId = RaceAppearanceId(0),
    val maxAges: List<Int> = DEFAULT_MAX_AGES,
    val oldAgeHairColor: Color? = DEFAULT_OLD_AGE_HAIR_COLOR,
    val venerableAgeHairColor: Color? = DEFAULT_VENERABLE_AGE_HAIR_COLOR,
    val statblock: Statblock = Statblock(),
) : LifeStages() {

    override fun contains(id: RaceAppearanceId) = id == appearance

    override fun getAllLifeStages() = defaultLifeStagesMap.computeIfAbsent(this) { createLifeStages() }

    override fun getLifeStage(age: Int) = getLifeStage(age, getAllLifeStages())

    override fun getLifeStageStartAge(age: Int) = getLifeStageStartAge(age, getAllLifeStages())

    override fun getRelativeSize(age: Int) = getRelativeSize(age, getAllLifeStages())

    private fun createLifeStages() = listOf(
        createLifeStage(0),
        createLifeStage(1),
        createLifeStage(2),
        createLifeStage(3),
        createLifeStage(4, true),
        createLifeStage(5, true),
        createLifeStage(6, true, oldAgeHairColor),
        createLifeStage(7, true, venerableAgeHairColor),
    )

    private fun createLifeStage(
        index: Int,
        hasBeard: Boolean = false,
        hairColor: Color? = null,
    ) = LifeStage(
        Name.init(DefaultLifeStages.entries[index].name),
        maxAges[index],
        fromPercentage(defaultRelativeSizes[index]),
        hasBeard,
        hairColor,
    )
}

@Serializable
@SerialName("Simple")
data class SimpleAging(
    val appearance: RaceAppearanceId = RaceAppearanceId(0),
    val lifeStages: List<LifeStage>,
    val statblock: Statblock = Statblock(),
) : LifeStages() {

    override fun contains(id: RaceAppearanceId) = id == appearance

    override fun getAllLifeStages() = lifeStages

    override fun getLifeStage(age: Int) = getLifeStage(age, lifeStages)

    override fun getLifeStageStartAge(age: Int) = getLifeStageStartAge(age, lifeStages)

    override fun getRelativeSize(age: Int) = getRelativeSize(age, lifeStages)

}

@Serializable
@SerialName("Immutable")
data class ImmutableLifeStage(
    val appearance: RaceAppearanceId = RaceAppearanceId(0),
    val statblock: Statblock = Statblock(),
) : LifeStages() {

    override fun contains(id: RaceAppearanceId) = id == appearance

    override fun getAllLifeStages() = listOf(immutable)

    override fun getLifeStage(age: Int) = null

    override fun getLifeStageStartAge(age: Int) = 0

    override fun getRelativeSize(age: Int) = FULL
}

private fun getLifeStage(age: Int, lifeStages: List<LifeStage>) = lifeStages
    .firstOrNull { age <= it.maxAge } ?: lifeStages.last()

private fun getLifeStageStartAge(age: Int, lifeStages: List<LifeStage>) = 1 + (lifeStages
    .lastOrNull { age > it.maxAge }?.maxAge ?: -1)

private fun getRelativeSize(age: Int, lifeStages: List<LifeStage>): Factor {
    var previousAge = 0
    var previousHeight = lifeStages.first().relativeSize * 0.5f

    if (age <= previousAge) {
        return previousHeight
    }

    lifeStages.forEach { stage ->
        if (age <= stage.maxAge) {
            val ageDiff = age - previousAge
            val maxAgeDiff = stage.maxAge - previousAge
            val factor = fromNumber(ageDiff / maxAgeDiff.toFloat())

            return previousHeight.interpolate(stage.relativeSize, factor)
        }

        previousAge = stage.maxAge
        previousHeight = stage.relativeSize
    }

    return previousHeight
}