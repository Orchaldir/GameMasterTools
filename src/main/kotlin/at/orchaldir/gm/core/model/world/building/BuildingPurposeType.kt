package at.orchaldir.gm.core.model.world.building

enum class BuildingPurposeType {
    SingleBusiness,
    SingleFamilyHouse,
    ApartmentHouse;

    fun isBusiness() = this == SingleBusiness
}