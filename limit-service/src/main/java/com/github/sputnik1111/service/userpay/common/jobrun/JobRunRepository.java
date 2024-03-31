package com.github.sputnik1111.service.userpay.common.jobrun;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JobRunRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean updateNextRun(@NonNull String jobName, @NonNull Instant prevNextRun, @NonNull Instant newNextRun){
        final String sql = "UPDATE job_run SET next_run = ? WHERE job_name = ? and next_run = ? ";
        return jdbcTemplate.update(
                sql,
                Timestamp.from(newNextRun),
                jobName,
                Timestamp.from(prevNextRun)
        )==1;
    }

    public Optional<Instant> getNextRun(@NonNull String jobName){
        final String sql = "SELECT next_run FROM job_run WHERE job_name = ? ";
        List<Timestamp> result = jdbcTemplate.queryForList(
                sql,
                Timestamp.class,
                jobName
        );
        return  result.isEmpty()
                ?Optional.empty()
                :Optional.of(result.get(0).toInstant());

    }

    public boolean insertJob(@NonNull String jobName, @NonNull Instant nextRun){
        final String sql = "INSERT INTO job_run (job_name,next_run) VALUES (?,?) ON CONFLICT(job_name) DO NOTHING";
        return jdbcTemplate.update(sql,jobName,Timestamp.from(nextRun))>0;
    }
}
