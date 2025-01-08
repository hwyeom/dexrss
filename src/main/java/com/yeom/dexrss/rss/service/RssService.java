package com.yeom.dexrss.rss.service;

import com.yeom.dexrss.rss.dto.RssItem;
import com.yeom.dexrss.rss.dto.RssSearchDto;
import com.yeom.dexrss.rss.property.RssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class RssService {
    private final MagnetLinkPollingService magnetLinkPollingService;
    private final RssProperties rssProperties;

    public Object findMagnetLink(RssSearchDto dto) {
//        String url = "https://torrentrj%d.com";
//        String path = "/v-2-13";
        switch (dto.getCategory()) {
            case "drama":
                dto.setPath(rssProperties.getDramaPath());
                break;
            case "ent":
                dto.setPath(rssProperties.getEntPath());
                break;
        }

        String itemSelector = "ul.page-list > li.topic-item > div.flex-grow > a";
        int index = 182;
        Optional<String> r = magnetLinkPollingService.checkValidUrl(dto.getUrl(), index);

        List<RssItem> items = new ArrayList<>();
        if (r.isPresent()) {
            Map<String, String> itemLinks = magnetLinkPollingService.findItemLinks(r.get(), dto.getPath(), itemSelector);

            itemLinks.keySet().forEach(key -> {
                String link = itemLinks.get(key);
                // div.box_content.p-0 > div:nth-child(6) > div > a.ml-2.p-2.border.text-16px
                Optional<String> a = magnetLinkPollingService.findMagnetLink(link, "a");
                a.ifPresent(x -> items.add(RssItem.builder().title(key).link(x).page(link).category(dto.getCategory()).build()));
            });
            log.info("items: {}", items);
        }
        return ResponseEntity.ok(items);
    }
}
