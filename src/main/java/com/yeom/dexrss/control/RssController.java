package com.yeom.dexrss.control;

import com.yeom.dexrss.rss.dto.RssSearchDto;
import com.yeom.dexrss.rss.service.RssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rss")
public class RssController {
    private final RssService rssService;

    @PostMapping("/v1/magnet/find")
    public Object findMagnetLink(@RequestBody RssSearchDto dto) {
        log.info("RssSearchDto: {}", dto);
        return ResponseEntity.ok(rssService.findMagnetLink(dto));
    }
}
