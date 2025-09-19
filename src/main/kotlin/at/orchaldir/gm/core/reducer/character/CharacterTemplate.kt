package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateCharacterTemplate
import at.orchaldir.gm.core.action.UpdateCharacterTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_CHARACTERS
import at.orchaldir.gm.core.model.util.VALID_VITAL_STATUS_FOR_CHARACTERS
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER_TEMPLATE: Reducer<CreateCharacterTemplate, State> = { state, _ ->
    val template = CharacterTemplate(state.getCharacterTemplateStorage().nextId)
    val templates = state.getCharacterTemplateStorage().add(template)
    noFollowUps(state.updateStorage(templates))
}

val UPDATE_CHARACTER_TEMPLATE: Reducer<UpdateCharacterTemplate, State> = { state, action ->
    val template = action.template

    validateCharacterTemplate(state, template)

    noFollowUps(state.updateStorage(state.getCharacterTemplateStorage().update(template)))
}

fun validateCharacterTemplate(
    state: State,
    template: CharacterTemplate,
) {
    state.getDataSourceStorage().require(template.sources)
}
