package giftproject.gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import giftproject.gift.dto.ProductRequestDto;
import giftproject.gift.dto.ProductResponseDto;
import giftproject.gift.entity.Product;
import giftproject.gift.repository.ProductRepository;
import giftproject.option.dto.OptionRequestDto;
import giftproject.option.repository.OptionRepository;
import giftproject.option.service.OptionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
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
public class ProductControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionService optionService;

    @Autowired
    private OptionRepository optionRepository;

    private OptionRequestDto createRequestDto;
    private Long productId = 1L;
    private List<OptionRequestDto> options = new ArrayList<>();

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        optionRepository.deleteAll();
        createRequestDto = new OptionRequestDto(productId, "Color", "Red", 10);
        options.add(createRequestDto);
    }

    @Test
    void 정상_생성() {
        ProductRequestDto requestDto = new ProductRequestDto("초코케이크", 10000,
                "http://img.com/image.jpg", options);

        ResponseEntity<ProductResponseDto> response = restTemplate.postForEntity("/api/products",
                requestDto,
                ProductResponseDto.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody().name()).isEqualTo("초코케이크")
        );

        List<Product> productsInDb = productRepository.findAll();
        assertAll(
                () -> assertThat(productsInDb).hasSize(1),
                () -> assertThat(productsInDb.get(0).getName()).isEqualTo("초코케이크"),
                () -> assertThat(productsInDb.get(0).getPrice()).isEqualTo(10000),
                () -> assertThat(productsInDb.get(0).getImageUrl()).isEqualTo(
                        "http://img.com/image.jpg")
        );
    }

    @Test
    void 상품명_15자_초과() {
        ProductRequestDto requestDto = new ProductRequestDto("상품명 15자 초과상품명 15자 초과", 10000,
                "http://img.com/image.jpg", options);

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

        assertThat(productRepository.findAll().isEmpty());
    }

    @Test
    void 특수_문자_포함() {
        ProductRequestDto requestDto = new ProductRequestDto("@", 10000,
                "http://img.com/image.jpg", options);

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
    void 카카오_포함() {
        ProductRequestDto requestDto = new ProductRequestDto("카카오", 10000,
                "http://img.com/image.jpg", options);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.POST,
                new HttpEntity<>(requestDto),
                new ParameterizedTypeReference<>() {
                }
        );

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().get("message")).isEqualTo(
                        "\"카카오\"가 포함된 문구는 담당 MD와 협의한 경우에만 사용 가능합니다.")
        );
    }

}
