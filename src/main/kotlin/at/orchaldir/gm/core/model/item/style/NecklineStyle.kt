package at.orchaldir.gm.core.model.item.style

enum class NecklineStyle {
    Asymmetrical,
    Crew,
    Halter,
    None,
    Strapless,
    V,
    DeepV,
    VeryDeepV;

    fun addTop() = when (this) {
        Asymmetrical, Halter, Strapless -> false
        else -> true
    }

    fun renderBack() = this == Asymmetrical
}