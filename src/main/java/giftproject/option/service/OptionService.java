package giftproject.option.service;

import giftproject.gift.entity.Product;
import giftproject.gift.repository.ProductRepository;
import giftproject.option.dto.OptionRequestDto;
import giftproject.option.dto.OptionResponseDto;
import giftproject.option.entity.Option;
import giftproject.option.repository.OptionRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OptionResponseDto create(Long productId, OptionRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new NoSuchElementException("ID가 " + productId + "인 상품을 찾을 수 없습니다."));

        boolean exists = optionRepository.existsByProductIdAndOptionTypeAndOptionValue(
                productId, request.optionType(), request.optionValue());
        if (exists) {
            throw new IllegalArgumentException(
                    "동일한 상품 내에 옵션 '" + request.optionType() + ": " + request.optionValue()
                            + "'이(가) 이미 존재합니다.");
        }

        Option option = new Option(
                product, request.optionType(), request.optionValue(), request.quantity()
        );
        product.addOrUpdateOption(option);
        Option savedOption = optionRepository.save(option);

        return OptionResponseDto.from(savedOption);
    }

    @Transactional
    public Page<OptionResponseDto> find(Long productId, Pageable pageable) {
        Page<Option> options = optionRepository.findByProductId(productId, pageable);
        return options.map(OptionResponseDto::from);
    }

    @Transactional
    public OptionResponseDto update(Long optionId, OptionRequestDto request) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(
                        () -> new NoSuchElementException("ID가 " + optionId + "인 옵션을 찾을 수 없습니다."));

        if (!option.getOptionType().equals(request.optionType()) ||
                !option.getOptionValue().equals(request.optionValue())) {

            boolean exists = optionRepository.existsByProductIdAndOptionTypeAndOptionValue(
                    request.productId(), request.optionType(), request.optionValue());
            if (exists) {
                throw new IllegalArgumentException(
                        "동일한 상품 내에 옵션 '" + request.optionType() + ": " + request.optionValue()
                                + "'이(가) 이미 존재합니다.");
            }
        }

        option.update(request.optionType(), request.optionValue(), request.quantity());
        optionRepository.save(option);

        return OptionResponseDto.from(option);
    }

    @Transactional
    public void delete(Long optionId, Long productId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(
                        () -> new NoSuchElementException("ID가 " + optionId + "인 옵션을 찾을 수 없습니다."));

        long existingOptionsCount = optionRepository.countByProductId(productId);
        if (existingOptionsCount <= 1) {
            throw new IllegalArgumentException("하나 이상의 옵션이 있어야 하므로 마지막 옵션은 삭제할 수 없습니다.");
        }

        optionRepository.delete(option);
    }
}
