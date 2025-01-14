package com.yeom.dexrss.schedule;

import com.yeom.dexrss.rss.service.RssService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Rollback(value = false)
class RssSchedulerTest {

    @Autowired
    private RssService rssService;

    @Autowired
    private RssScheduler rssScheduler;

    @Test
    @Commit
    void schedule() {
        rssScheduler.executeTask();
    }
}