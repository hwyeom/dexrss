package com.yeom.dexrss.rss.service;

import com.yeom.dexrss.rss.dto.RssItemDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class RssItemServiceTest {

    @Autowired
    private RssItemService rssItemService;

    @Test
    void getRssItems() {
        List<RssItemDto> drama = rssItemService.findByCategoryAndLimit("drama");

        log.info("dramas: {}", drama);
    }
}