package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.NoEyes
import at.orchaldir.gm.core.model.character.appearance.eye.OneEye
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import at.orchaldir.gm.visualization.character.appearance.calculateSize
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance

fun renderCharacterTable(
    filename: String,
    config: CharacterRenderConfig,
    appearances: List<List<Appearance>>,
) {
    val paddedSizeMap = mutableMapOf<Appearance, PaddedSize>()
    val size = appearances.fold(Size2d.square(0.001f)) { rowSize, list ->
        list.fold(rowSize) { columnSize, appearance ->
            val paddedSize = calculateSize(config, appearance)
            paddedSizeMap[appearance] = paddedSize
            columnSize.max(paddedSize.getFullSize())
        }
    }

    renderTable(filename, size, appearances) { aabb, renderer, appearance ->
        val state = CharacterRenderState(aabb, config, renderer, true, emptyList())

        visualizeAppearance(state, appearance, paddedSizeMap.getValue(appearance))
    }
}

fun renderCharacterTable(
    filename: String,
    config: CharacterRenderConfig,
    appearance: Appearance,
    equipmentTable: List<List<EquipmentData>>,
) {
    val paddedSize = calculateSize(config, appearance)

    renderTable(filename, paddedSize.getFullSize(), equipmentTable) { aabb, renderer, equipment ->
        val state = CharacterRenderState(aabb, config, renderer, true, listOf(equipment))

        visualizeAppearance(state, appearance, paddedSize)
    }
}

fun <C, R> renderCharacterTable(
    filename: String,
    config: CharacterRenderConfig,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    backToo: Boolean = false,
    create: (Distance, C, R) -> Pair<Appearance, List<EquipmentData>>,
) {
    val height = fromMillimeters(2000)
    val dataMap = mutableMapOf<Pair<R, C>, Triple<Appearance, List<EquipmentData>, PaddedSize>>()
    var maxSize = Size2d.square(0.001f)

    rows.forEach { (_, row) ->
        columns.forEach { (_, column) ->
            val data = create(height, column, row)
            val paddedSize = calculateSize(config, data.first)
            val size = paddedSize.getFullSize()

            dataMap[Pair(row, column)] = Triple(data.first, data.second, paddedSize)
            maxSize = maxSize.max(size)
        }
    }

    renderTable(filename, maxSize, rows, columns, backToo) { aabb, renderer, renderFront, column, row ->
        val (appearance, equipment, paddedSize) = dataMap.getValue(Pair(row, column))
        val state = CharacterRenderState(aabb, config, renderer, renderFront, equipment)

        visualizeAppearance(state, appearance, paddedSize)
    }
}

fun addNamesToBeardStyle(values: List<BeardStyle>) = values.map {
    Pair(
        when (it) {
            is Goatee -> it.goateeStyle.name
            is GoateeAndMoustache -> "${it.goateeStyle.name} + ${it.moustacheStyle.name}"
            is Moustache -> "${it.moustacheStyle.name} Moustache"
            ShavedBeard -> "Shaved"
        }, it
    )
}

fun addNamesToEyes(values: List<Eyes>) = values.map {
    Pair(
        when (it) {
            NoEyes -> "No Eyes"
            is OneEye -> "${it.size} Eye"
            is TwoEyes -> "Two Eyes"
        }, it
    )
}
