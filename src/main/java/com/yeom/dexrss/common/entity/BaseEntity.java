package com.yeom.dexrss.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass   // 다른 엔티티들이 상속받을수 있도록 설정
@EntityListeners(AuditingEntityListener.class)  // 엔티티 리스너 추가
@Getter
public class BaseEntity {
    @Column(updatable = false) // 등록 시간은 수정안되도록 설정
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // 엔티티가 persist 될 때 createdAt 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 엔티티가 업데이트 될 때 updatedAt 설정
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
