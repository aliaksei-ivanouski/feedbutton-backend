package com.fetocan.feedbutton.service.user

import com.fetocan.feedbutton.service.util.RemoteAddressResolver
import com.vladmihalcea.hibernate.type.basic.Inet
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class UserService(
    val userRepository: UserRepository,
    private val dsl: DSLContext
): UserRepository by userRepository {

    fun userCheckedIn(user: User) {
        user.lastCheckInAt = Instant.now()
        user.lastUsedIp = RemoteAddressResolver.resolveAddressFromRequest()?.let { Inet(it.hostAddress) }
        save(user)
    }
}
