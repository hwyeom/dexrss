package com.yeom.dexrss.rss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RssItemDto {
    private String title;
    private String link;
    // 웹 링크
    private String page;
    // tsharp 카테고리
    private String category;

    // 보드 아이디
    private long boardId;
}
