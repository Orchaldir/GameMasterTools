package at.orchaldir.gm.app.html.rpg.trait

import at.orchaldir.gm.app.NONE
import at.orchaldir.gm.app.CHARACTER_TRAIT_PREFIX
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.selector.character.getPersonalityTraitGroups
import at.orchaldir.gm.core.selector.character.getPersonalityTraits
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showCharacterTraits(
    call: ApplicationCall,
    state: State,
    personality: Set<CharacterTraitId>,
) {
    fieldIds(call, state, "Character Traits", personality)
}

// edit

fun HtmlBlockTag.editCharacterTraits(
    call: ApplicationCall,
    state: State,
    personality: Set<CharacterTraitId>,
) {
    showDetails("Character Traits") {
        state.getPersonalityTraitGroups().forEach { group ->
            val textId = "$CHARACTER_TRAIT_PREFIX${group.value}"
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

// parse

fun parseCharacterTraits(parameters: Parameters) = parameters.entries()
    .asSequence()
    .filter { e -> e.key.startsWith(CHARACTER_TRAIT_PREFIX) }
    .map { e -> e.value.first() }
    .filter { it != NONE }
    .map { CharacterTraitId(it.toInt()) }
    .toSet()