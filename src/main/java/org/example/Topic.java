package org.example;

import lombok.Getter;

@Getter
public enum Topic {
    TEMPERATURE("Temperature"),
    CO2("CO2");

    private final String value;
    Topic(String value) {
        this.value = value;
    }

    public static Topic fromValue(String value) {
        for (final Topic dayOfWeek : values()) {
            if (dayOfWeek.value.equalsIgnoreCase(value)) {
                return dayOfWeek;
            }
        }
        return null;
    }
}
