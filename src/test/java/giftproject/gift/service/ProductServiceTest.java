package giftproject.gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import giftproject.gift.dto.ProductRequestDto;
import giftproject.gift.dto.ProductResponseDto;
import giftproject.gift.entity.Product;
import giftproject.gift.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("상품명에 '카카오' 포함 시 예외 발생 및 DB에 저장되지 않음")
    void saveProduct_Kakao() {
        ProductRequestDto requestDto = new ProductRequestDto("카카오", 12000,
                "http://img.com/img.jpg", new ArrayList<>());

        assertThatThrownBy(() -> productService.save(requestDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("카카오");

        List<Product> products = productRepository.findAll();
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName("정상적인 상품 등록 시 DB에 성공적으로 저장되고 응답 DTO 반환")
    void saveProduct_success() {
        ProductRequestDto requestDto = new ProductRequestDto("초코케이크", 10000,
                "http://img.com/cake.jpg", new ArrayList<>());

        ProductResponseDto result = productService.save(requestDto);

        assertAll(
                () -> assertThat(result.id()).isNotNull(),
                () -> assertThat(result.name()).isEqualTo("초코케이크"),
                () -> assertThat(result.price()).isEqualTo(10000),
                () -> assertThat(result.imageUrl()).isEqualTo("http://img.com/cake.jpg")
        );
        Optional<Product> savedInDb = productRepository.findById(result.id());
        assertThat(savedInDb).isPresent();
        assertThat(savedInDb.get().getName()).isEqualTo("초코케이크");
        assertThat(savedInDb.get().getPrice()).isEqualTo(10000);
    }
}
