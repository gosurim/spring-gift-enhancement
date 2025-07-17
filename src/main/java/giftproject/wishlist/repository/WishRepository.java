package giftproject.wishlist.repository;

import giftproject.wishlist.entity.Wish;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    List<Wish> findByMemberId(Long memberId);

    Page<Wish> findByMemberId(Long memberId, Pageable pageable);

    Page<Wish> findByMemberIdAndCreationDateAfter(Long memberId, LocalDateTime thresholdDate,
            Pageable pageable);

    Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId);

    @Query("SELECT COUNT(DISTINCT w.product.id) FROM Wish w WHERE w.member.id = :memberId")
    int countProductsByMemberID(@Param("memberId") Long memberId);

    void deleteByMemberIdAndProductId(Long memberId, Long productId);

    @Modifying
    @Query("DELETE FROM Wish w WHERE w.creationDate < :thresholdDate")
    int deleteByCreationDateBefore(@Param("thresholdDate") LocalDateTime thresholdDate);
}
