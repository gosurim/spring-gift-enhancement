package giftproject.gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import giftproject.gift.dto.ProductRequestDto;
import giftproject.gift.dto.ProductResponseDto;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE products");
    }


    @Test
    @DisplayName("정상 생성")
    void createProduct_success() {
        ProductRequestDto requestDto = new ProductRequestDto("초코케이크", 10000,
                "http://img.com/image.jpg");

        ResponseEntity<ProductResponseDto> response = restTemplate.postForEntity("/api/products",
                requestDto,
                ProductResponseDto.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody().name()).isEqualTo("초코케이크")
        );
    }

    @Test
    @DisplayName("상품명 15자 초과")
    void createProduct_nameTooLong() {
        ProductRequestDto requestDto = new ProductRequestDto("상품명 15자 초과상품명 15자 초과", 10000,
                "http://img.com/image.jpg");

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.POST,
                new HttpEntity<>(requestDto),
                new ParameterizedTypeReference<>() {
                }
        );

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().get("name")).isEqualTo(
                        "상품명은 최대 15자까지 입력 가능합니다.")
        );
    }

    @Test
    @DisplayName("특수 문자 포함")
    void createProduct_invalidCharacters() {
        ProductRequestDto requestDto = new ProductRequestDto("@", 10000,
                "http://img.com/image.jpg");

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.POST,
                new HttpEntity<>(requestDto),
                new ParameterizedTypeReference<>() {
                }
        );

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().get("name")).isEqualTo(
                        "(), [], +, -, $, /, _ 외의 특수문자는 사용할 수 없습니다.")
        );
    }

    @Test
    @DisplayName("카카오 포함")
    void createProduct_nameContainsKakao() {
        ProductRequestDto requestDto = new ProductRequestDto("카카오", 10000,
                "http://img.com/image.jpg");

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.POST,
                new HttpEntity<>(requestDto),
                new ParameterizedTypeReference<>() {
                }
        );

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().get("error")).isEqualTo(
                        "\"카카오\"가 포함된 문구는 담당 MD와 협의한 경우에만 사용 가능합니다.")
        );
    }
}
