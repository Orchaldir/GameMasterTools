package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.character.appearance.hair.HairColor
import at.orchaldir.gm.core.model.character.appearance.hair.NoHairColor
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

const val LIFE_STAGE_TYPE = "Life Stage"

@JvmInline
@Serializable
value class LifeStageId(val value: Int) : Id<LifeStageId> {

    override fun next() = LifeStageId(value + 1)
    override fun type() = LIFE_STAGE_TYPE
    override fun value() = value

}

@Serializable
data class LifeStage(
    val name: Name,
    val maxAge: Int,
    val relativeSize: Factor = FULL,
    val hasBeard: Boolean = false,
    val hairColor: HairColor = NoHairColor,
)
