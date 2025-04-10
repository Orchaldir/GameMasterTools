package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOrnament
import at.orchaldir.gm.visualization.character.equipment.part.visualizeJewelryLine

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

    fun getThicknessFactor(line: JewelryLine) = when (line) {
        is OrnamentLine -> ornamentThickness
        else -> wireThickness
    }.convert(line.getSizeOfSub())

    fun getWireThickness(aabb: AABB, line: JewelryLine) = aabb.convertHeight(getThicknessFactor(line))
}

fun visualizeNecklace(
    state: CharacterRenderState,
    body: Body,
    necklace: Necklace,
) {
    val torso = state.config.body.getTorsoAabb(state.aabb, body)

    when (necklace.style) {
        is DangleNecklace -> visualizeDangleNecklace(state, torso, necklace.style, necklace.length)
        is DropNecklace -> visualizeDropNecklace(state, torso, necklace.style, necklace.length)
        is PendantNecklace -> visualizePendantNecklace(state, torso, necklace.style, necklace.length)
        is StrandNecklace -> visualizeStrandNecklace(state, torso, necklace.style, necklace.length)
    }
}

private fun visualizeDangleNecklace(
    state: CharacterRenderState,
    torso: AABB,
    style: DangleNecklace,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)
    val start = torso.getPoint(CENTER, bottomY)
    val maxLength = torso.convertWidth(config.maxLength)

    visualizeJewelryLineOfNecklace(state, torso, style.line, length)
    visualizeDangleEarring(state, style.dangle, start, maxLength / 2.0f)
}

private fun visualizeDropNecklace(
    state: CharacterRenderState,
    torso: AABB,
    style: DropNecklace,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)
    val start = torso.getPoint(CENTER, bottomY)
    val maxLength = torso.convertWidth(config.maxLength)

    visualizeJewelryLineOfNecklace(state, torso, style.line, length)
    visualizeDropEarring(state, style.drop, start, maxLength / 2.0f, maxLength, ABOVE_EQUIPMENT_LAYER)
}

private fun visualizePendantNecklace(
    state: CharacterRenderState,
    torso: AABB,
    style: PendantNecklace,
    length: NecklaceLength,
) {
    val bottomY = state.config.equipment.necklace.getLength(length)
    val size = state.config.equipment.necklace.pendantSize.convert(style.size)
    val radius = size / 2.0f
    val center = torso.getPoint(CENTER, bottomY + radius)

    visualizeJewelryLineOfNecklace(state, torso, style.line, length)
    visualizeOrnament(state.getLayer(ABOVE_EQUIPMENT_LAYER), style.ornament, center, torso.convertHeight(radius))
}

private fun visualizeStrandNecklace(
    state: CharacterRenderState,
    torso: AABB,
    necklace: StrandNecklace,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)

    repeat(necklace.strands) { index ->
        val thicknessFactor = config.getThicknessFactor(necklace.line)
        val thickness = torso.convertHeight(thicknessFactor)
        val padding = thicknessFactor * config.strandPadding.convert(necklace.padding)
        val line = createNecklaceLine(torso, length, bottomY, padding * index.toFloat())
        val roundedLine = subdivideLine(line, config.subdivisions)

        visualizeJewelryLine(state.getLayer(ABOVE_EQUIPMENT_LAYER), necklace.line, roundedLine, thickness)
    }
}

private fun visualizeJewelryLineOfNecklace(
    state: CharacterRenderState,
    torso: AABB,
    jewelryLine: JewelryLine,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)
    val line = createNecklaceLine(torso, length, bottomY)
    val roundedLine = subdivideLine(line, config.subdivisions)
    val thickness = config.getWireThickness(torso, jewelryLine)

    visualizeJewelryLine(state.getLayer(ABOVE_EQUIPMENT_LAYER), jewelryLine, roundedLine, thickness)
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
