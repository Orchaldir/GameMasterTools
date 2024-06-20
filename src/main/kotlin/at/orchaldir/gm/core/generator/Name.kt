package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.utils.NumberGenerator


fun generateName(numberGenerator: NumberGenerator, state: State, id: CharacterId): Character {
    val character = state.characters.getOrThrow(id)
    val culture = state.cultures.getOrThrow(character.culture)

    val name = when (culture.namingConvention) {
        is FamilyConvention -> generateFamilyName(character, state, numberGenerator, culture.namingConvention)
        is GenonymConvention -> TODO()
        is MatronymConvention -> TODO()
        is MononymConvention -> Mononym(generateName(state, numberGenerator, character, culture.namingConvention.names))
        NoNamingConvention -> character.name
        is PatronymConvention -> TODO()
    }

    return character.copy(name = name)
}

private fun generateFamilyName(
    character: Character,
    state: State,
    numberGenerator: NumberGenerator,
    namingConvention: FamilyConvention,
): CharacterName = when (character.name) {
    is FamilyName -> FamilyName(
        generateName(state, numberGenerator, character, namingConvention.givenNames),
        null,
        character.name.family,
    )

    else -> FamilyName(
        generateName(state, numberGenerator, character, namingConvention.givenNames),
        null,
        generateName(state, numberGenerator, namingConvention.familyNames),
    )
}

private fun generateName(
    state: State,
    numberGenerator: NumberGenerator,
    character: Character,
    genderMap: GenderMap<NameListId>,
) = generateName(state, numberGenerator, genderMap.get(character.gender))

private fun generateName(
    state: State,
    numberGenerator: NumberGenerator,
    nameListId: NameListId,
): String {
    val nameList = state.nameLists.getOrThrow(nameListId)
    return numberGenerator.select(nameList.names)
}
