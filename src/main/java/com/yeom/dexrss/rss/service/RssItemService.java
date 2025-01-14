package com.yeom.dexrss.rss.service;

import com.yeom.dexrss.rss.dto.RssItemDto;
import com.yeom.dexrss.rss.entity.RssItem;
import com.yeom.dexrss.rss.repository.RssItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RssItemService {
    private final RssItemRepository rssItemRepository;

    @Transactional
    public void saveDto(RssItem rssItem) {
        log.info("Saving rssItem: {}", rssItem);
        rssItemRepository.save(rssItem);
    }

    @Transactional
    public void save(List<RssItem> rssItems) {
        log.info("Saving rssItems: {}", rssItems);
        rssItemRepository.saveAll(rssItems);
    }

    @Transactional
    public void saveDto(List<RssItemDto> items) {
        List<RssItem> entityList = items.stream().map(x -> RssItem.builder()
                .title(x.getTitle())
                .link(x.getPage())
                .magnetLink(x.getLink())
                .category(x.getCategory())
                .boardId(x.getBoardId())
                .build()
        ).collect(Collectors.toList());

        save(entityList);
    }

    public List<RssItem> findByCategoryAndCreatedAt(String category, LocalDateTime goeDate) {
        return rssItemRepository.findByCategoryAndCreatedAtAfter(category, goeDate);
    }

    public List<RssItemDto> findByCategoryAndLimit(String category) {
        List<RssItem> limitByCategory = rssItemRepository.findLimitByCategory(category);
        return limitByCategory.stream()
                .map(x -> RssItemDto.builder()
                        .title(x.getTitle())
                        .link(x.getMagnetLink())
                        .category(x.getCategory())
                        .page(x.getLink())
                        .boardId(x.getBoardId())
                        .build()
                ).collect(Collectors.toList());
    }

}
