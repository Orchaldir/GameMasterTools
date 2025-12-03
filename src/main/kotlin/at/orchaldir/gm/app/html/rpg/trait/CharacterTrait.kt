package at.orchaldir.gm.app.html.rpg.trait

import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.GROUP
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitGroup
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.character.getCharactersUsing
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

    fieldName(trait.name)
    field("Cost", trait.cost)

    if (trait.group != null) {
        val traits = state.getPersonalityTraits(trait.group)
            .filter { it != trait }

        fieldElements(call, state, "Conflicting", traits)
    }

    showUsage(call, state, trait.id)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    trait: CharacterTraitId,
) {
    val characters = state.getCharacters(trait)
    val gods = state.getGodsWith(trait)

    if (characters.isEmpty() && gods.isEmpty()) {
        return
    }

    h2 { +"Usage" }

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
    selectInt("Cost", trait.cost, -1000, 1000, 1, COST)
    field("Group") {
        select {
            id = GROUP
            name = GROUP
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
    val group = parameters[GROUP]
        ?.toIntOrNull()
        ?.let { CharacterTraitGroup(it) }

    return CharacterTrait(
        id,
        parseName(parameters),
        group,
        parseInt(parameters, COST)
    )
}