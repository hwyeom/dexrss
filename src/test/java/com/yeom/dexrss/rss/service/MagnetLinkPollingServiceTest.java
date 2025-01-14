package com.yeom.dexrss.rss.service;

import com.yeom.dexrss.rss.dto.RssItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
class MagnetLinkPollingServiceTest {

    @Autowired
    private MagnetLinkPollingService magnetLinkPollingService;

    @Test
    public void test() {
        String url = "https://torrentrj%d.com";
        int index = 175;
        Optional<String> r = magnetLinkPollingService.checkValidUrl(url, index);

        System.out.println(r.get());
    }

    @Test
    public void findItem() {
        String url = "https://torrentrj%d.com";
        int index = 182;
        Optional<String> r = magnetLinkPollingService.checkValidUrl(url, index);

        if (r.isPresent()) {
            Map<String, String> itemLinks = magnetLinkPollingService.findItemLinks(r.get(), "/v-2-13", "ul.page-list > li.topic-item > div.flex-grow > a");

            List<RssItemDto> items = new ArrayList<>();

            itemLinks.keySet().forEach(key -> {
                String link = itemLinks.get(key);
                // body > div:nth-child(2) > div.container.mx-auto.max-w-7xl.flex.flex-col.lg\:flex-row.pb-4.mt-2.text-sm >
                // div.flex-grow > div.w-full > div.mt-2 > div.border.p-2 >
                // div.box_content.p-0 > div:nth-child(6) > div > a.ml-2.p-2.border.text-16px
                Optional<String> a = magnetLinkPollingService.findMagnetLink(link, "a");
                a.ifPresent(x -> items.add(RssItemDto.builder().title(key).link(x).page(link).category("").build()));
            });
            System.out.println(items);
        }
    }
}