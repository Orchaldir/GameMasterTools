package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.CLUB
import at.orchaldir.gm.app.FIXATION
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.rpg.combat.parseMeleeWeaponStats
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.OneHandedClub
import at.orchaldir.gm.core.model.item.equipment.TwoHandedClub
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showOneHandedClub(
    call: ApplicationCall,
    state: State,
    club: OneHandedClub,
) {
    showClubHead(call, state, club.head)
    field("Size", club.size)
    showHeadFixation(call, state, club.fixation)
    showShaft(call, state, club.shaft)
}

fun HtmlBlockTag.showTwoHandedClub(
    call: ApplicationCall,
    state: State,
    club: TwoHandedClub,
) {
    showClubHead(call, state, club.head)
    field("Size", club.size)
    showHeadFixation(call, state, club.fixation)
    showShaft(call, state, club.shaft)
}

// edit

fun HtmlBlockTag.editOneHandedClub(
    state: State,
    club: OneHandedClub,
) {
    editClubHead(state, club.head, CLUB)
    selectValue("Size", SIZE, Size.entries, club.size)
    editHeadFixation(state, club.fixation, FIXATION)
    editShaft(state, club.shaft)
}

fun HtmlBlockTag.editTwoHandedClub(
    state: State,
    club: TwoHandedClub,
) {
    editClubHead(state, club.head, CLUB)
    selectValue("Size", SIZE, Size.entries, club.size)
    editHeadFixation(state, club.fixation, FIXATION)
    editShaft(state, club.shaft)
}

// parse

fun parseOneHandedClub(
    state: State,
    parameters: Parameters,
) = OneHandedClub(
    parseClubHead(parameters, CLUB),
    parse(parameters, SIZE, Size.Medium),
    parseHeadFixation(parameters, FIXATION),
    parseShaft(parameters),
    parseMeleeWeaponStats(parameters),
)

fun parseTwoHandedClub(
    state: State,
    parameters: Parameters,
) = TwoHandedClub(
    parseClubHead(parameters, CLUB),
    parse(parameters, SIZE, Size.Medium),
    parseHeadFixation(parameters, FIXATION),
    parseShaft(parameters),
    parseMeleeWeaponStats(parameters),
)