package com.yeom.dexrss.rss.property;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class RssProperties {

    @Value("${rss.drama.path}")
    private String dramaPath;

    @Value("${rss.ent.path}")
    private String entPath;
}
