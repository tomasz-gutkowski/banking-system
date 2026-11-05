package tgutkowski.project.backend.model.dtos.responses.paging;

import java.util.List;

public class PageResponse<T extends Cursorable<D>, D>{
    private List<D> data;
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
