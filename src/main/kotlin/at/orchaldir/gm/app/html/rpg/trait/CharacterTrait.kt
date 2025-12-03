package at.orchaldir.gm.app.html.rpg.trait

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitGroup
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.character.getPersonalityTraitGroups
import at.orchaldir.gm.core.selector.character.getPersonalityTraits
import at.orchaldir.gm.core.selector.religion.getGodsWith
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showCharacterTrait(
    call: ApplicationCall,
    state: State,
    trait: CharacterTrait,
) {
    val characters = state.getCharacters(trait.id)
    val gods = state.getGodsWith(trait.id)
    fieldName(trait.name)

    if (trait.group != null) {
        val traits = state.getPersonalityTraits(trait.group)
            .filter { it != trait }

        fieldElements(call, state, "Conflicting", traits)
    }

    fieldElements(call, state, characters)
    fieldElements(call, state, gods)
}

// edit

fun HtmlBlockTag.editCharacterTrait(
    call: ApplicationCall,
    state: State,
    trait: CharacterTrait,
) {
    val groups = state.getPersonalityTraitGroups()
    val newGroup = groups.maxOfOrNull { it.value + 1 } ?: 0

    selectName(trait.name)
    field("Group") {
        select {
            id = "group"
            name = "group"
            option {
                label = "No group"
                value = ""
                selected = trait.group == null
            }
            groups.forEach { g ->
                option {
                    label = state.getPersonalityTraits(g)
                        .sortedBy { it.name.text }
                        .joinToString(separator = " VS ") { it.name.text }
                    value = g.value.toString()
                    selected = g == trait.group
                }
            }
            option {
                label = "New group"
                value = newGroup.toString()
            }
        }
    }
}

// parse

fun parseCharacterTrait(
    state: State,
    parameters: Parameters,
    id: CharacterTraitId,
): CharacterTrait {
    val group = parameters["group"]
        ?.toIntOrNull()
        ?.let { CharacterTraitGroup(it) }

    return CharacterTrait(id, parseName(parameters), group)
}