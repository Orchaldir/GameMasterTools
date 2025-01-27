package at.orchaldir.gm.app.html.model.magic

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import io.ktor.http.*
import io.ktor.server.util.*

// show

// edit

// parse

fun parseSpell(parameters: Parameters, id: SpellId) = Spell(
    id,
    parameters.getOrFail(NAME),
)
