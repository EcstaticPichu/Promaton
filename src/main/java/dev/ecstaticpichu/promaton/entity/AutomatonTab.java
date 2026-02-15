package dev.ecstaticpichu.promaton.entity;

public enum AutomatonTab {
    INVENTORY(0),
    SKIN(1);

    private final int index;

    AutomatonTab(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static AutomatonTab fromIndex(int index) {
        for (AutomatonTab tab : values()) {
            if (tab.index == index) {
                return tab;
            }
        }
        return INVENTORY;
    }
}
