package com.yeom.dexrss.rss.repository;

import com.yeom.dexrss.rss.entity.RssSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RssSettingRepository extends JpaRepository<RssSetting, String> {
}
