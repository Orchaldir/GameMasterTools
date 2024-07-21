package at.orchaldir.gm.core.model.item.style

import at.orchaldir.gm.core.model.item.style.NecklineStyle.*

val NECKLINES_WITH_SLEEVES = setOf(Crew, None, V, DeepV, VeryDeepV)

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

    fun supportsSleeves() = NECKLINES_WITH_SLEEVES.contains(this)

    fun getSupportsSleevesStyles() = if (supportsSleeves()) {
        SleeveStyle.entries
    } else {
        setOf(SleeveStyle.None)
    }
}