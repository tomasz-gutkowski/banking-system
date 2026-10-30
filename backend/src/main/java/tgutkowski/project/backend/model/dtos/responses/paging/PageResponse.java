package tgutkowski.project.backend.model.dtos.responses.paging;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            this.cursor = encode(list.get(expectedLength).getAsCursor());
        }
    }

    public static String encode(String cursor){
        //todo after done with debug
        return cursor;
    }

    public static String decode(String cursor){
        // todo after done with debug
        return cursor;
    }

    public List<D> getData() {
        return data;
    }

    public String getCursor() {
        return cursor;
    }
}
