package com.yeom.dexrss.control;

import com.yeom.dexrss.rss.dto.RssItemDto;
import com.yeom.dexrss.rss.dto.RssSearchDto;
import com.yeom.dexrss.rss.service.RssItemService;
import com.yeom.dexrss.rss.service.RssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rss")
public class RssController {
    private final RssService rssService;
    private final RssItemService rssItemService;

    @PostMapping("/v1/magnet/find")
    public Object findMagnetLink(@RequestBody RssSearchDto dto) {
        log.info("RssSearchDto: {}", dto);
        return ResponseEntity.ok(rssService.findMagnetLink(dto));
    }

    @PostMapping("/v2/magnet/find")
    public Object findMagnetLinkV2(@RequestBody RssSearchDto dto) {
        log.info("search Type: {}", dto.getCategory());
        List<RssItemDto> findRssItems = rssItemService.findByCategoryAndLimit(dto.getCategory());
        log.info("find Rss items: {}", findRssItems);
        return ResponseEntity.ok(findRssItems);
    }
}
