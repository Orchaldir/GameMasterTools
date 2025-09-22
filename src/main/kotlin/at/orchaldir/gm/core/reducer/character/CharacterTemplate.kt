package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateCharacterTemplate
import at.orchaldir.gm.core.action.UpdateCharacterTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.reducer.util.checkBeliefStatus
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER_TEMPLATE: Reducer<CreateCharacterTemplate, State> = { state, _ ->
    val template = CharacterTemplate(state.getCharacterTemplateStorage().nextId, race = RaceId(0))
    val templates = state.getCharacterTemplateStorage().add(template)
    noFollowUps(state.updateStorage(templates))
}

val UPDATE_CHARACTER_TEMPLATE: Reducer<UpdateCharacterTemplate, State> = { state, action ->
    val template = action.template
    state.getCharacterTemplateStorage().require(template.id)

    validateCharacterTemplate(state, template)

    noFollowUps(state.updateStorage(state.getCharacterTemplateStorage().update(template)))
}

fun validateCharacterTemplate(
    state: State,
    template: CharacterTemplate,
) {
    state.getCultureStorage().requireOptional(template.culture)
    state.getDataSourceStorage().require(template.sources)
    state.getLanguageStorage().require(template.languages.keys)
    state.getRaceStorage().require(template.race)
    state.getUniformStorage().requireOptional(template.uniform)
    validateStatblock(state, template.statblock)
    checkBeliefStatus(state, template.belief)
}
