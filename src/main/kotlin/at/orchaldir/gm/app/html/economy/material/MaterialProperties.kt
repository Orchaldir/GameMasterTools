package at.orchaldir.gm.app.html.economy.properties

import at.orchaldir.gm.app.CATEGORY
import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.CRYSTAl
import at.orchaldir.gm.app.DENSITY
import at.orchaldir.gm.app.FRACTURE
import at.orchaldir.gm.app.HARDNESS
import at.orchaldir.gm.app.LUSTER
import at.orchaldir.gm.app.OPACITY
import at.orchaldir.gm.app.TENACITY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.fieldWeight
import at.orchaldir.gm.app.html.util.math.parseWeight
import at.orchaldir.gm.app.html.util.math.selectWeight
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.CrystalSystem
import at.orchaldir.gm.core.model.economy.material.Fracture
import at.orchaldir.gm.core.model.economy.material.Luster
import at.orchaldir.gm.core.model.economy.material.MAX_HARDNESS
import at.orchaldir.gm.core.model.economy.material.MIN_HARDNESS
import at.orchaldir.gm.core.model.economy.material.MaterialCategory
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Tenacity
import at.orchaldir.gm.core.model.economy.material.Transparency
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import java.util.Locale

// show

fun HtmlBlockTag.showMaterialProperties(
    properties: MaterialProperties,
) {
    field("Category", properties.category)
    optionalField("Crystal System", properties.crystalSystem)
    fieldColor(properties.color)
    field("Transparency", properties.transparency)
    fieldWeight("Density", properties.density)
    field("Hardness", String.format(Locale.US, "%.1f", properties.hardness))
    field("Fracture", properties.fracture)
    field("Luster", properties.luster)
    field("Tenacity", properties.tenacity)
}

// edit

fun HtmlBlockTag.editMaterialProperties(
    properties: MaterialProperties,
) {
    selectValue("Category", CATEGORY, MaterialCategory.entries, properties.category)
    selectOptionalValue(
        "Crystal System",
        CRYSTAl,
        properties.crystalSystem,
        CrystalSystem.entries,
    )
    selectColor(properties.color)
    selectValue("Transparency", OPACITY, Transparency.entries, properties.transparency)
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
    parameters: Parameters,
) = MaterialProperties(
    parse(parameters, CATEGORY, MaterialCategory.Metal),
    parse(parameters, COLOR, Color.Pink),
    parse(parameters, CRYSTAl, CrystalSystem.None),
    parseWeight(parameters, DENSITY, SiPrefix.Kilo),
    parse(parameters, FRACTURE, Fracture.Uneven),
    parseFloat(parameters, HARDNESS),
    parse(parameters, LUSTER, Luster.Dull),
    parse(parameters, TENACITY, Tenacity.Brittle),
    parse(parameters, OPACITY, Transparency.Opaque),
)
