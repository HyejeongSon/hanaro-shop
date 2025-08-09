package com.hanaro.shop.domain;

import lombok.Getter;

@Getter
public enum ProductCategory {
    ELECTRONICS("전자기기"),
    CLOTHING("의류"),
    FOOD("식품"),
    BOOK("도서"),
    BEAUTY("뷰티");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }
}
