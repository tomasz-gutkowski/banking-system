package tgutkowski.project.backend.model.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.web.bind.MethodArgumentNotValidException;

public enum StatusChange {
    FREEZE(1),
    UNFREEZE(-1),
    CLOSE(0);

    private final int code;

    StatusChange(int code){
        this.code = code;
    }

    @JsonCreator
    public static StatusChange fromValue(String value) {
        for (StatusChange s : values()) {
            if (String.valueOf(s).equals(value) ||
                String.valueOf(s.code).equals(value)) {
                return s;
            }
        }
        return null;
    }
}
