package at.orchaldir.gm.core.model.util.name

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element

interface ElementWithSimpleName<ID> : Element<ID> {
    fun name(): String

    override fun name(state: State) = name()
}