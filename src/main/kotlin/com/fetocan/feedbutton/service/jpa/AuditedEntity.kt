package com.fetocan.feedbutton.service.jpa

import java.io.Serializable
import java.time.Instant
import javax.persistence.MappedSuperclass
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

@MappedSuperclass
open class AuditedEntity: Serializable {

    var createdAt: Instant? = null

    var updatedAt: Instant? = null

    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now()
        }
        updatedAt = Instant.now()
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }
}
