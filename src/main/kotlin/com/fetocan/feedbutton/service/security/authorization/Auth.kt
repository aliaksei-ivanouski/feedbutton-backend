package com.fetocan.feedbutton.service.security.authorization

import com.fetocan.feedbutton.service.exception.ErrorCodes.MANAGER_NOT_FOUND
import com.fetocan.feedbutton.service.exception.NotFoundException
import com.fetocan.feedbutton.service.manager.AccessScopesAware
import com.fetocan.feedbutton.service.manager.ManagerService
import com.fetocan.feedbutton.service.security.authentication.manager.ManagerClaims
import com.fetocan.feedbutton.service.venue.VenueService
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
object Auth : ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    private val managerService: ManagerService
        get() = applicationContext.getBean(ManagerService::class.java)

    private val venueService: VenueService
        get() = applicationContext.getBean(VenueService::class.java)

    private fun securityContextAuth() = SecurityContextHolder.getContext().authentication

    fun isAuthenticated(authentication: Authentication = securityContextAuth()): Boolean {
        return authentication.isAuthenticated
    }

    fun isMe(principalId: UUID, authentication: Authentication = securityContextAuth()): Boolean =
        isAuthenticated(authentication) && when (val principal = authentication.principal) {
//            is AppUserClaims -> principal.id == principalId
            is ManagerClaims -> principal.id == principalId
            else -> false
        }

    fun isAdmin(authentication: Authentication = securityContextAuth()) = hasRole("ADMIN", authentication)
    fun isManager(authentication: Authentication = securityContextAuth()) = hasRole("MANAGER", authentication)
    fun isUser(authentication: Authentication = securityContextAuth()) = hasRole("USER", authentication)

    fun hasRole(role: String, authentication: Authentication = securityContextAuth()) =
        hasAuthority("ROLE_$role", authentication)

    fun hasAccess(
        venueId: UUID,
        resourceType: String,
        permission: String,
        authentication: Authentication = securityContextAuth()
    ): Boolean {
        if (!isAuthenticated(authentication))
            return false

        if (isAdmin(authentication)) return true

        val principal = authentication.principal as? ManagerClaims
            ?: return false

        val accessMapping: AccessScopesAware? = managerService.getVenueMapping(principal.id, venueId)

        if (accessMapping == null) {
            val venue = venueService.findByIdOrNull(venueId)
                ?: throw NotFoundException(MANAGER_NOT_FOUND, "venue with id: $venueId not found")

//            if (venue.orgId != null) {
//                return hasOrgAccess(venue.orgId, resourceType, permission)
//            }
            return false
        }

        return hasScopeAccess(resourceType, permission, accessMapping.accessScopes)
    }

    fun hasOrgAccess(
        orgId: UUID,
        resourceType: String,
        permission: String,
        authentication: Authentication = securityContextAuth()
    ): Boolean {
        if (!isAuthenticated(authentication))
            return false

        if (isAdmin(authentication)) return true

        val principal = authentication.principal as? ManagerClaims
            ?: return false

        return false
//        val accessMapping: AccessScopesAware = managerService.getOrgMapping(principal.id, orgId)
//            ?: return false
//
//        return hasScopeAccess(resourceType, permission, accessMapping.accessScopes)
    }

    private fun hasScopeAccess(resourceType: String, permission: String, accessScopes: Array<String>) =
        accessScopes.any {
            val authority = it
            val delimiterIndex = authority.indexOf(":")
            if (delimiterIndex == -1) {
                false
            } else {
                val aResourceType = authority.substring(0, delimiterIndex)
                val aPermission = authority.substring(delimiterIndex + 1)
                (aResourceType == resourceType || aResourceType == "*")
                    && (aPermission == permission || aPermission == "*")
            }
        }

    fun principalId(authentication: Authentication = securityContextAuth()): UUID? {
        if (!isAuthenticated(authentication))
            return null

        return when (val principal = authentication.principal) {
//            is AppUserClaims -> principal.id
            is ManagerClaims -> principal.id
            else -> null
        }
    }

    fun hasAuthority(authority: String, authentication: Authentication = securityContextAuth()) =
        authentication.authorities.any {
            it.authority == authority
        }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        Auth.applicationContext = applicationContext
    }

}
