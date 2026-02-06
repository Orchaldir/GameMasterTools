package at.orchaldir.gm.prototypes.data

import at.orchaldir.gm.core.model.ELEMENTS
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.createStorage
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.Denomination
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Weight
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Command line args: $args" }
    val path = args[0]
    logger.info { "Path: $path" }

    val state = createDefaultState(path)

    Files.createDirectories(Paths.get(path))

    state.save()
}

fun createDefaultState(path: String): State = State(
    ELEMENTS.associateWith { createStorage(it) },
    path,
).updateStorage(
    listOf(
        Storage(createDefaultCalendar()),
        Storage(createDefaultCulture()),
        Storage(createDefaultCurrency()),
        Storage(createDefaultMaterials()),
    )
)

private fun createDefaultCalendar(): Calendar {
    val weekdays = Weekdays(
        listOf(
            WeekDay(Name.init("Monday")),
            WeekDay(Name.init("Tuesday")),
            WeekDay(Name.init("Wednesday")),
            WeekDay(Name.init("Thursday")),
            WeekDay(Name.init("Friday")),
            WeekDay(Name.init("Saturday")),
            WeekDay(Name.init("Sunday")),
        )
    )
    val months = ComplexMonths(
        listOf(
            MonthDefinition(31, "January"),
            MonthDefinition(28, "February"),
            MonthDefinition(31, "March"),
            MonthDefinition(30, "April"),
            MonthDefinition(31, "May"),
            MonthDefinition(30, "June"),
            MonthDefinition(31, "July"),
            MonthDefinition(31, "August"),
            MonthDefinition(30, "September"),
            MonthDefinition(31, "October"),
            MonthDefinition(30, "November"),
            MonthDefinition(31, "December"),
        )
    )
    return Calendar(
        CalendarId(0),
        Name.init("Default Calendar"),
        weekdays,
        months,
    )
}

private fun createDefaultCulture() = Culture(
    CultureId(0),
    Name.init("Default Culture"),
)

private fun createDefaultCurrency() = Currency(
    CurrencyId(0),
    Name.init("Default Currency"),
    subDenominations = listOf(
        Pair(Denomination.init("cp"), 10),
        Pair(Denomination.init("sp"), 100),
    )
)

private fun createDefaultMaterials() = listOf(
    createMaterial(0, "Copper", Color.OrangeRed, 8940),
    createMaterial(1, "Silver", Color.Silver, 10500),
    createMaterial(2, "Gold", Color.Gold, 19320),
    createMaterial(3, "Platinum", Color.AliceBlue, 21450),
    createMaterial(4, "Iron", Color.DimGray, 7870),
    createMaterial(5, "Steel", Color.Gray, 7850),
    createMaterial(6, "Brass", Color.Gold, 8600),
    createMaterial(7, "Bronze", Color.Orange, 8770),
)

private fun createMaterial(
    id: Int,
    name: String,
    color: Color,
    weight: Long,
): Material {
    val properties = MaterialProperties(color = color, density = Weight.fromKilograms(weight))

    return Material(MaterialId(id), Name.init(name), properties)
}