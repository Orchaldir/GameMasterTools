package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.*
import at.orchaldir.gm.app.plugins.character.Characters
import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.Item
import at.orchaldir.gm.core.model.item.ItemId
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.selector.getName
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a

// character

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: CharacterId,
) {
    link(call, id, state.getName(id))
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    link(call, character.id, state.getName(character))
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: CharacterId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: CharacterId,
) = call.application.href(Characters.Details(id))

// culture

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: CultureId,
) {
    link(call, id, state.cultures.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    culture: Culture,
) {
    link(call, culture.id, culture.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: CultureId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: CultureId,
) = call.application.href(Cultures.Details(id))

// fashion

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: FashionId,
) {
    link(call, id, state.fashion.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    fashion: Fashion,
) {
    link(call, fashion.id, fashion.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: FashionId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: FashionId,
) = call.application.href(Fashions.Details(id))

// item template

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: ItemTemplateId,
) {
    link(call, id, state.itemTemplates.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    itemTemplate: ItemTemplate,
) {
    link(call, itemTemplate.id, itemTemplate.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: ItemTemplateId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: ItemTemplateId,
) = call.application.href(ItemTemplates.Details(id))

// item

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: ItemId,
) = link(call, state, state.items.getOrThrow(id))

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    item: Item,
) {
    link(call, item.id, state.itemTemplates.get(item.template)?.name ?: "Unknown")
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: ItemId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: ItemId,
) = call.application.href(Items.Details(id))

// language

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: LanguageId,
) {
    link(call, id, state.languages.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    language: Language,
) {
    link(call, language.id, language.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: LanguageId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: LanguageId,
) = call.application.href(Languages.Details(id))

// material

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: MaterialId,
) {
    link(call, id, state.materials.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    material: Material,
) {
    link(call, material.id, material.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: MaterialId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: MaterialId,
) = call.application.href(Materials.Details(id))

// name list

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: NameListId,
) {
    link(call, id, state.nameLists.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    nameList: NameList,
) {
    link(call, nameList.id, nameList.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: NameListId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: NameListId,
) = call.application.href(NameLists.Details(id))

// personality

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: PersonalityTraitId,
) {
    link(call, id, state.personalityTraits.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    trait: PersonalityTrait,
) {
    link(call, trait.id, trait.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: PersonalityTraitId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: PersonalityTraitId,
) = call.application.href(Personality.Details(id))

// race

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: RaceId,
) {
    link(call, id, state.races.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    race: Race,
) {
    link(call, race.id, race.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: RaceId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: RaceId,
) = call.application.href(Races.Details(id))
