package com.fetocan.feedbutton.service.security

import com.fetocan.feedbutton.service.manager.ManagerService
import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
import org.springframework.security.core.Authentication

class CustomMethodSecurityExpressionHandler(
    private val managerService: ManagerService
) : DefaultMethodSecurityExpressionHandler() {

    override fun createSecurityExpressionRoot(
        authentication: Authentication,
        invocation: MethodInvocation?
    ): MethodSecurityExpressionOperations {
        val root = CustomMethodSecurityExpressionRoot(managerService, authentication)
        root.`this` = invocation?.`this`
        root.setPermissionEvaluator(permissionEvaluator)
        root.setTrustResolver(trustResolver)
        root.setRoleHierarchy(roleHierarchy)
        root.setDefaultRolePrefix(defaultRolePrefix)

        return root
    }

}
