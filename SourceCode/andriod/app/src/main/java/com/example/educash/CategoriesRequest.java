package com.example.educash;

public class CategoriesRequest {

    private Integer categorieId;

    private String categorieName;

    public CategoriesRequest(Integer categorieId, String categorieName) {
        this.categorieId = categorieId;
        this.categorieName = categorieName;
    }

    public Integer getCategorieId() {
        return categorieId;
    }

    public String getCategorieName() {
        return categorieName;
    }


}
