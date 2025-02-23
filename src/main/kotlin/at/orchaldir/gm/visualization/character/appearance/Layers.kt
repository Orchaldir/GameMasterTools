package at.orchaldir.gm.visualization.character.appearance

const val TEXT_LAYER = 100
const val ABOVE_EQUIPMENT_LAYER = 5
const val OUTERWEAR_LAYER = 4
const val HIGHER_EQUIPMENT_LAYER = 3
const val EQUIPMENT_LAYER = 2
const val LOWER_EQUIPMENT_LAYER = 1
const val MAIN_LAYER = 0
const val BEHIND_LAYER = -20
const val WING_LAYER = -30

fun getArmLayer(layer: Int, isFront: Boolean) = if (isFront) {
    layer
} else {
    layer - 10
}
