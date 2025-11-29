package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.character.*
import at.orchaldir.gm.app.html.util.showCreated
import at.orchaldir.gm.app.html.util.showEmploymentStatus
import at.orchaldir.gm.app.html.util.showPosition
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.generator.DateGenerator
import at.orchaldir.gm.core.generator.NameGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.SexualOrientation
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.selector.character.getAppearanceForAge
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.visualization.character.appearance.calculatePaddedSize
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.td
import kotlin.random.Random

fun Application.configureCharacterRouting() {
    routing {
        get<CharacterRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CharacterRoutes(),
                state.sortCharacters(all.sort),
                listOf(
                    tdColumn("Name") { showNameWithVitalStatus(call, state, it) },
                    Column("Title") { tdLink(call, state, it.title) },
                    Column("Race") { tdLink(call, state, it.race) },
                    Column("Gender") { tdEnum(it.gender) },
                    Column("Sexuality") {
                        if (it.sexuality != SexualOrientation.Heterosexual) {
                            tdEnum(it.sexuality)
                        } else {
                            td { }
                        }
                    },
                    Column("Culture") { tdLink(call, state, it.culture) },
                    createBeliefColumn(call, state),
                    createAgeColumn(state),
                    createStartDateColumn(call, state, "Birthdate"),
                    createEndDateColumn(call, state, "Deathdate"),
                    createVitalColumn(call, state, "Death"),
                    tdColumn("Housing Status") { showPosition(call, state, it.housingStatus.current, false) },
                    tdColumn("Employment Status") {
                        showEmploymentStatus(
                            call,
                            state,
                            it.employmentStatus.current,
                            false,
                            false
                        )
                    },
                    countCollectionColumn("Organizations") { state.getOrganizations(it.id) },
                    countColumn("Cost") { it.statblock.calculateCost(state) },
                ),
            ) {
                showCauseOfDeath(it)
                showGenderCount(it)
                showSexualOrientationCount(it)
                showHousingStatusCount(it)
            }
        }
        get<CharacterRoutes.Gallery> { gallery ->
            val state = STORE.getState()
            val routes = CharacterRoutes()
            val characters = state
                .getCharacterStorage()
                .getAll()
                .filter { it.appearance !is UndefinedAppearance }
            val sortedCharacters = state.sortCharacters(characters, gallery.sort)
            val maxSize = sortedCharacters
                .map { calculatePaddedSize(CHARACTER_CONFIG, it.appearance) }
                .maxBy { it.baseSize.height.value() }
                .getFullSize()

            handleShowGallery(
                state,
                routes,
                sortedCharacters,
                gallery.sort,
            ) { character ->
                val equipment = state.getEquipment(character)
                val appearance = state.getAppearanceForAge(character)
                val paddedSize = calculatePaddedSize(CHARACTER_CONFIG, appearance)

                visualizeAppearance(state, CHARACTER_CONFIG, maxSize, appearance, paddedSize, equipment)
            }
        }
        get<CharacterRoutes.Details> { details ->
            handleShowElementSplit(
                details.id,
                CharacterRoutes(),
                HtmlBlockTag::showCharacterDetails
            )
            { call, state, character ->
                val editAppearanceLink = call.application.href(CharacterRoutes.Appearance.Edit(character.id))

                showCharacterFrontAndBack(call, state, character)

                action(editAppearanceLink, "Edit Appearance")
            }
        }
        get<CharacterRoutes.New> {
            handleCreateElement(CharacterRoutes(), STORE.getState().getCharacterStorage())
        }
        get<CharacterRoutes.Delete> { delete ->
            handleDeleteElement(CharacterRoutes(), delete.id)
        }
        get<CharacterRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                CharacterRoutes(),
                HtmlBlockTag::editCharacter,
                HtmlBlockTag::showCharacterFrontAndBack,
            )
        }
        post<CharacterRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                CharacterRoutes(),
                ::parseCharacter,
                HtmlBlockTag::editCharacter,
                HtmlBlockTag::showCharacterFrontAndBack,
            )
        }
        post<CharacterRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCharacter)
        }
        get<CharacterRoutes.Birthday.Generate> { generate ->
            handleUpdateElement(generate.id, ::generateBirthday, "Generate the birthday of")
        }
        get<CharacterRoutes.Name.Generate> { generate ->
            handleUpdateElement(generate.id, ::generateName, "Generate the name of")
        }
    }
}

fun generateBirthday(
    state: State,
    id: CharacterId,
): Character {
    val generator = DateGenerator(RandomNumberGenerator(Random), state, state.getDefaultCalendarId())
    val character = state.getCharacterStorage().getOrThrow(id)
    val birthDate = generator.generateMonthAndDay(character.birthDate)

    return character.copy(birthDate = birthDate)
}

fun generateName(
    state: State,
    id: CharacterId,
): Character {
    val generator = NameGenerator(RandomNumberGenerator(Random), state, id)
    val name = generator.generate()

    return state.getCharacterStorage().getOrThrow(id).copy(name = name)
}

private fun HtmlBlockTag.showCharacterDetails(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    showData(character, call, state)
    showSocial(call, state, character)
    showPossession(call, state, character)
    showCreated(call, state, character.id)
}

private fun HtmlBlockTag.showCharacterFrontAndBack(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val equipment = state.getEquipment(character)
    val frontSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipment)
    val backSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipment, false)

    svg(frontSvg, 40)
    svg(backSvg, 40)
}
