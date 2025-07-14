package giftproject.gift.dto;

import giftproject.gift.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 생성 또는 업데이트 요청을 위한 DTO")
public record ProductRequestDto(
        @Schema(description = "상품명", example = "새로운 노트북", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 15, message = "상품명은 최대 15자까지 입력 가능합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9ㄱ-힣 ()\\[\\]+\\-&/_]*$",
                message = "(), [], +, -, $, /, _ 외의 특수문자는 사용할 수 없습니다."
        )
        String name,

        @Schema(description = "상품 가격", example = "1200000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        Integer price,

        @Schema(description = "상품 이미지 URL", example = "https://example.com/product_image.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이미지는 필수입니다.")
        String imageUrl) {

    public Product toEntity() {
        return new Product(name, price, imageUrl);
    }
}
