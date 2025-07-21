package giftproject.option.repository;

import giftproject.option.entity.Option;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

    boolean existsByProductIdAndOptionTypeAndOptionValue(
            Long productId, String optionType, String optionValue);

    Optional<Option> findByOptionTypeAndOptionValue(String optionType, String optionValue);

    List<Option> findAllByProductId(Long productId);

    List<Option> findByOptionType(String optionType);

    Page<Option> findByProductId(Long productId, Pageable pageable);

    long countByProductId(Long productId);
}
