package com.yeom.dexrss.control;

import com.yeom.dexrss.rss.dto.RssSearchDto;
import com.yeom.dexrss.rss.service.RssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/test/api/rss")
@Slf4j
@RequiredArgsConstructor
public class TestController {
    private final RssService rssService;

    @PostMapping("/v1/magnet/find")
    public Object findMagnetLink(@RequestBody RssSearchDto dto) {
        String baseUrl = rssService.updateBaseUrl();
        log.info("baseUrl: {}", baseUrl);

        return ResponseEntity.ok(baseUrl);
    }

    @GetMapping("/v1/magnet/schedule")
    public Object findSchedule() {
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

        return ResponseEntity.ok();
    }
}
