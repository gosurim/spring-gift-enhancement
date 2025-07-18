package giftproject.wishlist.service;

import giftproject.gift.entity.Product;
import giftproject.gift.service.ProductService;
import giftproject.member.entity.Member;
import giftproject.member.service.MemberService;
import giftproject.wishlist.dto.WishRequestDto;
import giftproject.wishlist.dto.WishResponseDto;
import giftproject.wishlist.entity.Wish;
import giftproject.wishlist.repository.WishRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;
    private final MemberService memberService;

    private static final int MAX_WISHLIST_PRODUCT_COUNT = 30;
    private static final int WISH_EXPIRATION_DAYS = 30;

    public WishService(WishRepository wishRepository, ProductService productService,
            MemberService memberService) {
        this.wishRepository = wishRepository;
        this.productService = productService;
        this.memberService = memberService;
    }

    @Transactional
    public WishResponseDto save(Long memberId, WishRequestDto requestDto) {
        Product product = productService.findEntityById(requestDto.productId());
        Member member = memberService.findEntityById(memberId);

        Wish savedWish = member.addOrUpdateWish(product, MAX_WISHLIST_PRODUCT_COUNT);
        wishRepository.save(savedWish);

        return WishResponseDto.from(savedWish);
    }

    @Transactional(readOnly = true)
    public Page<WishResponseDto> findRecent(Long memberId, Pageable pageable) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(WISH_EXPIRATION_DAYS);
        Page<Wish> wishPage = wishRepository.findByMemberIdAndCreationDateAfter(memberId,
                thirtyDaysAgo, pageable);
        return wishPage.map(WishResponseDto::from);
    }

    @Transactional(readOnly = true)
    public List<WishResponseDto> find(Long memberId) {
        List<Wish> wishes = wishRepository.findByMemberId(memberId);

        return wishes.stream()
                .map(WishResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void remove(Long memberId, Long productId) {
        wishRepository.findByMemberIdAndProductId(memberId, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "위시 리스트에서 해당 상품을 찾을 수 없습니다."));

        wishRepository.deleteByMemberIdAndProductId(memberId, productId);
    }

    @Transactional
    public int deleteExpiredWishesManually() {
        LocalDateTime thrityDaysAge = LocalDateTime.now().minusDays(WISH_EXPIRATION_DAYS);
        int deletedCount = wishRepository.deleteByCreationDateBefore(thrityDaysAge);
        System.out.println("만료된 위시 리스트" + deletedCount + "개가 삭제되었습니다.");
        return deletedCount;
    }
}
