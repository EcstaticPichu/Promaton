package dev.ecstaticpichu.promaton.block;

public enum ControllerTab {
    STATUS(0),
    CONTROL(1),
    LOGS(2),
    SKIN(3);

    private final int index;

    ControllerTab(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static ControllerTab fromIndex(int index) {
        for (ControllerTab tab : values()) {
            if (tab.index == index) {
                return tab;
            }
        }
        return STATUS;
    }
}
