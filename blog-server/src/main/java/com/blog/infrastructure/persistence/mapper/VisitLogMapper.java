package com.blog.infrastructure.persistence.mapper;

import com.blog.domain.entity.VisitLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 访问日志Mapper
 */
@Mapper
public interface VisitLogMapper {

    int insert(VisitLog visitLog);

    Long selectPvByDate(@Param("date") LocalDate date);

    Long selectUvByDate(@Param("date") LocalDate date);

    List<Long> selectPvTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
