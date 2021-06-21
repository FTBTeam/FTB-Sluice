package dev.ftb.mods.sluice.item;

public enum Upgrades {
    SPEED(5),
    LUCK(3),
    CONSUMPTION(5);

    public int effectedChange;
    Upgrades(int effectedChange) {
        this.effectedChange = effectedChange;
    }
}
