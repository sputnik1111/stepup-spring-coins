package com.github.sputnik1111.service.userpay.domain.payconfirm;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PayConfirmRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final UserPayConfirmViewRowMapper userPayConfirmViewRowMapper = new UserPayConfirmViewRowMapper();

    public PayConfirmView insert(long userId, long subLimit){
        final String sql = "INSERT INTO user_pay_confirm (pay_id,user_id,sub_limit) VALUES (?,?,?) RETURNING id";
        UUID payId = UUID.randomUUID();
        Long id = jdbcTemplate.getJdbcTemplate().queryForObject(sql,Long.class,payId,userId,subLimit);
        return new PayConfirmView(id,payId,userId,subLimit);
    }

    public void deleteAllByPayIds(@NonNull Collection<UUID> payIds){
        if(payIds.isEmpty()) return;
        final String sql = "DELETE FROM user_pay_confirm WHERE pay_id in (:payIds)";
        jdbcTemplate.update(
                sql,
                Map.of("payIds",payIds)
        );
    }

    public void deleteAll(){
        jdbcTemplate.getJdbcTemplate().update("DELETE FROM user_pay_confirm");
    }

    public List<PayConfirmView> findAllOrderByIdForUpdateSkipLocked(int size, long startIdExclude){
        final String sql = UserPayConfirmViewRowMapper.SELECT_SQL+ "WHERE id>? ORDER BY id LIMIT ? FOR UPDATE SKIP LOCKED";
        return jdbcTemplate.getJdbcTemplate().query(
                sql,
                userPayConfirmViewRowMapper,
                startIdExclude,
                size
        );
    }


    private static class UserPayConfirmViewRowMapper implements RowMapper<PayConfirmView> {

        static final String SELECT_SQL = "SELECT id,pay_id,user_id,sub_limit FROM user_pay_confirm ";

        @Override
        public PayConfirmView mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PayConfirmView(
                    rs.getLong("id"),
                    rs.getObject("pay_id", java.util.UUID.class),
                    rs.getLong("user_id"),
                    rs.getLong("sub_limit")
            );
        }
    }
}
