package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.InterpersonalRelationship
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.util.InSettlementMap
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId
import at.orchaldir.gm.core.model.world.settlement.TerrainType
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.Resize

sealed class Action

// data
data class LoadData(val path: String) : Action()

// element
data class CreateAction<ID : Id<ID>>(val id: ID) : Action()
data class CloneAction<ID : Id<ID>>(val id: ID) : Action()
data class DeleteAction<ID : Id<ID>>(val id: ID) : Action()
data class UpdateAction<ID : Id<ID>, ELEMENT : Element<ID>>(val element: ELEMENT) : Action()

// data
data class UpdateData(val data: Data) : Action()

//-- characters --

sealed class CharacterAction : Action()

// character
data class UpdateAppearance(
    val id: CharacterId,
    val appearance: Appearance,
) : CharacterAction()

data class UpdateRelationships(
    val id: CharacterId,
    val relationships: Map<CharacterId, Set<InterpersonalRelationship>>,
) : CharacterAction()

//-- world --

sealed class WorldAction : Action()

// settlement's abstract buildings

data class AddAbstractBuilding(
    val settlement: SettlementMapId,
    val tileIndex: Int,
    val size: MapSize2d = MapSize2d.square(1),
) : WorldAction()

data class RemoveAbstractBuilding(
    val settlement: SettlementMapId,
    val tileIndex: Int,
) : WorldAction()

// settlement's buildings

data class AddBuilding(
    val settlement: SettlementMapId,
    val tileIndex: Int,
    val size: MapSize2d,
) : WorldAction()

data class UpdateActionLot(
    val id: BuildingId,
    val tileIndex: Int,
    val size: MapSize2d,
) : WorldAction() {

    fun applyTo(building: Building) = if (building.position is InSettlementMap) {
        building.copy(
            position = building.position.copy(tileIndex = tileIndex),
            size = size,
        )
    } else {
        error("UpdateActionLot requires InSettlementMap!")
    }
}

// settlement's streets

data class AddStreetTile(
    val settlement: SettlementMapId,
    val tileIndex: Int,
    val type: StreetTemplateId,
    val street: StreetId?,
) : WorldAction()

data class RemoveStreetTile(
    val settlement: SettlementMapId,
    val tileIndex: Int,
) : WorldAction()

// settlement's terrain

data class ResizeTerrain(
    val settlement: SettlementMapId,
    val resize: Resize,
    val terrainType: TerrainType = TerrainType.Plain,
    val terrainId: Int = 0,
) : WorldAction()

data class SetTerrainTile(
    val settlement: SettlementMapId,
    val terrainType: TerrainType,
    val terrainId: Int,
    val tileIndex: Int,
) : WorldAction()
