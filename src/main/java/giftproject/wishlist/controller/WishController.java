package giftproject.wishlist.controller;

import giftproject.member.annotation.LoginMember;
import giftproject.member.entity.Member;
import giftproject.wishlist.dto.WishRequestDto;
import giftproject.wishlist.dto.WishResponseDto;
import giftproject.wishlist.service.WishService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<WishResponseDto> save(
            @Valid @RequestBody WishRequestDto requestDto,
            @LoginMember Member member
    ) {
        WishResponseDto responseDto = wishService.save(member.getId(), requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<WishResponseDto>> findWishListByMember(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            @LoginMember Member member
    ) {
        Page<WishResponseDto> wishPage = wishService.findAllPage(member.getId(), pageable);
        return ResponseEntity.ok(wishPage);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long productId,
            @LoginMember Member member
    ) {
        wishService.remove(member.getId(), productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
