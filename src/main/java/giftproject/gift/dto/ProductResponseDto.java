package giftproject.gift.dto;

import giftproject.gift.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 조회 응답을 위한 DTO")
public record ProductResponseDto(
        @Schema(description = "상품 ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "상품명", example = "선풍기")
        String name,

        @Schema(description = "상품 가격", example = "1500000")
        Integer price,

        @Schema(description = "상품 이미지 URL", example = "https://example.com/product_s24.jpg")
        String imageUrl) {

    public ProductResponseDto(Product product) {
        this(product.getId(), product.getName(), product.getPrice(), product.getImageUrl());
    }

    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(product.getId(), product.getName(), product.getPrice(),
                product.getImageUrl());
    }
}
