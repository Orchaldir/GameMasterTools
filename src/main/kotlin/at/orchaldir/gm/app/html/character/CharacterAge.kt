package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.AGE
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LIFE_STAGE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.PURPOSE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.race.parseLifeStageId
import at.orchaldir.gm.app.html.util.field
import at.orchaldir.gm.app.html.util.fieldAge
import at.orchaldir.gm.app.html.util.parseDate
import at.orchaldir.gm.app.html.util.selectDate
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.AgeViaBirthdate
import at.orchaldir.gm.core.model.character.AgeViaDefaultLifeStage
import at.orchaldir.gm.core.model.character.AgeViaLifeStage
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterAge
import at.orchaldir.gm.core.model.character.CharacterAgeType
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.LifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStageId
import at.orchaldir.gm.core.model.race.aging.LifeStagesType
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCharacterAge(
    call: ApplicationCall,
    state: State,
    character: Character,
    race: Race,
    age: CharacterAge,
) {
    showDetails("Age", true) {
        field("Type", age.getType())

        val lifeStageId = when (age) {
            is AgeViaBirthdate -> {
                field(call, state, "Birthdate", age.date)
                showAgeViaBirthdate(state, character, race)

                return@showDetails
            }
            AgeViaDefaultLifeStage -> race.lifeStages.getDefaultLifeStageId()
            is AgeViaLifeStage -> age.lifeStage
        }

        showLifeStage(race, lifeStageId)
    }
}

private fun DETAILS.showAgeViaBirthdate(
    state: State,
    character: Character,
    race: Race,
) {
    val ageInYears = character.getAgeInYears(state)
    fieldAge("Age", ageInYears)

    val lifeStage = race.lifeStages.getLifeStageForAge(ageInYears) ?: return
    val start = race.lifeStages.getStartAgeOfCurrentLifeStage(ageInYears)
    showLifeStage(lifeStage, start)
}

private fun DETAILS.showLifeStage(
    race: Race,
    lifeStageId: LifeStageId?,
) {
    if (lifeStageId != null) {
        val lifeStage = race.lifeStages.getLifeStage(lifeStageId)
        val start = race.lifeStages.getLifeStageStartAge(lifeStageId)
        showLifeStage(lifeStage, start)
    }
}

private fun DETAILS.showLifeStage(
    lifeStage: LifeStage,
    start: Int,
) {
    field("Life Stage", "${lifeStage.name.text} ($start-${lifeStage.maxAge} years)")
}

// edit

fun HtmlBlockTag.selectCharacterAge(
    state: State,
    character: Character,
    race: Race,
    age: CharacterAge,
) {
    showDetails("Age", true) {
        selectValue("Type", AGE, CharacterAgeType.entries, age.getType()) { type ->
            when (type) {
                CharacterAgeType.Birthdate -> false
                CharacterAgeType.LifeStage -> race.lifeStages.getType() != LifeStagesType.ImmutableLifeStage
                CharacterAgeType.DefaultLifeStage -> false
            }
        }

        val lifeStageId = when (age) {
            is AgeViaBirthdate -> {
                selectDate(
                    state,
                    "Birthdate",
                    age.date,
                    combine(AGE, DATE),
                    race.startDate(),
                )
                showAgeViaBirthdate(state, character, race)

                return@showDetails
            }
            AgeViaDefaultLifeStage -> race.lifeStages.getDefaultLifeStageId()
            is AgeViaLifeStage -> {
                selectValue(
                    "Life Stage",
                    combine(AGE, LIFE_STAGE),
                    race.lifeStages.getAllLifeStages().withIndex().toList(),
                ) { indexed ->
                    label = indexed.value.name.text
                    value = indexed.index.toString()
                    selected = indexed.index == age.lifeStage.value
                }

                age.lifeStage
            }
        }

        showLifeStage(race, lifeStageId)
    }
}

// parse

fun parseCharacterAge(
    parameters: Parameters,
    state: State,
): CharacterAge = when (parse(parameters, AGE, CharacterAgeType.DefaultLifeStage)) {
    CharacterAgeType.Birthdate -> AgeViaBirthdate(
        parseDate(parameters, state.getDefaultCalendar(), combine(AGE, DATE)),
    )
    CharacterAgeType.DefaultLifeStage -> AgeViaDefaultLifeStage
    CharacterAgeType.LifeStage -> AgeViaLifeStage(
        parseLifeStageId(parameters, combine(AGE, LIFE_STAGE)),
    )
}
