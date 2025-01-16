package com.yeom.dexrss.rss.service;

import com.yeom.dexrss.rss.dto.RssItemDto;
import com.yeom.dexrss.rss.dto.RssSearchDto;
import com.yeom.dexrss.rss.entity.RssItem;
import com.yeom.dexrss.rss.property.RssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yeom.dexrss.common.global.GlobalVariable.TORRENT_RG_IDX_KEY;
import static com.yeom.dexrss.common.global.GlobalVariable.TORRENT_RG_URL_FORMAT_KEY;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Service
@Slf4j
public class RssService {
    private final MagnetLinkPollingService magnetLinkPollingService;
    private final RssProperties rssProperties;
    private final RssItemService rssItemService;
    private final RssSettingService rssSettingService;

    // URL 포맷과 index 값으로 BaseURL 정보를 찾고 DB에 업데이트 한다.
    public String updateBaseUrl() {
        Optional<String> opUrlFormat = rssSettingService.findByKey(TORRENT_RG_URL_FORMAT_KEY);
        if (opUrlFormat.isEmpty()) throw new RuntimeException("NOT FOUND: " + TORRENT_RG_URL_FORMAT_KEY);
        String urlFormat = opUrlFormat.get();

        Optional<String> opIndex = rssSettingService.findByKey(TORRENT_RG_IDX_KEY);
        if (opIndex.isEmpty()) throw new RuntimeException("NOT FOUND: " + TORRENT_RG_IDX_KEY);
        int index = Integer.parseInt(opIndex.get());

        Optional<String> r = magnetLinkPollingService.checkValidUrl(urlFormat, index);
        if (r.isPresent()) {
            String findUrl = r.get();
            // 인덱스가 다르다면 DB 업데이트 해줘야 함
            if (!findUrl.equalsIgnoreCase(String.format(urlFormat, index))) {
                //  TODO 변경된 인덱스 찾기
                // 정규식을 통해 숫자 추출
                String regex = "https://torrentrj(\\d+)\\.com";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(findUrl);
                if (matcher.find()) {
                    String extractedNumber = matcher.group(1); // 첫 번째 그룹에서 추출
                    int newIndex = Integer.parseInt(extractedNumber);
                    log.info("추출된 index: {}", newIndex);
                    if (index != newIndex) {
                        // 인덱스 업데이트
                        rssSettingService.updateTorrentRGIndex(extractedNumber);
                    }
                } else {
                    log.warn("URL 에서 index 를 추출할 수 없습니다.");
                }
            }
            return findUrl;
        } else {
            throw new RuntimeException("Find Base Rss URL Not Found");
        }
    }

    public void findAndUpdateRssItem(String baseUrl, String category) {
        // 카테고리 타입에 맞는 uri 찾기
        String uriPath = switch (category) {
            case "drama" -> rssProperties.getDramaPath();
            case "ent" -> rssProperties.getEntPath();
            default -> throw new RuntimeException("Invalid category");
        };
        Random random = new Random();

        // DB에 저장된 항목 리스트와 비교 후 없는 부분만 마그넷 링크를 조회 하자
        // DB에 저장된 항목 리스트 조회.. 조회 기간은 일주일정도?
        List<RssItem> dbList = rssItemService.findByCategoryAndCreatedAt(category, LocalDateTime.now().minusDays(7));

        // 신규 리스트 항목 변수
        List<RssItemDto> items = new ArrayList<>();
        // 토렌트 사이트 게시판 본문 읽어오기
        String siteBodyHtml = magnetLinkPollingService.getSiteBodyHtml(baseUrl, uriPath);
        // 게시판 글 li 정보 리스트 추출
        Elements liElementList = magnetLinkPollingService.extractBySelector(siteBodyHtml, "ul.page-list > li.topic-item");
        for (Element liElement : liElementList) {
            String boardId = liElement.select("div").get(0).text();
            String title = liElement.select("div.flex-grow > a").first().text();
            String link = liElement.select("div.flex-grow > a").first().attributes().get("href");
            log.info("boardId: {} link: {}", boardId, link);

            // 링크가 DB에 있는 값인지 비교
            if(dbList.stream().anyMatch(x -> x.getBoardId() == Long.parseLong(boardId))){
                continue;
            }

            Optional<String> a = magnetLinkPollingService.findMagnetLink(baseUrl + link, "a");
            List<RssItemDto> magnetLinkItemList = magnetLinkPollingService.findMagnetLink2(baseUrl + link);
            magnetLinkItemList.forEach(x -> {
                if (!hasText(x.getTitle())) x.setTitle(title);
                x.setPage(link);
                x.setCategory(category);
                x.setBoardId(Long.parseLong(boardId));
            });
            items.addAll(magnetLinkItemList);
            // 웹 조회 간격을 너무 빠르지 않게 딜레이를 주자
            try {
                Thread.sleep(random.nextInt(3000) + 1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }

        log.info("추출된 신규 건: {}", items);
        // DB 에 업데이트
        if (!items.isEmpty()) {
            rssItemService.saveDto(items);
        }
    }

    public Object findMagnetLink(RssSearchDto dto) {
//        String url = "https://torrentrj%d.com";
//        String path = "/v-2-13";
        // RSS 사이트 베이스 URL 검색
        int index = 182;
        Optional<String> r = magnetLinkPollingService.checkValidUrl(dto.getUrl(), index);

        switch (dto.getCategory()) {
            case "drama":
                dto.setPath(rssProperties.getDramaPath());
                break;
            case "ent":
                dto.setPath(rssProperties.getEntPath());
                break;
        }

        String itemSelector = "ul.page-list > li.topic-item > div.flex-grow > a";

        List<RssItemDto> items = new ArrayList<>();
        if (r.isPresent()) {
            Map<String, String> itemLinks = magnetLinkPollingService.findItemLinks(r.get(), dto.getPath(), itemSelector);

            itemLinks.keySet().forEach(key -> {
                String link = itemLinks.get(key);
                // div.box_content.p-0 > div:nth-child(6) > div > a.ml-2.p-2.border.text-16px
                Optional<String> a = magnetLinkPollingService.findMagnetLink(link, "a");
                a.ifPresent(x -> items.add(RssItemDto.builder().title(key).link(x).page(link).category(dto.getCategory()).build()));
            });
            log.info("items: {}", items);
        }
        return ResponseEntity.ok(items);
    }
}
