package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.util.origin.BornElement
import at.orchaldir.gm.utils.NumberGenerator

class NameGenerator(
    private val numberGenerator: NumberGenerator,
    private val state: State,
    private val character: Character,
) {
    private val namingConvention: NamingConvention =
        state.getCultureStorage().getOptional(character.culture)?.namingConvention ?: NoNamingConvention

    constructor(numberGenerator: NumberGenerator, state: State, id: CharacterId) :
            this(numberGenerator, state, state.getCharacterStorage().getOrThrow(id))

    fun generate() = when (namingConvention) {
        NoNamingConvention -> character.name
        is MononymConvention -> Mononym(generateName(namingConvention.names))
        is FamilyConvention -> generateFamilyName(namingConvention)
        is GenonymConvention -> generateGenonym(namingConvention.names)
        is MatronymConvention -> generateGenonym(namingConvention.names)
        is PatronymConvention -> generateGenonym(namingConvention.names)
        is RandomGivenAndLastName -> generateRandomName(namingConvention)
        is OccupationalNamingConvention -> OccupationalName(generateName(namingConvention.names))
    }

    private fun generateFamilyName(convention: FamilyConvention) = FamilyName(
        generateName(convention.givenNames),
        generateMiddleName(convention.middleNameOptions, convention.givenNames),
        if (character.name is FamilyName && character.origin is BornElement) {
            character.name.family
        } else {
            generateName(convention.familyNames)
        }
    )

    private fun generateRandomName(convention: RandomGivenAndLastName) = FamilyName(
        generateName(convention.givenNames),
        generateMiddleName(convention.middleNameOptions, convention.givenNames),
        generateName(convention.lastNames),
    )

    private fun generateGenonym(names: GivenNames) = Genonym(generateName(names))

    private fun generateMiddleName(
        options: OneOf<MiddleNameOption>,
        givenNames: GivenNames,
    ): Name? {
        val middleNameOption = state.rarityGenerator.generate(options, numberGenerator)

        return when (middleNameOption) {
            MiddleNameOption.None -> null
            MiddleNameOption.Random -> generateName(givenNames)
        }
    }

    private fun generateName(names: GivenNames) = when (names) {
        is NonGenderedGivenNames -> generateName(names.list)
        is MaleAndFemaleGivenNames -> when (character.gender) {
            Gender.Female -> generateName(names.female, names.unisex)
            Gender.Genderless -> TODO()
            Gender.Male -> generateName(names.female, names.unisex)
        }
    }

    private fun generateName(list: NameListId, optional: NameListId?): Name {
        val listNames = state.getNameListStorage()
            .getOrThrow(list)
            .names
        val optionalNames = state.getNameListStorage()
            .getOptional(optional)
            ?.names ?: emptyList()

        return numberGenerator.select(listNames + optionalNames)
    }

    private fun generateName(nameListId: NameListId): Name {
        val nameList = state.getNameListStorage().getOrThrow(nameListId)
        return numberGenerator.select(nameList.names)
    }

}


