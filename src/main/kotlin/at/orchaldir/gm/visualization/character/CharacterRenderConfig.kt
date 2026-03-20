package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.ColorRenderConfig
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.EquipmentConfig

interface ICharacterConfig<T> {

    fun get(): T

    fun fullAABB(): AABB
    fun headAABB(): AABB
    fun torsoAABB(): AABB

    fun body(): BodyConfig
    fun equipment(): EquipmentConfig
    fun head(): HeadConfig

}

data class CharacterRenderConfig(
    val padding: Distance,
    val body: BodyConfig,
    val equipment: EquipmentConfig,
    val head: HeadConfig,
    val colors: ColorRenderConfig,
) {

    fun calculateSize(height: Distance) = Size2d.square(height + padding * 2.0f)

    fun getHairLength(config: ICharacterConfig<Head>, length: HairLength) =
        body.getDistanceFromNeckToBottom(config.headAABB()) *
                head.hair.getLength(length)

}