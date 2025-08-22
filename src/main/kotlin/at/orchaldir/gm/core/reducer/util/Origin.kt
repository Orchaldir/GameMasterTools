package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.origin.*
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun <ID : Id<ID>> checkOrigin(
    state: State,
    id: ID,
    origin: Origin,
    date: Date?,
    createId: (Int) -> ID,
) {
    when (origin) {
        is BornElement -> if (id is CharacterId) {
            checkCharacterParent(state, id, origin.father, Gender.Male, date, "Father")
            checkCharacterParent(state, id, origin.mother, Gender.Female, date, "Mother")
        } else {
            error("BornElement is only supported by characters!")
        }

        is CombinedElement -> {
            checkCreator(state, id, origin.creator, date)
            origin.parents.forEach { parent ->
                checkParent(state, id, createId(parent), date)
            }
        }

        is CreatedElement -> checkCreator(state, id, origin.creator, date)
        is ModifiedElement -> checkOrigin(state, id, origin.modifier, createId(origin.parent), date)
        is EvolvedElement -> checkParent(state, id, createId(origin.parent), date)
        is TranslatedElement -> checkOrigin(state, id, origin.translator, createId(origin.parent), date)
        OriginalElement, PlanarOrigin, UndefinedOrigin -> doNothing()
    }
}

private fun <ID : Id<ID>> checkOrigin(
    state: State,
    id: ID,
    creator: Reference,
    parent: ID,
    date: Date?,
) {
    checkCreator(state, id, creator, date)
    checkParent(state, id, parent, date)
}

private fun <ID : Id<ID>> checkCreator(
    state: State,
    id: ID,
    creator: Reference,
    date: Date?,
) {
    validateCreator(state, creator, id, date, "Creator")
}

private fun checkCharacterParent(
    state: State,
    id: CharacterId,
    parentIdValue: Int?,
    gender: Gender,
    date: Date?,
    text: String,
) {
    if (parentIdValue != null) {
        val parentId = CharacterId(parentIdValue)
        checkParent(state, id, parentId, date)
        val parent = state.getCharacterStorage().getOrThrow(parentId)
        require(parent.gender == gender) { "$text $parentIdValue is not $gender!" }
    }
}

private fun <ID : Id<ID>> checkParent(
    state: State,
    id: ID,
    parent: ID,
    date: Date?,
) {
    state.requireExists(parent, date) { "parent ${parent.print()}" }
    require(id != parent) { "${id.print()} cannot be its own parent!" }
}
