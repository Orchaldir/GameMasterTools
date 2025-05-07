package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.magic.SpellId
import kotlinx.serialization.Serializable

@Serializable
data class AbstractChapter(
    val pages: Int,
    val spells: Set<SpellId> = emptySet(),
)