package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectText
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.app.parse.world.parseMoonId
import at.orchaldir.gm.app.parse.world.parseMountainId
import at.orchaldir.gm.app.parse.world.parseRiverId
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.*
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.fieldComplexName(
    state: State,
    name: ComplexName,
) {
    field("Name") {
        showComplexName(state, name)
    }
}

fun HtmlBlockTag.showComplexName(
    state: State,
    name: ComplexName,
) {
    +name.resolve(state)
}

fun FORM.selectComplexName(
    state: State,
    name: ComplexName,
) {
    selectValue("Name Type", combine(NAME, TYPE), ComplexNameType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == name.getType()
    }

    when (name) {
        is NameWithReference -> {
            selectValue("Name Type", combine(NAME, REFERENCE, TYPE), ReferenceForNameType.entries, true) { type ->
                label = type.name
                value = type.name
                selected = type == name.reference.getType()
            }

            // replace with id.type()?
            val elements = when (name.reference) {
                is ReferencedFamilyName -> state.getCharacterStorage().getAll()
                is ReferencedFullName -> state.getCharacterStorage().getAll()
                is ReferencedMoon -> state.getMoonStorage().getAll()
                is ReferencedMountain -> state.getMountainStorage().getAll()
                is ReferencedRiver -> state.getRiverStorage().getAll()
                is ReferencedTown -> state.getTownStorage().getAll()
            }
            val id = name.reference.getId()

            selectValue("Referenced Element", combine(NAME, REFERENCE), elements) { element ->
                label = element.name(state)
                value = element.id().value().toString()
                selected = element.id() == id
            }

            selectText("Prefix", name.prefix ?: "", combine(NAME, PREFIX), 1)
            selectText("Postfix", name.postfix ?: "", combine(NAME, POSTFIX), 1)
        }

        is SimpleName -> selectText("Name", name.name, NAME, 1)
    }
}

fun parseComplexName(parameters: Parameters): ComplexName {
    return when (parse(parameters, combine(NAME, TYPE), ComplexNameType.Simple)) {
        ComplexNameType.Simple -> SimpleName(parseString(parameters, NAME))
        ComplexNameType.Reference -> {
            val prefix = parseOptionalString(parameters, combine(NAME, PREFIX))
            val postfix = parseOptionalString(parameters, combine(NAME, POSTFIX))
            val param = combine(NAME, REFERENCE)

            val id = when (parse(parameters, combine(NAME, REFERENCE, TYPE), ReferenceForNameType.FamilyName)) {
                ReferenceForNameType.FamilyName, ReferenceForNameType.FullName -> ReferencedFamilyName(
                    parseCharacterId(
                        parameters,
                        param
                    )
                )

                ReferenceForNameType.Moon -> ReferencedMoon(parseMoonId(parameters, param))
                ReferenceForNameType.Mountain -> ReferencedMountain(parseMountainId(parameters, param))
                ReferenceForNameType.River -> ReferencedRiver(parseRiverId(parameters, param))
                ReferenceForNameType.Town -> ReferencedTown(parseTownId(parameters, param))
            }

            return NameWithReference(id, prefix, postfix)
        }
    }
}
