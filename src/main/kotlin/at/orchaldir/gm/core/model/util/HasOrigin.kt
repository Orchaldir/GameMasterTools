package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.util.origin.Origin

interface HasOrigin : Creation {
    fun origin(): Origin
}