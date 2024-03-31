package com.github.sputnik1111.service.userpay.domain.userlimit;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserLimitRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean upsertCurrentLimitByUserId(long userId, long initLimit, long subLimit){
        if (initLimit<subLimit){
            final String sql = "INSERT INTO user_limit (user_id,current_limit) VALUES (?,?) "+
                "ON CONFLICT (user_id) DO NOTHING";
            jdbcTemplate.update(sql,userId,initLimit);
            return false;
        }
        final String sql = "INSERT INTO user_limit (user_id,current_limit) VALUES (?,?) "+
                "ON CONFLICT (user_id) DO UPDATE SET current_limit=user_limit.current_limit - ? WHERE user_limit.current_limit>=?";

        return jdbcTemplate.update(sql,userId,initLimit-subLimit,subLimit,subLimit)>0;
    }

    public void restoreLimitForAllUsers(long initLimit){
        final String sql = "UPDATE user_limit SET current_limit = ? ";
        jdbcTemplate.update(sql,initLimit);
    }

    public Optional<Long> currentLimitForUser(long userId){
        final String sql = "SELECT current_limit FROM user_limit WHERE user_id = ? ";
        List<Long> result = jdbcTemplate.queryForList(
                sql,
                Long.class,
                userId
        );
        return result.isEmpty()
                ?Optional.empty()
                :Optional.of(result.get(0));
    }

    public void increaseLimits(@NonNull Collection<UserLimitIncreaseDto> dtos){
        if (dtos.isEmpty()) return;
        final String sql = "UPDATE user_limit SET current_limit = LEAST(current_limit + ?,?)  WHERE  user_id = ?";
        List<Object[]> params = dtos.stream()
                        .map(d->new Object[]{d.amount(),d.maxLimit(),d.userId()})
                        .toList();
        jdbcTemplate.batchUpdate(
                sql,
                params
        );
    }
}
