package com.fetocan.feedbutton.service.manager

import com.fetocan.feedbutton.service.cache.CacheConfig.Companion.MANAGER_CACHE_NAME
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ManagerRepository: JpaRepository<Manager, UUID> {
    @Cacheable(
        cacheNames = [MANAGER_CACHE_NAME],
        key = "#id"
    )
    override fun findById(id: UUID): Optional<Manager>

    fun findByEmailIgnoreCase(email: String): Manager?

    @CacheEvict(
        cacheNames = [MANAGER_CACHE_NAME],
        key = "#manager.id"
    )
    override fun <S : Manager> save(manager: S): S
}
