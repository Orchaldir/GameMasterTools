package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.MonthDefinition
import at.orchaldir.gm.core.model.character.CHARACTER
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.world.building.BuildByBusiness
import at.orchaldir.gm.core.model.world.building.BuildByCharacter
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BuilderTest {

    private val BUSINESS0 = BusinessId(2)
    private val CHARACTER0 = CharacterId(3)

    private val CALENDAR = Calendar(CalendarId(0), months = listOf(MonthDefinition("a")))
    private val DAY0 = Day(100)
    private val DAY1 = Day(200)
    private val DAY2 = Day(300)

    private val STATE = State(
        listOf(
            Storage(Business(BUSINESS0, startDate = DAY1)),
            Storage(CALENDAR),
            Storage(Character(CHARACTER0, birthDate = DAY1)),
        )
    )

    private val BUILD_BY_BUSINESS = BuildByBusiness(BUSINESS0)
    private val BUILD_BY_CHARACTER = BuildByCharacter(CHARACTER0)

    @Test
    fun `Builder is an unknown character`() {
        val state = STATE.removeStorage(CHARACTER)

        assertIllegalArgument("Cannot use an unknown character 3 as builder!") {
            checkBuilder(state, BUILD_BY_CHARACTER, DAY0)
        }
    }
}