package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.utils.Id

fun <ID : Id<ID>> validateCanDelete(canDelete: Boolean, id: ID) {
    require(canDelete) { "Cannot delete ${id.type()} ${id.value()}, because it is used!" }
}