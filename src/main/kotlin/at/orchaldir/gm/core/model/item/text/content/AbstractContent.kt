package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.SpellId
import kotlinx.serialization.Serializable

@Serializable
data class AbstractContent(
    val pages: Int = 100,
    val spells: Set<SpellId> = emptySet(),
)