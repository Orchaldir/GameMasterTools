package at.orchaldir.gm.core.model.item.style

enum class FootwearStyle {
    Boots,
    KneeHighBoots,
    Sandals,
    Shoes,
    Slippers;

    fun isFootVisible(fromFront: Boolean) = when (this) {
        Sandals -> false
        Slippers -> fromFront
        else -> true
    }
}