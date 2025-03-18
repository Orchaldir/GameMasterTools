package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.beard.visualizeBeard

data class MouthConfig(
    private val simpleWidth: SizeConfig<Factor>,
    val simpleHeight: Factor,
    val femaleHeight: Factor,
) {
    fun getSimpleWidth(size: Size) = simpleWidth.convert(size)

    fun getWidth(mouth: Mouth): Factor {
        val width = when (mouth) {
            is FemaleMouth -> mouth.width
            NoMouth -> Size.Medium
            is NormalMouth -> mouth.width
            is Beak -> error("Beak is not supported!")
        }

        return getSimpleWidth(width)
    }

    fun getHeight(mouth: Mouth) = when (mouth) {
        is FemaleMouth -> femaleHeight
        NoMouth -> Factor(0.0f)
        is NormalMouth -> simpleHeight
        is Beak -> error("Beak is not supported!")
    }
}

fun visualizeMouth(state: CharacterRenderState, head: Head) {
    val aabb = state.aabb
    val config = state.config

    when (head.mouth) {
        NoMouth -> doNothing()
        is NormalMouth -> {
            visualizeMaleMouth(aabb, config, head.mouth, head, state)
            visualizeBeard(state, head, head.mouth.beard)
        }

        is FemaleMouth -> visualizeFemaleMouth(state, head.mouth)
        is Beak -> doNothing()
    }
}

private fun visualizeMaleMouth(
    aabb: AABB,
    config: CharacterRenderConfig,
    mouth: NormalMouth,
    head: Head,
    state: CharacterRenderState,
) {
    if (!state.renderFront) {
        return
    }

    val center = aabb.getPoint(CENTER, config.head.mouthY)
    val width = aabb.convertWidth(config.head.mouthConfig.getSimpleWidth(mouth.width))
    val height = aabb.convertHeight(config.head.mouthConfig.getHeight(head.mouth))
    val mouthAabb = AABB.fromCenter(center, Size2d(width, height))
    val option = NoBorder(Color.Black.toRender())

    state.renderer.getLayer().renderRectangle(mouthAabb, option)
}

private fun visualizeFemaleMouth(
    state: CharacterRenderState,
    mouth: FemaleMouth,
) {
    if (!state.renderFront) {
        return
    }

    val aabb = state.aabb
    val config = state.config
    val options = NoBorder(mouth.color.toRender())
    val width = config.head.mouthConfig.getSimpleWidth(mouth.width)
    val halfHeight = config.head.mouthConfig.femaleHeight * 0.5f
    val (left, right) = aabb.getMirroredPoints(width, config.head.mouthY)
    val (topLeft, topRight) =
        aabb.getMirroredPoints(width * 0.5f, config.head.mouthY - halfHeight)
    val (bottomLeft, bottomRight) =
        aabb.getMirroredPoints(width * 0.5f, config.head.mouthY + halfHeight)
    val cupidsBow = aabb.getPoint(CENTER, config.head.mouthY - halfHeight * 0.5f)
    val polygon = Polygon2d(listOf(left, bottomLeft, bottomRight, right, topRight, cupidsBow, topLeft))

    state.renderer.getLayer().renderPolygon(polygon, options)
}

