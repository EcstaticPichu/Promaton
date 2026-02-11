package dev.ecstaticpichu.promaton.block;

import net.minecraft.util.StringRepresentable;

public enum IndicatorState implements StringRepresentable {
    RUNNING("running"),
    READY("ready"),
    IDLE("idle"),
    ERROR("error"),
    DEAD("dead");

    private final String name;

    IndicatorState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
