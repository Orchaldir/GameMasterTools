package at.orchaldir.gm.core.model.item.style

enum class NecklineStyle {
    Crew,
    None,
    Strapless,
    V,
    DeepV,
    VeryDeepV;

    fun addTop() = this != Strapless
}