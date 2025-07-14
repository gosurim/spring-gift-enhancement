package giftproject.gift.controller;

import giftproject.gift.dto.ProductRequestDto;
import giftproject.gift.dto.ProductResponseDto;
import giftproject.gift.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품 API", description = "상품 정보를 관리하는 API")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "새로운 상품 등록", description = "상품명, 가격, 이미지 URL을 받아 새로운 상품을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "상품 등록 성공",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(example = "{\"message\": \"상품명은 필수입니다.\"}")))
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto) {
        return new ResponseEntity<>(productService.save(requestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<ProductResponseDto> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable Long id) {
        return new ResponseEntity<>(productService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto requestDto
    ) {
        return new ResponseEntity<>(
                productService.update(id, requestDto.name(), requestDto.price(),
                        requestDto.imageUrl()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
