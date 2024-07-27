package at.orchaldir.gm.core.model.item.style

enum class FootwearStyle {
    Boots,
    KneeHighBoots,
    Pumps,
    Sandals,
    Shoes,
    Slippers;

    fun hasHeel() = this == Pumps

    fun hasShaft() = this != Pumps

    fun hasSole() = this != Pumps

    fun isFootVisible(fromFront: Boolean) = when (this) {
        Sandals -> false
        Slippers -> fromFront
        else -> true
    }
}