package at.orchaldir.gm.core.model.item.style

enum class NecklineStyle {
    Asymmetrical,
    Crew,
    None,
    Strapless,
    V,
    DeepV,
    VeryDeepV;

    fun addTop() = when (this) {
        Asymmetrical, Strapless -> false
        else -> true
    }
}