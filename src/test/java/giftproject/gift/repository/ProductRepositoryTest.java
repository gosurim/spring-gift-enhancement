package giftproject.gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import giftproject.gift.entity.Product;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 상품저장및조회성공() {
        Product product = new Product("바나나", 100000, "url");

        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("바나나");
        assertThat(foundProduct.getPrice()).isEqualTo(100000);
        assertThat(foundProduct.getImageUrl()).isEqualTo("url");
        assertThat(foundProduct.getId()).isNotNull();
    }

    @DisplayName("ID로 상품을 조회하면 해당 상품을 반환한다")
    @Test
    void findProductById() {
        Product product = new Product("바나나", 100000, "url");
        Product savedProduct = productRepository.save(product);

        Optional<Product> foundProductOptional = productRepository.findById(savedProduct.getId());

        assertThat(foundProductOptional).isPresent();
        Product foundProduct = foundProductOptional.get();
        assertThat(foundProduct.getName()).isEqualTo("바나나");
        assertThat(foundProduct.getId()).isEqualTo(savedProduct.getId());
    }

    @DisplayName("존재하지 않는 ID로 상품을 조회하면 Optional.empty를 반환한다")
    @Test
    void findProductByNonExistingId() {
        Long nonExistingId = 999L;
        Optional<Product> foundProductOptional = productRepository.findById(nonExistingId);

        assertThat(foundProductOptional).isEmpty();
    }
}
