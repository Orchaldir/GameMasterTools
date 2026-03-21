package at.orchaldir.gm.core.model.rpg.dice

enum class DieType {
    D4,
    D6,
    D8,
    D10,
    D12,
    D20,
    D100;

    fun getNumber() = when (this) {
        D4 -> 4
        D6 -> 6
        D8 -> 8
        D10 -> 10
        D12 -> 12
        D20 -> 20
        D100 -> 100
    }

    fun display(dieText: String = "d") = "$dieText+${getNumber()}"
}

fun display(
    dice: Int,
    modifier: Int,
    dieText: String = "d",
): String {
    var string = if (dice != 0) {
        "$dice$dieText"
    } else {
        ""
    }

    if (modifier > 0) {
        string += "+$modifier"
    } else if (modifier < 0) {
        string += "$modifier"
    }

    return string
}