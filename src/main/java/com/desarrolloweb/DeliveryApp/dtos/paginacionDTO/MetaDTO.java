package com.desarrolloweb.DeliveryApp.dtos.paginacionDTO;

public class MetaDTO {
    private long totalItems;
    private int itemCount;
    private int itemsPerPage;
    private int totalPages;
    private int currentPage;

    // Constructores
    public MetaDTO() {}
    
    public MetaDTO(long totalItems, int itemCount, int itemsPerPage, int totalPages, int currentPage) {
        this.totalItems = totalItems;
        this.itemCount = itemCount;
        this.itemsPerPage = itemsPerPage;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    // --- Getters y Setters ---
    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public int getItemsPerPage() { return itemsPerPage; }
    public void setItemsPerPage(int itemsPerPage) { this.itemsPerPage = itemsPerPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

}
