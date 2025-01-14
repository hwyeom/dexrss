package com.yeom.dexrss.rss.service;

import com.yeom.dexrss.common.global.GlobalVariable;
import com.yeom.dexrss.rss.entity.RssSetting;
import com.yeom.dexrss.rss.repository.RssSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.yeom.dexrss.common.global.GlobalVariable.TORRENT_RG_IDX_KEY;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RssSettingService {
    private final RssSettingRepository rssSettingRepository;

    public Optional<String> findByKey(String key) {
        Optional<RssSetting> find = rssSettingRepository.findById(key);
        return find.map(RssSetting::getValue);
    }

    // 인덱스 값 받아서 토렌트알지 인덱스값 변경하기
    @Transactional
    public void updateTorrentRGIndex(String extractedNumber) {
        Optional<RssSetting> findEntity = rssSettingRepository.findById(TORRENT_RG_IDX_KEY);
        if (findEntity.isPresent()) {
            RssSetting rssSetting = findEntity.get();
            rssSetting.setValue(extractedNumber);
            rssSettingRepository.saveAndFlush(rssSetting);
        }
    }
}
