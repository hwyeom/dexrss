package com.yeom.dexrss.rss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MagnetLinkPollingService {
    private final RestTemplate restTemplate;

    // URL 이 정상적인지 확인해야된다.
    public Optional<String> checkValidUrl(String baseUrl, int startIndex) {
        int index = startIndex;
        while (index < 1000) { // 원하는 범위를 설정
            String url = String.format(baseUrl, index);
            try {
                log.info("Checking URL: {}", url);
                ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
                HttpStatusCode statusCode = result.getStatusCode();

                if (statusCode.is3xxRedirection()) {
                    return Optional.of(result.getHeaders().getLocation().toString()); // 리다이렉트된 URL 반환
                } else if (statusCode.is2xxSuccessful()) {
                    return Optional.of(url); // 이미 정상 응답이라면 현재 URL 반환
                } else {
                    log.error("Unexpected response: {}", statusCode);
                    return Optional.empty();
                }
            } catch (HttpClientErrorException e) {
                // 404 같은 에러를 만나면 무시하고 다음 URL로
                if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                    log.warn("Error checking URL: {} - {}", url, e.getMessage());
                }
            } catch (Exception e) {
                log.warn("Unexpected error checking URL: {} - {}", url, e.getMessage());
            }
            index++;
        }

        return Optional.empty(); // 유효한 URL을 찾지 못함
    }

    // 토렌트 사이트 특정 게시판에서 업로드 된 글 링크정보들을 추출한다
    public Map<String, String> findItemLinks(String baseUrl, String path, String tagSelector) {
        // https://torrentrj182.com/v-2-13
        // 입력된 url 에서 html 바디 추출
        ResponseEntity<String> forEntity = restTemplate.getForEntity(baseUrl + path, String.class);
        // ul.page-list > li.topic-item > div.flex-grow > a
        // 응답 받은 body html 에서 특정 셀렉터에 해당하는 타이틀 , link 정보 추출
        return extractLink(baseUrl, forEntity.getBody(), tagSelector);
    }

    // HTML 태그 정보 추출
    private Map<String, String> extractLink(String baseUrl, String html, String tagSelector) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            Document document = Jsoup.parse(html); // HTML을 파싱하여 Document 객체 생성
            Elements elements = document.select(tagSelector); // 특정 태그 선택

            log.info("Found {} - elements matching selector: {}", elements.size(), tagSelector);
            for (Element element : elements) {
//                log.info("Tag: {}", element.tagName());
//                log.info("Text: {}", element.text());
//                log.info("Attributes: {}", element.attributes());
//                log.info("-------------------------");
                result.put(element.text(), baseUrl + element.attributes().get("href"));
            }
        } catch (Exception e) {
            log.error("Error parsing HTML: {}", e.getMessage());
        }

        return result;
    }

    // 특정 글 html 을 추출 후 마그넷 링크정보를 가져온다
    // `href` 속성에 `magnet:?`가 포함된 태그 추출
    public Optional<String> findMagnetLink(String url, String tagSelector) {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
//        log.info("Found {}", forEntity.getBody());

        try {
            Document document = Jsoup.parse(forEntity.getBody()); // HTML을 파싱하여 Document 객체 생성
            Elements magnetLinks = document.select(tagSelector); // `href`에 `magnet:?` 포함된 `a` 태그 선택

            log.info("Found " + magnetLinks.size() + " magnet links:");
            return magnetLinks.stream()
                    .filter(x -> x.hasAttr("href") && x.attr("href").contains("magnet:"))
                    .map(x -> x.attr("href"))
                    .findFirst();

        } catch (Exception e) {
            log.error("Error parsing HTML: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
