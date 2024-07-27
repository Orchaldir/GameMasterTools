package at.orchaldir.gm.core.model.item.style

enum class FootwearStyle {
    Boots,
    KneeHighBoots,
    Pumps,
    Sandals,
    Shoes,
    Slippers;

    fun hasSole() = this != Pumps

    fun isFootVisible(fromFront: Boolean) = when (this) {
        Sandals -> false
        Slippers -> fromFront
        else -> true
    }
}