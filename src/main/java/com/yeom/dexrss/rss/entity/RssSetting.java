package com.yeom.dexrss.rss.entity;

import com.yeom.dexrss.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "rss_setting")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RssSetting extends BaseEntity {
    @Id
    private String key;
    @Setter
    private String value;
}
