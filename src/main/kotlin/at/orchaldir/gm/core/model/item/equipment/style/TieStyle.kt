package at.orchaldir.gm.core.model.item.equipment.style

enum class TieStyle {
    ButterflyBowTie,
    DiamondBowTie,
    KnitTie,
    RoundedBowTie,
    SlimBowTie,
    Tie;

    fun isBowTie() = this == ButterflyBowTie || this == DiamondBowTie || this == RoundedBowTie || this == SlimBowTie
}