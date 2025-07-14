package giftproject.wishlist.repository;

import giftproject.wishlist.entity.Wish;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class WishRepository {

    private final JdbcTemplate jdbcTemplate;
    private final WishRowMapper wishRowMapper;

    public WishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.wishRowMapper = new WishRowMapper();
    }

    public Wish save(Wish wish) {
        String sql = "INSERT INTO wishes (member_id, product_id, quantity) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, wish.getMemberId());
            ps.setLong(2, wish.getProductId());
            ps.setInt(3, wish.getQuantity());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        wish.setId(generatedId);
        return wish;
    }

    public int saveDuplicate(Long memberId, Long productId) {
        String selectSql = "SELECT quantity FROM wishes WHERE member_id = ? AND product_id = ?";
        Integer currentQuantity = jdbcTemplate.queryForObject(selectSql, Integer.class, memberId,
                productId);

        String updateSql = "UPDATE wishes SET quantity = ? WHERE member_id = ? AND product_id = ?";
        return jdbcTemplate.update(updateSql, currentQuantity + 1, memberId, productId);
    }

    public int countProductsByMemberID(Long memberId) {
        String sql = "SELECT COUNT(DISTINCT product_id) FROM wishes WHERE member_id = ? ";
        return jdbcTemplate.queryForObject(sql, Integer.class, memberId);
    }

    public List<Wish> findByMemberId(Long memberId) {
        String sql = "SELECT id, member_id, product_id, quantity FROM wishes WHERE member_id = ?";
        return jdbcTemplate.query(sql, wishRowMapper, memberId);
    }

    public Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId) {
        String sql = "SELECT id, member_id, product_id, quantity FROM wishes WHERE member_id = ? AND product_id = ?";
        try {
            Wish wish = jdbcTemplate.queryForObject(sql, wishRowMapper, memberId, productId);
            return Optional.of(wish);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteByMemberIdAndProductId(Long memberId,
            Long productId) {
        String sql = "DELETE FROM wishes WHERE member_id = ? AND product_id = ?";
        jdbcTemplate.update(sql, memberId, productId);
    }

    private static class WishRowMapper implements RowMapper<Wish> {

        @Override
        public Wish mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Wish(
                    rs.getLong("id"),
                    rs.getLong("member_id"),
                    rs.getLong("product_id"),
                    rs.getInt("quantity")
            );
        }
    }
}
