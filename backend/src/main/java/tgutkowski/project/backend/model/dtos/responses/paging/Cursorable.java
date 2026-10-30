package tgutkowski.project.backend.model.dtos.responses.paging;

public interface Cursorable<D>{
    String getAsCursor();
    D convertToDTO();
}
