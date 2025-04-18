package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CATEGORY
import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialCategory
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import io.ktor.http.*
import io.ktor.server.util.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMaterial(material: Material) {
    field("Name", material.name)
    field("Category", material.category)
    fieldColor(material.color)
}

// edit

fun HtmlBlockTag.editMaterial(material: Material) {
    selectName(material.name)
    selectValue("Category", CATEGORY, MaterialCategory.entries, material.category)
    selectColor(material.color)
}

// parse

fun parseMaterialId(value: String) = MaterialId(value.toInt())
fun parseMaterialId(parameters: Parameters, param: String) = MaterialId(parseInt(parameters, param))
fun parseOptionalMaterialId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { MaterialId(it) }

fun parseMaterial(id: MaterialId, parameters: Parameters): Material {
    val name = parameters.getOrFail(NAME)

    return Material(
        id,
        name,
        parse(parameters, CATEGORY, MaterialCategory.Metal),
        parse(parameters, COLOR, Color.Pink),
    )
}
