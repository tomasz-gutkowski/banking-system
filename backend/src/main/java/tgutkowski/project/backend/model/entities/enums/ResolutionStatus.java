package tgutkowski.project.backend.model.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

public enum ResolutionStatus {
    PENDING(0),
    ACCEPTED(1),
    DECLINED(-1);

    private final int code;
    ResolutionStatus(int code) {
        this.code = code;
    }

    @JsonCreator
    public static ResolutionStatus fromValue(String value) {
        for (ResolutionStatus s : values()) {
            if (String.valueOf(s).equals(value) ||
                    String.valueOf(s.code).equals(value)) {
                return s;
            }
        }
        return null;
    }

    @Component
    public static class ResolutionStatusConverter implements Converter<String, ResolutionStatus> {
        @Override
        public ResolutionStatus convert(String source) {
            return fromValue(source);
        }
    }
}
