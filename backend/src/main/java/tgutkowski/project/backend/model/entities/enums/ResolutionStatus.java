package tgutkowski.project.backend.model.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ResolutionStatus {
    PENDING,
    ACCEPTED,
    DECLINED;

    @JsonCreator
    public static ResolutionStatus fromValue(String value) {
        for (ResolutionStatus s : values()) {
            if (String.valueOf(s).equals(value)){
                return s;
            }
        }
        return null;
    }
}
