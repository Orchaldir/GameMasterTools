package at.orchaldir.gm.app.html.economy.money

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.item.equipment.selectMaterial
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.selector.economy.money.calculateWeight
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPermille
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.SiPrefix
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
    showDetails("Value", true) {
        fieldLink("Currency", call, state, unit.currency)
        field("Number", unit.number)
        field("Denomination", unit.denomination)
        fieldValue(state, unit)
    }
    fieldWeight("Weight", state.calculateWeight(unit))
    showCurrencyFormat(call, state, unit.format)
}

private fun HtmlBlockTag.fieldValue(
    state: State,
    unit: CurrencyUnit,
) {
    val currency = state.getCurrencyStorage().getOrThrow(unit.currency)
    val denomination = currency.getDenomination(unit.denomination)
    field("Value", denomination.display(unit.number))
}

fun HtmlBlockTag.showCurrencyFormat(
    call: ApplicationCall,
    state: State,
    format: CurrencyFormat,
) {
    showDetails("Format", true) {
        field("Type", format.getType())

        when (format) {
            UndefinedCurrencyFormat -> doNothing()
            is Coin -> {
                fieldLink("Material", call, state, format.material)
                showComplexShape(format.shape)
                fieldDistance("Radius", format.radius)
                fieldDistance("Thickness", format.thickness)
                fieldFactor("Rim Factor", format.rimFactor)
                showCoinSide(call, state, format.front, "Front")
            }

            is HoledCoin -> {
                fieldLink("Material", call, state, format.material)
                showComplexShape(format.shape)
                fieldDistance("Radius", format.radius)
                fieldDistance("Thickness", format.thickness)
                fieldFactor("Rim Factor", format.rimFactor)
                showDetails("Hole") {
                    showComplexShape(format.holeShape)
                    fieldFactor("Factor", format.holeFactor)
                    field("Has rim?", format.hasHoleRim)
                }
            }

            is BiMetallicCoin -> {
                showDetails("Outer") {
                    fieldLink("Material", call, state, format.material)
                    showComplexShape(format.shape)
                    fieldDistance("Radius", format.radius)
                    fieldFactor("Rim Factor", format.rimFactor)
                }
                fieldDistance("Thickness", format.thickness)
                showDetails("Inner") {
                    fieldLink("Material", call, state, format.innerMaterial)
                    showComplexShape(format.innerShape)
                    fieldFactor("Factor", format.innerFactor)
                }
                showCoinSide(call, state, format.front, "Front")
            }
        }
    }
}

// edit

fun FORM.editCurrencyUnit(
    state: State,
    unit: CurrencyUnit,
) {
    val currency = state.getCurrencyStorage().getOrThrow(unit.currency)
    selectName(unit.name)
    showDetails("Value", true) {
        selectElement(
            state,
            "Currency",
            CURRENCY,
            state.getCurrencyStorage().getAll(),
            unit.currency,
        )
        selectInt(
            "Number",
            unit.number,
            1,
            10000,
            1,
            NUMBER,
        )
        selectInt(
            "Denomination",
            unit.denomination,
            0,
            currency.countDenominations() - 1,
            1,
            combine(DENOMINATION, NUMBER),
        )
        fieldValue(state, unit)
    }
    fieldWeight("Weight", state.calculateWeight(unit))
    editCurrencyFormat(state, unit.format)
}

