package com.yeom.dexrss.control;

import com.yeom.dexrss.rss.dto.RssSearchDto;
import com.yeom.dexrss.rss.service.RssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

        return ResponseEntity.ok();
    }
}
