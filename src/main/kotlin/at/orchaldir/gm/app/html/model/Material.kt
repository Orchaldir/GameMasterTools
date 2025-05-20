package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CATEGORY
import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.DENSITY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialCategory
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMaterial(material: Material) {
    fieldName(material.name)
    field("Category", material.category)
    fieldColor(material.color)
    fieldWeight("Density", material.density)
}

// edit

fun HtmlBlockTag.editMaterial(material: Material) {
    selectName(material.name)
    selectValue("Category", CATEGORY, MaterialCategory.entries, material.category)
    selectColor(material.color)
    selectWeight(
        "Density",
        DENSITY,
        material.density,
        1,
        25000,
        SiPrefix.Kilo,
    )
}

// parse

fun parseMaterialId(value: String) = MaterialId(value.toInt())
fun parseMaterialId(parameters: Parameters, param: String) = MaterialId(parseInt(parameters, param))
fun parseOptionalMaterialId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { MaterialId(it) }

fun parseMaterial(id: MaterialId, parameters: Parameters): Material {
    val density = parseWeight(parameters, DENSITY, SiPrefix.Kilo)
    return Material(
        id,
        parseName(parameters),
        parse(parameters, CATEGORY, MaterialCategory.Metal),
        parse(parameters, COLOR, Color.Pink),
        density,
    )
}