fun HtmlBlockTag.editCurrencyFormat(
    state: State,
    format: CurrencyFormat,
) {
    showDetails("Format", true) {
        selectValue(
            "Type",
            FORMAT,
            CurrencyFormatType.entries,
            format.getType(),
        )

        when (format) {
            UndefinedCurrencyFormat -> doNothing()
            is Coin -> {
                selectMaterial(state, format.material, MATERIAL)
                selectComplexShape(format.shape, SHAPE)
                selectRadius(format.radius)
                selectThickness(format.thickness)
                selectRimFactor(format.rimFactor)
                editCoinSide(state, format.front, "Front", FRONT)
            }

            is HoledCoin -> {
                selectMaterial(state, format.material, MATERIAL)
                selectComplexShape(format.shape, SHAPE)
                selectRadius(format.radius)
                selectThickness(format.thickness)
                selectRimFactor(format.rimFactor)
                showDetails("Hole", true) {
                    selectComplexShape(format.holeShape, combine(HOLE, SHAPE))
                    selectRadiusFactor(format.holeFactor)
                    selectBool("Has rim?", format.hasHoleRim, combine(HOLE, EDGE))
                }
            }

            is BiMetallicCoin -> {
                showDetails("Outer", true) {
                    selectMaterial(state, format.material, MATERIAL)
                    selectComplexShape(format.shape, SHAPE)
                    selectRadius(format.radius)
                    selectRimFactor(format.rimFactor)
                }
                selectThickness(format.thickness)
                showDetails("Inner", true) {
                    selectMaterial(state, format.innerMaterial, combine(HOLE, MATERIAL))
                    selectComplexShape(format.innerShape, combine(HOLE, SHAPE))
                    selectRadiusFactor(format.innerFactor)
                }
                editCoinSide(state, format.front, "Front", FRONT)
            }
        }
    }
}

private fun HtmlBlockTag.selectRadius(radius: Distance) {
    selectDistance(
        "Radius",
        LENGTH,
        radius,
        MIN_RADIUS,
        MAX_RADIUS,
        SiPrefix.Milli,
    )
}

private fun HtmlBlockTag.selectThickness(thickness: Distance) {
    selectDistance(
        "Thickness",
        THICKNESS,
        thickness,
        MIN_THICKNESS,
        MAX_THICKNESS,
        SiPrefix.Micro,
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
    parseSimpleOptionalInt(parameters, param)?.let { CurrencyUnitId(it) }

fun parseCurrencyUnit(parameters: Parameters, id: CurrencyUnitId): CurrencyUnit = CurrencyUnit(
    id,
    parseName(parameters),
    parseCurrencyId(parameters, CURRENCY),
    parseInt(parameters, NUMBER, 1),
    parseInt(parameters, combine(DENOMINATION, NUMBER), 0),
    parseCurrencyFormat(parameters),
)

fun parseCurrencyFormat(parameters: Parameters) =
    when (parse(parameters, FORMAT, CurrencyFormatType.Undefined)) {
        CurrencyFormatType.Undefined -> UndefinedCurrencyFormat
        CurrencyFormatType.Coin -> Coin(
            parseMaterialId(parameters, MATERIAL),
            parseComplexShape(parameters, SHAPE),
            parseRadius(parameters),
            parseThickness(parameters),
            parseRimFactor(parameters),
            parseCoinSide(parameters, FRONT)
        )

        CurrencyFormatType.HoledCoin -> HoledCoin(
            parseMaterialId(parameters, MATERIAL),
            parseComplexShape(parameters, SHAPE),
            parseRadius(parameters),
            parseThickness(parameters),
            parseRimFactor(parameters),
            parseComplexShape(parameters, combine(HOLE, SHAPE)),
            parseRadiusFactor(parameters),
            parseBool(parameters, combine(HOLE, EDGE)),
        )

        CurrencyFormatType.BiMetallicCoin -> BiMetallicCoin(
            parseMaterialId(parameters, MATERIAL),
            parseComplexShape(parameters, SHAPE),
            parseRadius(parameters),
            parseThickness(parameters),
            parseRimFactor(parameters),
            parseMaterialId(parameters, combine(HOLE, MATERIAL)),
            parseComplexShape(parameters, combine(HOLE, SHAPE)),
            parseRadiusFactor(parameters),
            parseCoinSide(parameters, FRONT)
        )
    }

private fun parseRadius(parameters: Parameters): Distance =
    parseDistance(parameters, LENGTH, SiPrefix.Milli, DEFAULT_RADIUS)

private fun parseThickness(parameters: Parameters): Distance =
    parseDistance(parameters, THICKNESS, SiPrefix.Micro, DEFAULT_THICKNESS)

private fun parseRimFactor(parameters: Parameters): Factor =
    parseFactor(parameters, EDGE, DEFAULT_RIM_FACTOR)

private fun parseRadiusFactor(parameters: Parameters): Factor =
    parseFactor(parameters, HOLE, DEFAULT_RADIUS_FACTOR)
