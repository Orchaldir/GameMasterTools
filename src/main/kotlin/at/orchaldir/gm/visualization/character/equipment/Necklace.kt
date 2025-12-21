package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeLineStyle
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOrnament

data class NecklaceConfig(
    val maxLength: Factor,
    private val lengthMap: Map<NecklaceLength, Factor>,
    val pendantSize: SizeConfig<Factor>,
    val strandPadding: SizeConfig<Factor>,
    private val ornamentThickness: SizeConfig<Factor>,
    private val wireThickness: SizeConfig<Factor>,
    val subdivisions: Int,
) {
    fun getLength(length: NecklaceLength) = lengthMap.getValue(length)

    fun getThicknessFactor(line: LineStyle) = when (line) {
        is OrnamentLine -> ornamentThickness
        else -> wireThickness
    }.convert(line.getSizeOfSub())

    fun getWireThickness(aabb: AABB, line: LineStyle) = aabb.convertHeight(getThicknessFactor(line))
}

fun visualizeNecklace(
    state: CharacterRenderState<Body>,
    necklace: Necklace,
) {
    val torso = state.torsoAABB()

    when (necklace.style) {
        is DangleNecklace -> visualizeDangleNecklace(state, torso, necklace.style, necklace.length)
        is DropNecklace -> visualizeDropNecklace(state, torso, necklace.style, necklace.length)
        is PendantNecklace -> visualizePendantNecklace(state, torso, necklace.style, necklace.length)
        is StrandNecklace -> visualizeStrandNecklace(state, torso, necklace.style, necklace.length)
    }
}

private fun visualizeDangleNecklace(
    state: CharacterRenderState<Body>,
    torso: AABB,
    style: DangleNecklace,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)
    val start = torso.getPoint(CENTER, bottomY)
    val maxLength = torso.convertWidth(config.maxLength)

    if (state.renderFront) {
        visualizeJewelryLineOfNecklace(state, torso, style.line, length)
        visualizeDangleEarring(state, style.dangle, start, maxLength / 2.0f)
    } else {
        visualizeJewelryLineOfNecklace(state, torso, style.line, NecklaceLength.Collar)
    }
}

private fun visualizeDropNecklace(
    state: CharacterRenderState<Body>,
    torso: AABB,
    style: DropNecklace,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)
    val start = torso.getPoint(CENTER, bottomY)
    val maxLength = torso.convertWidth(config.maxLength)

    if (state.renderFront) {
        visualizeJewelryLineOfNecklace(state, torso, style.line, length)
        visualizeDropEarring(state, style.drop, start, maxLength / 2.0f, maxLength, ABOVE_EQUIPMENT_LAYER)
    } else {
        visualizeJewelryLineOfNecklace(state, torso, style.line, NecklaceLength.Collar)
    }
}

private fun visualizePendantNecklace(
    state: CharacterRenderState<Body>,
    torso: AABB,
    style: PendantNecklace,
    length: NecklaceLength,
) {
    val bottomY = state.config.equipment.necklace.getLength(length)
    val size = state.config.equipment.necklace.pendantSize.convert(style.size)
    val radius = size / 2.0f
    val center = torso.getPoint(CENTER, bottomY + radius)

    if (state.renderFront) {
        val renderer = state.getLayer(ABOVE_EQUIPMENT_LAYER)

        visualizeJewelryLineOfNecklace(state, torso, style.line, length)
        visualizeOrnament(state, renderer, style.ornament, center, torso.convertHeight(radius))
    } else {
        visualizeJewelryLineOfNecklace(state, torso, style.line, NecklaceLength.Collar)
    }
}

private fun visualizeStrandNecklace(
    state: CharacterRenderState<Body>,
    torso: AABB,
    style: StrandNecklace,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)

    if (state.renderFront) {
        repeat(style.strands) { index ->
            val thicknessFactor = config.getThicknessFactor(style.line)
            val thickness = torso.convertHeight(thicknessFactor)
            val padding = thicknessFactor * config.strandPadding.convert(style.padding)
            val line = createNecklaceLine(torso, length, bottomY, padding * index.toFloat())
            val roundedLine = subdivideLine(line, config.subdivisions)
            val renderer = state.getLayer(ABOVE_EQUIPMENT_LAYER)

            visualizeLineStyle(state, renderer, style.line, roundedLine, thickness)
        }
    } else {
        visualizeJewelryLineOfNecklace(state, torso, style.line, NecklaceLength.Collar)
    }
}

private fun visualizeJewelryLineOfNecklace(
    state: CharacterRenderState<Body>,
    torso: AABB,
    jewelryLine: LineStyle,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)
    val line = createNecklaceLine(torso, length, bottomY)
    val roundedLine = subdivideLine(line, config.subdivisions)
    val thickness = config.getWireThickness(torso, jewelryLine)
    val renderer = state.renderer.getLayer(ABOVE_EQUIPMENT_LAYER)

    visualizeLineStyle(state, renderer, jewelryLine, roundedLine, thickness)
}

private fun createNecklaceLine(
    torso: AABB,
    length: NecklaceLength,
    bottomY: Factor,
    padding: Factor = ZERO,
): Line2d {
    val width = HALF + padding * 2.0f
    val paddingDistance = torso.convertWidth(padding)
    val (left, right) = torso.getMirroredPoints(width, START)
    val builder = Line2dBuilder()
        .addPoint(left)

    when (length) {
        NecklaceLength.Collar, NecklaceLength.Choker -> {
            val point = torso
                .getPoint(CENTER, bottomY)
                .addHeight(paddingDistance)
            builder.addPoint(point)
        }

        else -> {
            val (bottomLeft, bottomRight) = torso.getMirroredPoints(width, bottomY)
            builder
                .addPoint(bottomLeft.addHeight(paddingDistance))
                .addPoint(bottomRight.addHeight(paddingDistance))
        }
    }

    return builder
        .addPoint(right)
        .build()
}
