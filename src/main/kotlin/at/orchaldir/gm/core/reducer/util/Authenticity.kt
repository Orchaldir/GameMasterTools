package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.doNothing

fun checkAuthenticity(
    state: State,
    authenticity: Authenticity,
) = when (authenticity) {
    Authentic, Invented, UndefinedAuthenticity -> doNothing()
    is MaskOfOtherGod -> state.getGodStorage().require(authenticity.god) {
        "Cannot be the mask of unknown ${authenticity.god.print()}!"
    }
}