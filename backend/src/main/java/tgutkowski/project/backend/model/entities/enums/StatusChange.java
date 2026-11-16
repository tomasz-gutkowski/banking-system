package tgutkowski.project.backend.model.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusChange {
    FREEZE,
    UNFREEZE,
    CLOSE;


    @JsonCreator
    public static StatusChange fromValue(String value) {
        for (StatusChange s : values()) {
            if (String.valueOf(s).equals(value)){
                return s;
            }
        }
        return null;
    }
}
