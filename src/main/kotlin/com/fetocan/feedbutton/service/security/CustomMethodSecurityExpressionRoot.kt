package com.fetocan.feedbutton.service.security

import com.fetocan.feedbutton.service.manager.ManagerService
import com.fetocan.feedbutton.service.security.authorization.Auth
import org.springframework.security.access.expression.SecurityExpressionRoot
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
import org.springframework.security.core.Authentication

class CustomMethodSecurityExpressionRoot(
    private val managerAccountService: ManagerService,
    authentication: Authentication
) : SecurityExpressionRoot(authentication), MethodSecurityExpressionOperations {

    private var filterObject: Any? = null
    private var returnObject: Any? = null
    private var target: Any? = null

    override fun setFilterObject(filterObject: Any?) {
        this.filterObject = filterObject
    }

    override fun getFilterObject(): Any? {
        return filterObject
    }

    override fun setReturnObject(returnObject: Any?) {
        this.returnObject = returnObject
    }

    override fun getReturnObject(): Any? {
        return this.returnObject
    }

    fun setThis(target: Any?) {
        this.target = target
    }

    override fun getThis(): Any? {
        return target
    }

    fun isMe(principalId: Long) = Auth.isMe(principalId, authentication)

    fun isAdmin() = Auth.isAdmin(authentication)
    fun isManager() = Auth.isManager(authentication)
    fun isUser() = Auth.isUser(authentication)

    fun hasAccess(venueId: Long, resourceType: String, permission: String) =
        Auth.hasAccess(venueId, resourceType, permission, authentication)

    fun hasOrgAccess(orgId: Long, resourceType: String, permission: String) =
        Auth.hasOrgAccess(orgId, resourceType, permission, authentication)

}
