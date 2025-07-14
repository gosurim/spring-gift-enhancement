package giftproject.member.repository;

import giftproject.member.entity.Member;
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
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MemberRowMapper memberRowMapper;

    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.memberRowMapper = new MemberRowMapper();
    }

    public Member save(Member member) {
        String sql = "INSERT INTO members (email, password) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, member.getEmail());
            ps.setString(2, member.getPassword());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        member.setId(generatedId);
        return member;
    }

    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, email, password FROM members WHERE email = ?";
        try {
            Member member = jdbcTemplate.queryForObject(sql, memberRowMapper, email);
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Member> findById(Long id) {
        String sql = "SELECT id, email, password FROM members WHERE id = ?";
        try {
            Member member = jdbcTemplate.queryForObject(sql, memberRowMapper, id);
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Member> findAll() {
        String sql = "SELECT id, email, password FROM members";
        return jdbcTemplate.query(sql, memberRowMapper);
    }

    public void update(Member member) {
        String sql = "UPDATE members SET email = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, member.getEmail(), member.getPassword(), member.getId());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM members WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class MemberRowMapper implements RowMapper<Member> {

        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Member(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("password")
            );
        }
    }
}
