package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.POPULATION
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.RealmConfig
import at.orchaldir.gm.core.model.realm.SettlementSizeId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showRealmConfig(
    call: ApplicationCall,
    state: State,
    config: RealmConfig,
) {
    h2 { +"Realm" }

    table {
        tr {
            th { +"Settlement Size" }
            th { +"Max Population" }
        }
        config.settlementSizes.forEach { size ->
            tr {
                tdLink(call, state, size)
                tdSkipZero(size.maxPopulation)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editRealmConfig(
    state: State,
    config: RealmConfig,
) {
    h2 { +"Realm" }

    var minMaxPopulation = 0

    editList(
        "Settlement Sizes",
        POPULATION,
        config.settlementSizes,
        1,
        10,
        1,
    ) { index, param, standard ->
        editSettlementSize(state, standard, param, minMaxPopulation)
        minMaxPopulation = standard.maxPopulation
    }
}

// parse

fun parseRealmConfig(
    parameters: Parameters,
) = RealmConfig(
    parseList(parameters, POPULATION, 1) { index, param ->
        parseSettlementSize(SettlementSizeId(index), parameters, param)
    },
)
