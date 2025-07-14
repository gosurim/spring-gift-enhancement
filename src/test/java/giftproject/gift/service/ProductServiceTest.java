package giftproject.gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import giftproject.gift.dto.ProductRequestDto;
import giftproject.gift.dto.ProductResponseDto;
import giftproject.gift.entity.Product;
import giftproject.gift.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE products");
    }

    @Test
    @DisplayName("카카오 포함")
    void saveProduct_Kakao() {
        ProductRequestDto requestDto = new ProductRequestDto("카카오", 12000,
                "http://img.com/img.jpg");

        assertThatThrownBy(() -> productService.save(requestDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("\"카카오\"가 포함된 문구는 담당 MD와 협의한 경우에만 사용 가능합니다.");

        List<Product> products = productRepository.findAll();
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName("정상 상품 등록")
    void saveProduct_success() {
        ProductRequestDto requestDto = new ProductRequestDto("초코케이크", 10000,
                "http://img.com/cake.jpg");
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
    }
}
