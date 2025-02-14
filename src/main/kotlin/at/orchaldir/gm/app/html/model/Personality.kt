package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.NONE
import at.orchaldir.gm.app.PERSONALITY_PREFIX
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.selector.getPersonalityTraitGroups
import at.orchaldir.gm.core.selector.getPersonalityTraits
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showPersonality(
    call: ApplicationCall,
    state: State,
    personality: Set<PersonalityTraitId>,
) {
    showList("Personality", personality) { t ->
        link(call, state, t)
    }
}

// edit

fun HtmlBlockTag.editPersonality(
    call: ApplicationCall,
    state: State,
    personality: Set<PersonalityTraitId>,
) {
    field("Personality") {
        details {
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
}

// parse

fun parsePersonality(parameters: Parameters) = parameters.entries()
    .asSequence()
    .filter { e -> e.key.startsWith(PERSONALITY_PREFIX) }
    .map { e -> e.value.first() }
    .filter { it != NONE }
    .map { PersonalityTraitId(it.toInt()) }
    .toSet()