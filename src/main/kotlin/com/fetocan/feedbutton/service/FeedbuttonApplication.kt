package com.fetocan.feedbutton.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class FeedbuttonApplication

fun main(args: Array<String>) {
	runApplication<FeedbuttonApplication>(*args)
}
