package at.orchaldir.gm.app.html.economy.properties

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.editMaterialCategory
import at.orchaldir.gm.app.html.economy.material.parseMaterialCategory
import at.orchaldir.gm.app.html.economy.material.showMaterialCategory
import at.orchaldir.gm.app.html.util.math.fieldWeight
import at.orchaldir.gm.app.html.util.math.parseWeight
import at.orchaldir.gm.app.html.util.math.selectWeight
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import java.util.*

// show

fun HtmlBlockTag.showMaterialProperties(
    call: ApplicationCall,
    state: State,
    properties: MaterialProperties,
) {
    showMaterialCategory(call, state, properties.category)
    field("Crystal System", properties.crystalSystem)
    fieldWeight("Density", properties.density)
    field("Hardness", displayHardness(properties))
    field("Fracture", properties.fracture)
    field("Luster", properties.luster)
    field("Tenacity", properties.tenacity)
}

fun displayHardness(properties: MaterialProperties) =
    String.format(Locale.US, "%.1f", properties.hardness)

fun HtmlBlockTag.displayFracture(fracture: Fracture) = when (fracture) {
    Fracture.Undefined -> doNothing()
    else -> +fracture.name
}

fun HtmlBlockTag.displayTenacity(tenacity: Tenacity) = when (tenacity) {
    Tenacity.Undefined -> doNothing()
    else -> +tenacity.name
}

// edit

fun HtmlBlockTag.editMaterialProperties(
    call: ApplicationCall,
    state: State,
    properties: MaterialProperties,
) {
    editMaterialCategory(call, state, properties.category)
    selectOptionalValue(
        "Crystal System",
        CRYSTAl,
        properties.crystalSystem,
        CrystalSystem.entries,
    )
    selectWeight(
        "Density",
        DENSITY,
        properties.density,
        1,
        25000,
        SiPrefix.Kilo,
    )
    selectFloat(
        "Hardness",
        properties.hardness,
        MIN_HARDNESS,
        MAX_HARDNESS,
        0.1f,
        HARDNESS,
    )
    selectValue("Fracture", FRACTURE, Fracture.entries, properties.fracture)
    selectValue("Luster", LUSTER, Luster.entries, properties.luster)
    selectValue("Tenacity", TENACITY, Tenacity.entries, properties.tenacity)
}

// parse

fun parseMaterialProperties(
    state: State,
    parameters: Parameters,
) = MaterialProperties(
    parseMaterialCategory(state, parameters),
    parse(parameters, CRYSTAl, CrystalSystem.Amorphous),
    parseWeight(parameters, DENSITY, SiPrefix.Kilo),
    parse(parameters, FRACTURE, Fracture.Uneven),
    parseFloat(parameters, HARDNESS),
    parse(parameters, LUSTER, Luster.Dull),
    parse(parameters, TENACITY, Tenacity.Brittle),
)
