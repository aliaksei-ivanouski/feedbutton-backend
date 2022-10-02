package com.fetocan.feedbutton.service.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun <T : Any> findById(id: UUID, type: Class<T>): T?
    fun findByEmailIgnoreCase(email: String): User?
}
