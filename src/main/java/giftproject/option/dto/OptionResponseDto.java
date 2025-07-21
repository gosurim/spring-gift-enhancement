package giftproject.option.dto;

import giftproject.option.entity.Option;

public record OptionResponseDto(
        Long id,
        Long productId,
        String optionType,
        String optionValue,
        int quantity
) {

    public OptionResponseDto(Long id, Long productId, String optionType, String optionValue,
            int quantity) {
        this.id = id;
        this.productId = productId;
        this.optionType = optionType;
        this.optionValue = optionValue;
        this.quantity = quantity;
    }

    public static OptionResponseDto from(Option option) {
        return new OptionResponseDto(
                option.getId(),
                option.getProduct() != null ? option.getProduct().getId() : null,
                option.getOptionType(),
                option.getOptionValue(),
                option.getQuantity()
        );
    }
}
