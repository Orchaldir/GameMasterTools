package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.NoEyes
import at.orchaldir.gm.core.model.character.appearance.eye.OneEye
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import at.orchaldir.gm.visualization.character.appearance.calculatePaddedSize
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance

private val MIN_SIZE = Size2d.square(fromMillimeters(1))

fun renderCharacterTable(
    state: State,
    filename: String,
    config: CharacterRenderConfig,
    appearances: List<List<Appearance>>,
) {
    val paddedSizeMap = mutableMapOf<Appearance, PaddedSize>()
    val size = appearances.fold(MIN_SIZE) { rowSize, list ->
        list.fold(rowSize) { columnSize, appearance ->
            val paddedSize = calculatePaddedSize(config, appearance)
            paddedSizeMap[appearance] = paddedSize
            columnSize.max(paddedSize.getFullSize())
        }
    }

    renderTable(filename, size, appearances) { renderAabb, renderer, appearance ->
        val fullAabb = paddedSizeMap[appearance]!!.getInnerAABB(renderAabb)
        val renderState = CharacterRenderState(state, appearance, fullAabb, config, renderer, true, EquipmentMap())

        visualizeAppearance(renderState)
    }
}

fun renderEquipmentDataTable(
    state: State,
    filename: String,
    config: CharacterRenderConfig,
    appearance: Appearance,
    equipmentTable: List<List<EquipmentData>>,
) = renderCharacterTableWithoutColorScheme(
    state,
    filename,
    config,
    appearance,
    equipmentTable.map { row ->
        row.map {
            EquipmentMap.from(it)
        }
    },
)

fun renderCharacterTableWithoutColorScheme(
    state: State,
    filename: String,
    config: CharacterRenderConfig,
    appearance: Appearance,
    equipmentTable: List<List<EquipmentDataMap>>,
) = renderCharacterTable(
    state,
    filename,
    config,
    appearance,
    equipmentTable.map { list ->
        list.map { map ->
            map.addColors { data -> EquipmentDataPair(data, UndefinedColors) }
        }
    },
)

fun renderCharacterTable(
    state: State,
    filename: String,
    config: CharacterRenderConfig,
    appearance: Appearance,
    equipmentTable: List<List<EquipmentElementMap>>,
) {
    val paddedSize = calculatePaddedSize(config, appearance)

    renderTable(filename, paddedSize.getFullSize(), equipmentTable) { aabb, renderer, equipmentMap ->
        val renderState = CharacterRenderState(state, appearance, aabb, config, renderer, true, equipmentMap)

        visualizeAppearance(renderState)
    }
}

fun <C, R> renderCharacterTableWithoutColorScheme(
    state: State,
    filename: String,
    config: CharacterRenderConfig,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    backToo: Boolean = false,
    create: (Distance, C, R) -> Pair<Appearance, EquipmentDataMap>,
) = renderCharacterTable(
    state,
    filename,
    config,
    rows,
    columns,
    backToo,
) { distance, row, column ->
    val (appearance, equipmentMap) = create(distance, row, column)

    Pair(appearance, equipmentMap.addColors { data -> Pair(data, UndefinedColors) })
}

fun <C, R> renderCharacterTable(
    state: State,
    filename: String,
    config: CharacterRenderConfig,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    backToo: Boolean = false,
    create: (Distance, C, R) -> Pair<Appearance, EquipmentElementMap>,
) {
    val height = fromMillimeters(2000)
    mutableMapOf<Pair<R, C>, Triple<Appearance, EquipmentElementMap, PaddedSize>>()

    renderTable(
        filename,
        rows,
        columns,
        MIN_SIZE,
        backToo,
        { column, row ->
            val data = create(height, column, row)
            val paddedSize = calculatePaddedSize(config, data.first, data.second)

            Pair(data, paddedSize)
        },
        { renderAabb, renderer, renderFront, data ->
            val (appearance, equipment) = data
            val renderState =
                CharacterRenderState(state, appearance, renderAabb, config, renderer, renderFront, equipment)

            visualizeAppearance(renderState)
        }
    )
}

fun addNamesToBeardStyle(values: List<BeardStyle>) = values.map {
    Pair(
        when (it) {
            is FullBeard -> "${it.style} + Full Beard"
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
