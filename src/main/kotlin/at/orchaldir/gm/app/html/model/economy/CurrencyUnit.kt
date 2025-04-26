package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.CURRENCY
import at.orchaldir.gm.app.FORMAT
import at.orchaldir.gm.app.HOLE
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.fieldDistance
import at.orchaldir.gm.app.html.model.fieldFactor
import at.orchaldir.gm.app.html.model.item.equipment.selectMaterial
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.app.html.model.parseFactor
import at.orchaldir.gm.app.html.model.parseMaterialId
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.html.model.selectFactor
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.BiMetallicCoin
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.CurrencyFormat
import at.orchaldir.gm.core.model.economy.money.CurrencyFormatType
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.economy.money.DEFAULT_RADIUS
import at.orchaldir.gm.core.model.economy.money.DEFAULT_RADIUS_FACTOR
import at.orchaldir.gm.core.model.economy.money.HoledCoin
import at.orchaldir.gm.core.model.economy.money.MAX_RADIUS
import at.orchaldir.gm.core.model.economy.money.MAX_RADIUS_FACTOR
import at.orchaldir.gm.core.model.economy.money.MIN_RADIUS
import at.orchaldir.gm.core.model.economy.money.MIN_RADIUS_FACTOR
import at.orchaldir.gm.core.model.economy.money.Shape
import at.orchaldir.gm.core.model.economy.money.UndefinedCurrencyFormat
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.unit.Distance
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCurrencyUnit(
    call: ApplicationCall,
    state: State,
    unit: CurrencyUnit,
) {
    fieldLink("Currency", call, state, unit.currency)
    field("Value", unit.value)
    showCurrencyFormat(call, state, unit.format)
}

fun HtmlBlockTag.showCurrencyFormat(
    call: ApplicationCall,
    state: State,
    format: CurrencyFormat,
) {
    field("Format", format.getType())

    when (format) {
        UndefinedCurrencyFormat -> doNothing()
        is Coin -> {
            fieldLink("Material", call, state, format.material)
            field("Shape", format.shape)
            fieldDistance("Radius", format.radius)
        }

        is HoledCoin -> {
            fieldLink("Material", call, state, format.material)
            field("Shape", format.shape)
            fieldDistance("Radius", format.radius)
            showDetails("Hole") {
                field("Shape", format.holeShape)
                fieldFactor("Factor", format.holeFactor)
            }
        }

        is BiMetallicCoin -> {
            showDetails("Outer") {
                fieldLink("Material", call, state, format.material)
                field("Shape", format.shape)
                fieldDistance("Radius", format.radius)
            }
            showDetails("Inner") {
                fieldLink("Material", call, state, format.innerMaterial)
                field("Shape", format.innerShape)
                fieldFactor("Factor", format.innerFactor)
            }
        }
    }
}

// edit

fun FORM.editCurrencyUnit(
    state: State,
    unit: CurrencyUnit,
) {
    selectName(unit.name)
    selectElement(
        state,
        "Currency",
        CURRENCY,
        state.getCurrencyStorage().getAll(),
        unit.currency,
    )
    selectInt("Value", unit.value, 1, 10000, 1, NUMBER)
    editCurrencyFormat(state, unit.format)
}

fun HtmlBlockTag.editCurrencyFormat(
    state: State,
    format: CurrencyFormat,
) {
    selectValue(
        "Format",
        FORMAT,
        CurrencyFormatType.entries,
        format.getType(),
        true
    )

    when (format) {
        UndefinedCurrencyFormat -> doNothing()
        is Coin -> {
            selectMaterial(state, format.material, MATERIAL)
            selectShape(format.shape, SHAPE)
            selectRadius(format.radius)
        }

        is HoledCoin -> {
            selectMaterial(state, format.material, MATERIAL)
            selectShape(format.shape, SHAPE)
            selectRadius(format.radius)
            showDetails("Hole") {
                selectShape(format.holeShape, combine(HOLE, SHAPE))
                selectRadiusFactor(format.holeFactor)
            }
        }

        is BiMetallicCoin -> {
            showDetails("Outer") {
                selectMaterial(state, format.material, MATERIAL)
                selectShape(format.shape, SHAPE)
                selectRadius(format.radius)
            }
            showDetails("Inner") {
                selectMaterial(state, format.innerMaterial, combine(HOLE, MATERIAL))
                selectShape(format.innerShape, combine(HOLE, SHAPE))
                selectRadiusFactor(format.innerFactor)
            }
        }
    }
}

private fun HtmlBlockTag.selectShape(shape: Shape, param: String) {
    selectValue("Shape", param, Shape.entries, shape, true)
}

private fun HtmlBlockTag.selectRadius(radius: Distance) {
    selectDistance(
        "Radius",
        LENGTH,
        radius,
        MIN_RADIUS,
        MAX_RADIUS,
        MIN_RADIUS,
        true
    )
}

private fun DETAILS.selectRadiusFactor(factor: Factor) {
    selectFactor(
        "Radius Factor",
        HOLE,
        factor,
        MIN_RADIUS_FACTOR,
        MAX_RADIUS_FACTOR,
        fromPercentage(1),
    )
}

// parse

fun parseCurrencyUnitId(parameters: Parameters, param: String) =
    parseOptionalCurrencyUnitId(parameters, param) ?: CurrencyUnitId(0)

fun parseOptionalCurrencyUnitId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { CurrencyUnitId(it) }

fun parseCurrencyUnit(parameters: Parameters, id: CurrencyUnitId): CurrencyUnit = CurrencyUnit(
    id,
    parseName(parameters),
    parseCurrencyId(parameters, CURRENCY),
    parseInt(parameters, NUMBER, 1),
    parseCurrencyFormat(parameters),
)

fun parseCurrencyFormat(parameters: Parameters) =
    when (parse(parameters, FORMAT, CurrencyFormatType.Undefined)) {
        CurrencyFormatType.Undefined -> UndefinedCurrencyFormat
        CurrencyFormatType.Coin -> Coin(
            parseMaterialId(parameters, MATERIAL),
            parse(parameters, SHAPE, Shape.Circle),
            parseRadius(parameters),
        )

        CurrencyFormatType.HoledCoin -> HoledCoin(
            parseMaterialId(parameters, MATERIAL),
            parse(parameters, SHAPE, Shape.Circle),
            parseRadius(parameters),
            parse(parameters, combine(HOLE, SHAPE), Shape.Circle),
            parseRadiusFactor(parameters),
        )

        CurrencyFormatType.BiMetallicCoin -> BiMetallicCoin(
            parseMaterialId(parameters, MATERIAL),
            parse(parameters, SHAPE, Shape.Circle),
            parseRadius(parameters),
            parseMaterialId(parameters, combine(HOLE, MATERIAL)),
            parse(parameters, combine(HOLE, SHAPE), Shape.Circle),
            parseRadiusFactor(parameters),
        )
    }

private fun parseRadius(parameters: Parameters): Distance =
    parseDistance(parameters, LENGTH, DEFAULT_RADIUS)

private fun parseRadiusFactor(parameters: Parameters): Factor =
    parseFactor(parameters, HOLE, DEFAULT_RADIUS_FACTOR)
