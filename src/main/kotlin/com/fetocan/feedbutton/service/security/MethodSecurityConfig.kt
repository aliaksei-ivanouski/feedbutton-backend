package com.fetocan.feedbutton.service.security

import com.fetocan.feedbutton.service.manager.ManagerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.DenyAllPermissionEvaluator
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class MethodSecurityConfig : GlobalMethodSecurityConfiguration() {

    @Autowired
    private lateinit var managerService: ManagerService

    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        val expressionHandler = CustomMethodSecurityExpressionHandler(managerService)
        expressionHandler.setPermissionEvaluator(DenyAllPermissionEvaluator())
        return expressionHandler
    }

}
