package giftproject.option.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import giftproject.gift.entity.Product;
import giftproject.gift.repository.ProductRepository;
import giftproject.option.dto.OptionRequestDto;
import giftproject.option.entity.Option;
import giftproject.option.repository.OptionRepository;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class OptionControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionRepository optionRepository;

    private Product product;
    private Option option;
    private OptionRequestDto createRequestDto;
    private OptionRequestDto updateRequestDto;
    private Long productId = 1L;

    @BeforeEach
    void setUp() {
        optionRepository.deleteAll();
        productRepository.deleteAll();

        product = new Product(null, "Test Product", 10000, "url");
        product = productRepository.save(product);
        productId = product.getId();

        option = new Option(product, "Color", "Red", 10);
        createRequestDto = new OptionRequestDto(productId, "Color", "Red", 10);
    }

    @Test
    @DisplayName("옵션 생성 실패 - 특수문자 입력")
    void createWishSpecialChars_shouldFailValidation() {
        createRequestDto = new OptionRequestDto(productId, "@", "Red", 5);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "/api/products/{productId}/options",
                HttpMethod.POST,
                new HttpEntity<>(createRequestDto),
                new ParameterizedTypeReference<>() {
                }, productId
        );

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().get("optionType")).isEqualTo(
                        "(), [], +, -, $, /, _ 외의 특수문자는 사용할 수 없습니다.")
        );
    }
}
