package at.orchaldir.gm.core.model.item.equipment.style

enum class FootwearStyle {
    Boots,
    KneeHighBoots,
    Pumps,
    Sandals,
    Shoes,
    Slippers;

    fun hasShaft() = this != Sandals && this != Slippers

    fun hasSole() = this != Pumps

    fun isFootVisible(fromFront: Boolean) = when (this) {
        Sandals -> false
        Slippers -> fromFront
        else -> true
    }
}