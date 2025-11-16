package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class SimpleModifiedDice(
    val dice: Int,
    val modifier: Int,
) {
    fun display(dieType: String = "d"): String {
        var string =  if (dice != 0) {
            "$dice$dieType"
        }
        else {
            ""
        }

        if (modifier > 0) {
            string += "+$modifier"
        } else if (modifier < 0) {
            string += "$modifier"
        }

        return string
    }
}
