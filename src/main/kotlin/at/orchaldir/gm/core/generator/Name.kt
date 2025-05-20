package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.utils.NumberGenerator

class NameGenerator(
    private val numberGenerator: NumberGenerator,
    private val state: State,
    private val character: Character,
) {
    private val namingConvention: NamingConvention =
        state.getCultureStorage().getOrThrow(character.culture).namingConvention

    constructor(numberGenerator: NumberGenerator, state: State, id: CharacterId) :
            this(numberGenerator, state, state.getCharacterStorage().getOrThrow(id))

    fun generate() = when (namingConvention) {
        NoNamingConvention -> character.name
        is MononymConvention -> Mononym(generateName(namingConvention.names))
        is FamilyConvention -> generateFamilyName(namingConvention)
        is GenonymConvention -> generateGenonym(namingConvention.names)
        is MatronymConvention -> generateGenonym(namingConvention.names)
        is PatronymConvention -> generateGenonym(namingConvention.names)
    }

    private fun generateFamilyName(convention: FamilyConvention) = FamilyName(
        generateName(convention.givenNames),
        generateMiddleName(convention),
        if (character.name is FamilyName && character.origin is Born) {
            character.name.family
        } else {
            generateName(convention.familyNames)
        }
    )

    private fun generateGenonym(names: GenderMap<NameListId>) = Genonym(generateName(names))

    private fun generateMiddleName(convention: FamilyConvention): Name? {
        val middleNameOption = state.rarityGenerator.generate(convention.middleNameOptions, numberGenerator)

        return when (middleNameOption) {
            MiddleNameOption.None -> null
            MiddleNameOption.Random -> generateName(convention.givenNames)
        }
    }

    private fun generateName(genderMap: GenderMap<NameListId>) =
        generateName(genderMap.get(character.gender))

    private fun generateName(nameListId: NameListId): Name {
        val nameList = state.getNameListStorage().getOrThrow(nameListId)
        return numberGenerator.select(nameList.names)
    }

}


