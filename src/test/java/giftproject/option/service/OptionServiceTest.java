package giftproject.option.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import giftproject.gift.entity.Product;
import giftproject.gift.repository.ProductRepository;
import giftproject.option.dto.OptionRequestDto;
import giftproject.option.dto.OptionResponseDto;
import giftproject.option.entity.Option;
import giftproject.option.repository.OptionRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OptionServiceTest {

    @Autowired
    private OptionService optionService;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product product;
    private Option option;
    private OptionRequestDto createRequestDto;
    private OptionRequestDto updateRequestDto;
    private Long productId = 1L;
    private Long optionId = 101L;

    @BeforeEach
    void setUp() {
        optionRepository.deleteAll();
        productRepository.deleteAll();

        product = new Product(null, "Test Product", 10000, "url");
        product = productRepository.save(product);
        productId = product.getId();

        option = new Option(product, "Color", "Red", 10);

        createRequestDto = new OptionRequestDto(productId, "Color", "Red", 10);
        updateRequestDto = new OptionRequestDto(productId, "Color", "Blue", 15);
    }

    @Test
    @DisplayName("옵션 생성 성공")
    void createOption_success() {
        OptionResponseDto result = optionService.create(productId, createRequestDto);

        Optional<Option> foundOption = optionRepository.findById(result.id());
        assertThat(foundOption).isPresent();
        assertThat(foundOption.get().getOptionValue()).isEqualTo("Red");
        assertThat(foundOption.get().getQuantity()).isEqualTo(10);
        assertThat(foundOption.get().getProduct().getId()).isEqualTo(productId);

        assertThat(result.optionValue()).isEqualTo("Red");
        assertThat(result.quantity()).isEqualTo(10);
        assertThat(result.productId()).isEqualTo(productId);
    }

    @Test
    @DisplayName("옵션 생성 실패 - 중복 옵션")
    void createOption_duplicateOption() {
        Option existingOption = new Option(product, "Color", "Red", 5);
        optionRepository.save(existingOption);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            optionService.create(productId, createRequestDto);
        });

        assertThat(thrown.getMessage()).isEqualTo("동일한 상품 내에 옵션 'Color: Red'이(가) 이미 존재합니다.");

        long count = optionRepository.countByProductId(productId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("옵션 목록 조회 성공")
    void findOptions_success() {
        optionRepository.save(new Option(product, "Color", "Red", 10));
        optionRepository.save(new Option(product, "Color", "Blue", 20));
        optionRepository.save(new Option(product, "Size", "Small", 5));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<OptionResponseDto> result = optionService.find(productId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).optionValue()).isEqualTo("Red");
    }

    @Test
    @DisplayName("옵션 업데이트 성공")
    void updateOption_success() {
        Option savedOption = optionRepository.save(new Option(product, "Color", "Red", 10));
        Long savedOptionId = savedOption.getId();

        OptionResponseDto result = optionService.update(savedOptionId, updateRequestDto);

        Optional<Option> updatedOption = optionRepository.findById(savedOptionId);
        assertThat(updatedOption).isPresent();
        assertThat(updatedOption.get().getOptionValue()).isEqualTo("Blue");
        assertThat(updatedOption.get().getQuantity()).isEqualTo(15);

        assertThat(result.optionValue()).isEqualTo("Blue");
        assertThat(result.quantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("옵션 업데이트 실패 - 옵션 없음")
    void updateOption_optionNotFound() {
        Long nonExistentOptionId = 999L;

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            optionService.update(nonExistentOptionId, updateRequestDto);
        });

        assertThat(thrown.getMessage()).isEqualTo("ID가 999인 옵션을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("옵션 삭제 성공")
    void deleteOption_success() {
        Option optionToDelete = optionRepository.save(new Option(product, "Color", "Red", 10));
        optionRepository.save(new Option(product, "Color", "Blue", 20));

        optionService.delete(optionToDelete.getId(), productId);

        assertThat(optionRepository.findById(optionToDelete.getId())).isNotPresent();
        assertThat(optionRepository.countByProductId(productId)).isEqualTo(1);
    }

    @Test
    @DisplayName("옵션 삭제 실패 - 마지막 옵션 삭제 불가")
    void deleteOption_lastOptionNotDeletable() {
        Option onlyOption = optionRepository.save(new Option(product, "Color", "Red", 10));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            optionService.delete(onlyOption.getId(), productId);
        });

        assertThat(thrown.getMessage()).isEqualTo("하나 이상의 옵션이 있어야 하므로 마지막 옵션은 삭제할 수 없습니다.");
        assertThat(optionRepository.findById(onlyOption.getId())).isPresent();
        assertThat(optionRepository.countByProductId(productId)).isEqualTo(1);
    }
}
