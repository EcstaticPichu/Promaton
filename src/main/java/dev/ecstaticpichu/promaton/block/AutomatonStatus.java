package dev.ecstaticpichu.promaton.block;

public enum AutomatonStatus {
    DEAD(0, IndicatorState.DEAD),
    ERROR(1, IndicatorState.ERROR),
    IDLE(2, IndicatorState.IDLE),
    SLEEPING(3, IndicatorState.READY),
    WORKING(4, IndicatorState.RUNNING),
    COMPANION(5, IndicatorState.RUNNING);

    private final int index;
    private final IndicatorState indicatorState;

    AutomatonStatus(int index, IndicatorState indicatorState) {
        this.index = index;
        this.indicatorState = indicatorState;
    }

    public int getIndex() {
        return index;
    }

    public IndicatorState getIndicatorState() {
        return indicatorState;
    }

    public int getComparatorSignal() {
        return index;
    }

    public static AutomatonStatus fromIndex(int index) {
        for (AutomatonStatus status : values()) {
            if (status.index == index) {
                return status;
            }
        }
        return DEAD;
    }
}
