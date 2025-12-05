package at.orchaldir.gm.app.html.rpg.trait

import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.GROUP
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitGroup
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitType
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.rpg.getCharacterTraitGroups
import at.orchaldir.gm.core.selector.rpg.getCharacterTraits
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
    field("Type", trait.type)
    field("Cost", trait.cost)

    if (trait.group != null) {
        val traits = state.getCharacterTraits(trait.group)
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
    val characterTemplates = state.getCharacterTemplates(trait)
    val gods = state.getGodsWith(trait)

    if (characters.isEmpty() && characterTemplates.isEmpty() && gods.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, characters)
    fieldElements(call, state, characterTemplates)
    fieldElements(call, state, gods)
}

// edit

fun HtmlBlockTag.editCharacterTrait(
    call: ApplicationCall,
    state: State,
    trait: CharacterTrait,
) {
    val groups = state.getCharacterTraitGroups()
    val newGroup = groups.maxOfOrNull { it.value + 1 } ?: 0

    selectName(trait.name)
    selectValue("Type", TYPE, CharacterTraitType.entries, trait.type)
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
                    label = state.getCharacterTraits(g)
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
        parse(parameters, TYPE, CharacterTraitType.Personality),
        group,
        parseInt(parameters, COST)
    )
}