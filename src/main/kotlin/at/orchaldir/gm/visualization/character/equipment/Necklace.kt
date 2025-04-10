package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOrnament

data class NecklaceConfig(
    private val lengthMap: Map<NecklaceLength, Factor>,
    val pendantSize: SizeConfig<Factor>,
    val strandPadding: SizeConfig<Factor>,
    val wireThickness: SizeConfig<Factor>,
) {
    fun getLength(length: NecklaceLength) = lengthMap.getValue(length)

    fun getWireThickness(aabb: AABB, thickness: Size) = aabb.convertHeight(wireThickness.convert(thickness))
}

fun visualizeNecklace(
    state: CharacterRenderState,
    body: Body,
    necklace: Necklace,
) {
    val torso = state.config.body.getTorsoAabb(state.aabb, body)

    when (necklace.style) {
        is DangleNecklace -> {
            visualizeStrand(state, torso, necklace.style.strand, necklace.length)
        }

        is DropNecklace -> visualizeDropNecklace(state, torso, necklace.style, necklace.length)
        is PendantNecklace -> visualizePendantNecklace(state, torso, necklace.style, necklace.length)
        is StrandNecklace -> visualizeStrandNecklace(state, torso, necklace.style, necklace.length)
    }
}

private fun visualizeDropNecklace(
    state: CharacterRenderState,
    torso: AABB,
    style: DropNecklace,
    length: NecklaceLength,
) {
    val bottomY = state.config.equipment.necklace.getLength(length)
    val start = torso.getPoint(CENTER, bottomY)
    val earRadius = torso.convertWidth(HALF)
    val maxLength = Distance.fromMeters(torso.getEnd().y - start.y)

    visualizeStrand(state, torso, style.strand, length)
    visualizeDropEarring(state, style.drop, start, earRadius, maxLength)
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

    visualizeStrand(state, torso, style.strand, length)
    visualizeOrnament(state, style.ornament, center, torso.convertHeight(radius))
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
        val size = necklace.strand.getSizeOfSub()
        val strandThickness = config.wireThickness.convert(size)
        val padding = strandThickness * config.strandPadding.convert(necklace.padding)
        val line = createNecklaceLine(torso, length, bottomY, padding * index.toFloat())
        val roundedLine = subdivideLine(line, 2)

        visualizeStrand(state, torso, necklace.strand, roundedLine)
    }
}

private fun visualizeStrand(
    state: CharacterRenderState,
    torso: AABB,
    strand: Strand,
    length: NecklaceLength,
) {
    val config = state.config.equipment.necklace
    val bottomY = config.getLength(length)
    val line = createNecklaceLine(torso, length, bottomY)
    val roundedLine = subdivideLine(line, 2)

    visualizeStrand(state, torso, strand, roundedLine)
}

private fun visualizeStrand(
    state: CharacterRenderState,
    torso: AABB,
    strand: Strand,
    line: Line2d,
) {
    val renderer = state.getLayer(ABOVE_EQUIPMENT_LAYER)

    when (strand) {
        is Chain -> {
            val wireThickness = state.config.equipment.necklace.getWireThickness(torso, strand.thickness)
            val wireOptions = LineOptions(strand.color.toRender(), wireThickness)
            renderer.renderLine(line, wireOptions)
        }

        is OrnamentChain -> {
            val size = state.config.equipment.necklace.getWireThickness(torso, strand.size)
            val radius = size / 2.0f

            calculatePointsOnLine(line, size).forEach { center ->
                visualizeOrnament(state, strand.ornament, center, radius)
            }
        }
        is Wire -> {
            val wireThickness = state.config.equipment.necklace.getWireThickness(torso, strand.thickness)
            val wireOptions = LineOptions(strand.color.toRender(), wireThickness)
            renderer.renderLine(line, wireOptions)
        }
    }
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
