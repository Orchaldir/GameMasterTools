package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.NONE
import at.orchaldir.gm.app.PERSONALITY_PREFIX
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

fun HtmlBlockTag.showPersonality(
    call: ApplicationCall,
    state: State,
    personality: Set<CharacterTraitId>,
) {
    fieldIds(call, state, "Personality", personality)
}

fun HtmlBlockTag.showPersonalityTrait(
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

fun HtmlBlockTag.editPersonality(
    call: ApplicationCall,
    state: State,
    personality: Set<CharacterTraitId>,
) {
    showDetails("Personality") {
        state.getPersonalityTraitGroups().forEach { group ->
            val textId = "$PERSONALITY_PREFIX${group.value}"
            var isAnyCheck = false

            p {
                state.getPersonalityTraits(group).forEach { trait ->
                    val isChecked = personality.contains(trait.id)
                    isAnyCheck = isAnyCheck || isChecked

                    radioInput {
                        id = textId
                        name = textId
                        value = trait.id.value.toString()
                        checked = isChecked
                    }
                    label {
                        htmlFor = textId
                        link(call, trait)
                    }
                }

                radioInput {
                    id = textId
                    name = textId
                    value = NONE
                    checked = !isAnyCheck
                }
                label {
                    htmlFor = textId
                    +NONE
                }
            }
        }
    }
}

fun HtmlBlockTag.editPersonalityTrait(
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

fun parsePersonality(parameters: Parameters) = parameters.entries()
    .asSequence()
    .filter { e -> e.key.startsWith(PERSONALITY_PREFIX) }
    .map { e -> e.value.first() }
    .filter { it != NONE }
    .map { CharacterTraitId(it.toInt()) }
    .toSet()

fun parsePersonalityTrait(
    state: State,
    parameters: Parameters,
    id: CharacterTraitId,
): CharacterTrait {
    val group = parameters["group"]
        ?.toIntOrNull()
        ?.let { CharacterTraitGroup(it) }

    return CharacterTrait(id, parseName(parameters), group)
}