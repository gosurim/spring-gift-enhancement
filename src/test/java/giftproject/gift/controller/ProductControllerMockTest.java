package giftproject.gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import giftproject.gift.dto.ProductResponseDto;
import giftproject.gift.entity.Product;
import giftproject.gift.repository.ProductRepository;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
        IntStream.rangeClosed(1, 25).forEach(i ->
                productRepository.save(new Product("상품" + i, 10000 + i, "url"))
        );
    }

    @DisplayName("페이지네이션을 적용하여 상품 목록을 조회한다 - 기본값 (page=0, size=10)")
    @Test
    void getProductsWithDefaultPagination() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        JsonNode rootNode = objectMapper.readTree(responseBody);

        List<ProductResponseDto> products = objectMapper.readValue(
                rootNode.get("content").traverse(),
                new TypeReference<List<ProductResponseDto>>() {
                }
        );

        int pageNumber = rootNode.get("pageable").get("pageNumber").asInt();
        long totalElements = rootNode.get("totalElements").asLong();
        int totalPages = rootNode.get("totalPages").asInt();
        boolean isFirst = rootNode.get("first").asBoolean();
        boolean isLast = rootNode.get("last").asBoolean();

        assertThat(products).isNotNull();
        assertThat(products).hasSize(10);
        assertThat(pageNumber).isEqualTo(0);
        assertThat(totalPages).isEqualTo(3);
        assertThat(totalElements).isEqualTo(25);
        assertThat(isFirst).isTrue();
        assertThat(isLast).isFalse();

        assertThat(products.get(0).getName()).isEqualTo("상품25");
        assertThat(products.get(9).getName()).isEqualTo("상품16");
    }

    @DisplayName("페이지네이션을 적용하여 상품 목록을 조회한다 - 특정 페이지 (page=1, size=5)")
    @Test
    void getProductsWithCustomPagination() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        JsonNode rootNode = objectMapper.readTree(responseBody);

        List<ProductResponseDto> products = objectMapper.readValue(
                rootNode.get("content").traverse(),
                new TypeReference<List<ProductResponseDto>>() {
                }
        );

        int pageNumber = rootNode.get("pageable").get("pageNumber").asInt();
        long totalElements = rootNode.get("totalElements").asLong();
        int totalPages = rootNode.get("totalPages").asInt();
        boolean isFirst = rootNode.get("first").asBoolean();
        boolean isLast = rootNode.get("last").asBoolean();

        assertThat(products).isNotNull();
        assertThat(products).hasSize(5);
        assertThat(pageNumber).isEqualTo(1);
        assertThat(totalElements).isEqualTo(25);
        assertThat(totalPages).isEqualTo(5);
        assertThat(isFirst).isFalse();
        assertThat(isLast).isFalse();

        assertThat(products.get(0).getName()).isEqualTo("상품20");
        assertThat(products.get(4).getName()).isEqualTo("상품16");
    }
}
