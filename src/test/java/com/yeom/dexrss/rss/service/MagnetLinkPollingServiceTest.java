package com.yeom.dexrss.rss.service;

import com.yeom.dexrss.rss.dto.RssItemDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.data.repository.util.ClassUtils.ifPresent;

@Slf4j
@SpringBootTest
class MagnetLinkPollingServiceTest {

    @Autowired
    private MagnetLinkPollingService magnetLinkPollingService;
    @Autowired
    private RestTemplate restTemplate;

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

    @Test
    public void testFindMagnetLink() {
        String url = "https://torrentrj184.com/v/281925";

        // body > div:nth-child(2) > div.container.mx-auto.max-w-7xl.flex.flex-col.lg\:flex-row.pb-4.mt-2.text-sm > div.flex-grow > div.w-full > div.mt-2 > div:nth-child(1)
        // 해당 페이지에서 추출된 마그넷 아이템 리스트
        List<RssItemDto> rssItemDtoList = new ArrayList<>();
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        try {
            Document document = Jsoup.parse(forEntity.getBody()); // HTML을 파싱하여 Document 객체 생성
            Elements divList = document.select("div.border"); // `href`에 `magnet:?` 포함된 `a` 태그 선택

            for (Element div : divList) {
                Elements a = div.select("a");
                // 마그넷 주소 검색
                Optional<String> opMagnet = a.stream()
                        .filter(x -> x.hasAttr("href") && x.attr("href").contains("magnet:"))
                        .map(x -> x.attr("href"))
                        .findFirst();
                // 마그넷 주소가 없으면 스킵
                if (opMagnet.isEmpty()) {
                    continue;
                }
                RssItemDto item = RssItemDto.builder()
                        .link(opMagnet.get())
                        .build();
                log.info("magnetLink: {}", opMagnet.get());
                // 해당 건 제목 검색
                div.select("h3.title").stream()
                        .map(Element::text)
                        .findFirst()
                        .ifPresent(item::setTitle);

                // RSS ITEM 객체 저장
                rssItemDtoList.add(item);
            }

        } catch (Exception e) {
            log.error("Error parsing HTML: {}", e.getMessage());
        }
    }
}