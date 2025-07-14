package giftproject.gift.service;

import giftproject.gift.dto.ProductRequestDto;
import giftproject.gift.dto.ProductResponseDto;
import giftproject.gift.entity.Product;
import giftproject.gift.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto save(ProductRequestDto requestDto) {
        Product product = requestDto.toEntity();
        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.from(savedProduct);
    }

    public List<ProductResponseDto> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new ProductResponseDto(product);
    }

    public ProductResponseDto update(Long id, String name, Integer price, String url) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        product.update(name, price, url);
        productRepository.update(product);

        return ProductResponseDto.from(product);
    }

    public void delete(Long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productRepository.delete(id);
    }
}
