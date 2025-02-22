package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.NoEyes
import at.orchaldir.gm.core.model.character.appearance.eye.OneEye
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.calculateSize
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance

fun renderCharacterTable(
    filename: String,
    config: CharacterRenderConfig,
    appearances: List<List<Appearance>>,
) {
    val size = calculateSize(config, appearances[0][0])
    renderTable(filename, size, appearances) { aabb, renderer, appearance ->
        val state = CharacterRenderState(aabb, config, renderer, true, emptyList())

        visualizeAppearance(state, appearance)
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
    val height = Distance(200)
    val size = config.calculateSize(height)

    renderTable(filename, size, rows, columns, backToo) { aabb, renderer, renderFront, column, row ->
        val (appearance, equipment) = create(height, column, row)
        val state = CharacterRenderState(aabb, config, renderer, renderFront, equipment)

        visualizeAppearance(state, appearance)
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
