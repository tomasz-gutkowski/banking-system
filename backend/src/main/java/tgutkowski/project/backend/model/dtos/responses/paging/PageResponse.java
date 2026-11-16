package tgutkowski.project.backend.model.dtos.responses.paging;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Single page of paginated list of data")
public class PageResponse<T extends Cursorable<D>, D>{
    @Schema(description = "Ordered list of data")
    private List<D> data;
    @Schema(description = "UUIDv4 pointer at the start of next page, used as cursor query parameter in next request. Cursor equal to null signifies this being a last available page")
    private String cursor;

    public void setPage(List<T> list, int expectedLength){
        if(list == null){
            this.data = List.of();
            this.cursor = null;
        }
        else if(list.size() <= expectedLength){
            this.data = list.stream().map(Cursorable::convertToDTO).toList();
            this.cursor = null;
        }
        else{
            this.data = list.subList(0, expectedLength).stream().map(Cursorable::convertToDTO).toList();
            this.cursor = list.get(expectedLength).getAsCursor();
        }
    }

    public List<D> getData() {
        return data;
    }

    public String getCursor() {
        return cursor;
    }
}
