package dev.ftb.mods.sluice.item;

public enum Upgrades {
    SPEED(5, "upgrade_speed"),
    LUCK(3, "upgrade_fortune"),
    CONSUMPTION(5, "upgrade_fluid");

    public int effectedChange;
    public String tooltip;
    Upgrades(int effectedChange, String tooltip) {
        this.effectedChange = effectedChange;
        this.tooltip = tooltip;
    }
}
