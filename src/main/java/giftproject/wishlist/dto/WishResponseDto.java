package giftproject.wishlist.dto;

import giftproject.gift.dto.ProductResponseDto;
import giftproject.wishlist.entity.Wish;

public record WishResponseDto(
        Long id,
        Long memberId,
        ProductResponseDto product,
        int quantity
) {

    public WishResponseDto(Wish wish, ProductResponseDto productResponseDto) {
        this(wish.getId(), wish.getMember().getId(), productResponseDto, wish.getQuantity());
    }

    public static WishResponseDto from(Wish wish) {
        ProductResponseDto productDto = null;
        if (wish.getProduct() != null) {
            productDto = ProductResponseDto.from(wish.getProduct());
        }

        Long memberId = null;
        if (wish.getMember() != null) {
            memberId = wish.getMember().getId();
        }

        return new WishResponseDto(
                wish.getId(),
                memberId,
                productDto,
                wish.getQuantity()
        );
    }
}