package com.desarrolloweb.DeliveryApp.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public class PageResponse<T> {

    private final List<T> data;
    private final Meta meta;

    private PageResponse(List<T> data, Meta meta) {
        this.data = data;
        this.meta = meta;
    }

    public static <S, T> PageResponse<T> from(Page<S> page, Function<S, T> mapper, int currentPage) {
        List<T> data = page.getContent().stream().map(mapper).toList();
        Meta meta = new Meta(
                page.getTotalElements(),
                data.size(),
                page.getSize(),
                page.getTotalPages(),
                currentPage
        );
        return new PageResponse<>(data, meta);
    }

    public List<T> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {

        private final long totalItems;
        private final int itemCount;
        private final int itemsPerPage;
        private final int totalPages;
        private final int currentPage;

        private Meta(long totalItems, int itemCount, int itemsPerPage, int totalPages, int currentPage) {
            this.totalItems = totalItems;
            this.itemCount = itemCount;
            this.itemsPerPage = itemsPerPage;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
        }

        public long getTotalItems() {
            return totalItems;
        }

        public int getItemCount() {
            return itemCount;
        }

        public int getItemsPerPage() {
            return itemsPerPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }
    }
}
