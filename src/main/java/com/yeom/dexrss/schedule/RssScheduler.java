package com.yeom.dexrss.schedule;

import com.yeom.dexrss.rss.service.RssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Component
public class RssScheduler {
//    private final RssSettingService rssSettingService;
    private final RssService rssService;

    // RSS 사이트 URL 업데이트 및 게시판 정보 추출하여 마그넷 링크 정보 업데이트
    @Scheduled(cron = "0 0 0/2 * * ?")
    public void executeTask() {
        log.info("RssScheduler Executing task");
        String baseUrl = rssService.updateBaseUrl();
        log.info("RssScheduler baseUrl: {}", baseUrl);

        List<String> categories = new ArrayList<>();
        categories.add("drama");
        categories.add("ent");

        for (String category : categories) {
            log.info("RssScheduler Run findAndUpdateRssItem: {}", category);
            rssService.findAndUpdateRssItem(baseUrl, category);
        }
    }
}
