package at.orchaldir.gm.core.model

data class CannotDeleteException(
    val result: DeleteResult,
) : Exception("Cannot delete ${result.id.print()} because of ${result.countElements()} elements!")
