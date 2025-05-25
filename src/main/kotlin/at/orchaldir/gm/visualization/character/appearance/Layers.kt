package at.orchaldir.gm.visualization.character.appearance

const val TEXT_LAYER = 100
const val HELD_EQUIPMENT_LAYER = 70
const val MOUTH_LAYER = 60
const val ABOVE_EQUIPMENT_LAYER = 50
const val OUTERWEAR_LAYER = 45
const val JACKET_LAYER = 40
const val TIE_LAYER = 35
const val HIGHER_EQUIPMENT_LAYER = 30
const val EQUIPMENT_LAYER = 20
const val LOWER_EQUIPMENT_LAYER = 10
const val MAIN_LAYER = 0
const val BEHIND_LAYER = -20
const val WING_LAYER = -60
const val HAIR_LAYER = -70

fun getArmLayer(layer: Int, isFront: Boolean) = if (isFront) {
    layer
} else {
    layer - 10
}
