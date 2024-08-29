package com.devsuperior.dscommerce.dto;

import com.devsuperior.dscommerce.entities.Product;

public class ProductMinDTO {

    private Long id;
    private String name;
    private String imgUrl;
    private Double price;

    public ProductMinDTO(Long id, String name, String imgUrl, Double price) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.price = price;
    }

    public ProductMinDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        imgUrl = entity.getImgUrl();
        price = entity.getPrice();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Double getPrice() {
        return price;
    }
}
