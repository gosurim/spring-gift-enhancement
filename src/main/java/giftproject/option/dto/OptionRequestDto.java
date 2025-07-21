package giftproject.option.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OptionRequestDto(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @NotBlank(message = "옵션 유형은 필수입니다.")
        @Size(max = 50, message = "옵션 유형은 공백 포함 50자를 초과할 수 없습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$",
                message = "(), [], +, -, $, /, _ 외의 특수문자는 사용할 수 없습니다.")
        String optionType,

        @NotBlank(message = "옵션 값은 필수입니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$",
                message = "(), [], +, -, $, /, _ 외의 특수문자는 사용할 수 없습니다.")
        String optionValue,

        @Max(value = 99999999, message = "옵션 수량은 1억 개 미만이어야 합니다.")
        int quantity
) {
    
}
