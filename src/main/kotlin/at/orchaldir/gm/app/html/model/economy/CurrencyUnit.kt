package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.item.equipment.selectMaterial
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseBool
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.selector.economy.money.display
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPermille
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
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
    showDenomination(state, unit)
    showCurrencyFormat(call, state, unit.format)
}

private fun HtmlBlockTag.showDenomination(
    state: State,
    unit: CurrencyUnit,
) {
    val currency = state.getCurrencyStorage().getOrThrow(unit.currency)
    field("Denomination", currency.display(unit.value))
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
            fieldFactor("Rim Factor", format.rimFactor)
        }

        is HoledCoin -> {
            fieldLink("Material", call, state, format.material)
            field("Shape", format.shape)
            fieldDistance("Radius", format.radius)
            fieldFactor("Rim Factor", format.rimFactor)
            showDetails("Hole") {
                field("Shape", format.holeShape)
                fieldFactor("Factor", format.holeFactor)
                field("Has rim?", format.hasHoleRim)
            }
        }

        is BiMetallicCoin -> {
            showDetails("Outer") {
                fieldLink("Material", call, state, format.material)
                field("Shape", format.shape)
                fieldDistance("Radius", format.radius)
                fieldFactor("Rim Factor", format.rimFactor)
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
    selectInt("Value", unit.value, 1, 10000, 1, NUMBER, update = true)
    showDenomination(state, unit)
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
            selectRimFactor(format.rimFactor)
        }

        is HoledCoin -> {
            selectMaterial(state, format.material, MATERIAL)
            selectShape(format.shape, SHAPE)
            selectRadius(format.radius)
            selectRimFactor(format.rimFactor)
            showDetails("Hole", true) {
                selectShape(format.holeShape, combine(HOLE, SHAPE))
                selectRadiusFactor(format.holeFactor)
                selectBool("Has rim?", format.hasHoleRim, combine(HOLE, EDGE), update = true)
            }
        }

        is BiMetallicCoin -> {
            showDetails("Outer", true) {
                selectMaterial(state, format.material, MATERIAL)
                selectShape(format.shape, SHAPE)
                selectRadius(format.radius)
                selectRimFactor(format.rimFactor)
            }
            showDetails("Inner", true) {
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
        fromMicrometers(100),
        true
    )
}

private fun HtmlBlockTag.selectRimFactor(factor: Factor) {
    selectFactor(
        "Rim Factor",
        EDGE,
        factor,
        ZERO,
        MAX_RIM_FACTOR,
        fromPermille(1),
        true,
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
        true,
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
            parseRimFactor(parameters),
        )

        CurrencyFormatType.HoledCoin -> HoledCoin(
            parseMaterialId(parameters, MATERIAL),
            parse(parameters, SHAPE, Shape.Circle),
            parseRadius(parameters),
            parseRimFactor(parameters),
            parse(parameters, combine(HOLE, SHAPE), Shape.Circle),
            parseRadiusFactor(parameters),
            parseBool(parameters, combine(HOLE, EDGE)),
        )

        CurrencyFormatType.BiMetallicCoin -> BiMetallicCoin(
            parseMaterialId(parameters, MATERIAL),
            parse(parameters, SHAPE, Shape.Circle),
            parseRadius(parameters),
            parseRimFactor(parameters),
            parseMaterialId(parameters, combine(HOLE, MATERIAL)),
            parse(parameters, combine(HOLE, SHAPE), Shape.Circle),
            parseRadiusFactor(parameters),
        )
    }

private fun parseRadius(parameters: Parameters): Distance =
    parseDistance(parameters, LENGTH, DEFAULT_RADIUS)

private fun parseRimFactor(parameters: Parameters): Factor =
    parseFactor(parameters, EDGE, DEFAULT_RIM_FACTOR)

private fun parseRadiusFactor(parameters: Parameters): Factor =
    parseFactor(parameters, HOLE, DEFAULT_RADIUS_FACTOR)
