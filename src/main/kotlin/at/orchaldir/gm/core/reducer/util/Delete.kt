package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.utils.Id

fun <ID : Id<ID>> validateCanDelete(canDelete: Boolean, id: ID) =
    validateCanDelete(canDelete, id, "it is used")

fun <ID : Id<ID>> validateCanDelete(count: Int, id: ID, reason: String) {
    validateCanDelete(count == 0, id, reason)
}

fun <ID : Id<ID>> validateCanDelete(canDelete: Boolean, id: ID, reason: String) {
    require(canDelete) { "Cannot delete ${id.print()}, because $reason!" }
}