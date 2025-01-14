package com.yeom.dexrss.rss.repository;

import com.yeom.dexrss.rss.entity.RssItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RssItemRepository extends JpaRepository<RssItem, Long> {

    // 카테고리 기준 입력한 시간 이후의 항목리스트 조회
    @Query("SELECT m FROM RssItem m WHERE m.category = :category AND m.createdAt >= :goeDate")
    List<RssItem> findByCategoryAndCreatedAtAfter(@Param("category")String category, @Param("goeDate")LocalDateTime goeDate);

    @Query("SELECT m FROM RssItem m WHERE m.category = :category ORDER BY m.boardId DESC limit 20")
    List<RssItem> findLimitByCategory(@Param("category")String category);
}
