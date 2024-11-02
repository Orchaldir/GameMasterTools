package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element

interface ElementWithSimpleName<ID> : Element<ID> {
    fun name(): String
}

interface ElementWithComplexName<ID> : Element<ID> {
    fun name(state: State): String
}