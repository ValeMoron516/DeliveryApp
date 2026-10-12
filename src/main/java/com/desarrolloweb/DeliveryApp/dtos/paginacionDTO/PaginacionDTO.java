package com.desarrolloweb.DeliveryApp.dtos.paginacionDTO;

import java.util.List;

public class PaginacionDTO<T> {
    private List<T> data;
    private MetaDTO meta;

    // Constructores
    public PaginacionDTO() { }

    public PaginacionDTO(List<T> data, MetaDTO meta) {
        this.data = data;
        this.meta = meta;
    }

    // --- Getters y Setters ---
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }

    public MetaDTO getMeta() { return meta; }
    public void setMeta(MetaDTO meta) { this.meta = meta; }
}
