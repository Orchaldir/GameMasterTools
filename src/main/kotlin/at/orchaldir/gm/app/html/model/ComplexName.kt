package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.character.parseCharacterId
import at.orchaldir.gm.app.html.model.world.parseMoonId
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.app.parse.world.parseMountainId
import at.orchaldir.gm.app.parse.world.parseRiverId
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.*
import io.ktor.http.*
import io.ktor.server.application.*
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

fun HtmlBlockTag.fieldReferenceByName(
    call: ApplicationCall,
    state: State,
    name: ComplexName,
) {
    if (name is NameWithReference) {
        field("Referenced by Name") {
            when (name.reference) {
                is ReferencedGivenName -> link(call, state, name.reference.id)
                is ReferencedFamilyName -> link(call, state, name.reference.id)
                is ReferencedFullName -> link(call, state, name.reference.id)
                is ReferencedMoon -> link(call, state, name.reference.id)
                is ReferencedMountain -> link(call, state, name.reference.id)
                is ReferencedRiver -> link(call, state, name.reference.id)
                is ReferencedTown -> link(call, state, name.reference.id)
            }
        }
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
    selectValue("Name Type", combine(NAME, TYPE), ComplexNameType.entries, name.getType(), true)

    internalSelect(state, name)
}

fun FORM.selectOptionalComplexName(
    state: State,
    name: ComplexName?,
) {
    selectOptionalValue("Name Type", combine(NAME, TYPE), name?.getType(), ComplexNameType.entries, true) { type ->
        label = type.name
        value = type.name
    }

    if (name != null) {
        internalSelect(state, name)
    }
}

private fun FORM.internalSelect(
    state: State,
    name: ComplexName,
) {
    when (name) {
        is NameWithReference -> {
            selectValue(
                "Reference Type",
                combine(NAME, REFERENCE, TYPE),
                ReferenceForNameType.entries,
                name.reference.getType(),
                true
            )

            // replace with id.type()?
            val elements = when (name.reference) {
                is ReferencedGivenName -> state.getCharacterStorage().getAll()
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
    val type = parse(parameters, combine(NAME, TYPE), ComplexNameType.Simple)
    return internalParse(parameters, type)
}

fun parseOptionalComplexName(parameters: Parameters): ComplexName? {
    val type = parse<ComplexNameType>(parameters, combine(NAME, TYPE))

    return if (type != null) {
        internalParse(parameters, type)
    } else {
        null
    }
}

fun internalParse(parameters: Parameters, type: ComplexNameType) = when (type) {
    ComplexNameType.Simple -> SimpleName(parseString(parameters, NAME))
    ComplexNameType.Reference -> {
        val prefix = parseOptionalString(parameters, combine(NAME, PREFIX))
        val postfix = parseOptionalString(parameters, combine(NAME, POSTFIX))
        val param = combine(NAME, REFERENCE)

        val id = when (parse(parameters, combine(NAME, REFERENCE, TYPE), ReferenceForNameType.FamilyName)) {
            ReferenceForNameType.FamilyName -> ReferencedFamilyName(
                parseCharacterId(parameters, param)
            )

            ReferenceForNameType.GivenName -> ReferencedGivenName(
                parseCharacterId(parameters, param)
            )

            ReferenceForNameType.FullName -> ReferencedFullName(
                parseCharacterId(parameters, param)
            )

            ReferenceForNameType.Moon -> ReferencedMoon(parseMoonId(parameters, param))
            ReferenceForNameType.Mountain -> ReferencedMountain(parseMountainId(parameters, param))
            ReferenceForNameType.River -> ReferencedRiver(parseRiverId(parameters, param))
            ReferenceForNameType.Town -> ReferencedTown(parseTownId(parameters, param))
        }

        if (prefix == null && postfix == null) {
            NameWithReference(id, "?", "?")
        } else {
            NameWithReference(id, prefix, postfix)
        }
    }
}
