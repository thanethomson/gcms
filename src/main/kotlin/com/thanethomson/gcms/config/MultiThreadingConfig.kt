package com.thanethomson.gcms.config

import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors

@Configuration
open class MultiThreadingConfig {

    @Value("\${com.thanethomson.gcms.thread-pool-size}")
    var threadPoolSize: Int = 0

    @Bean
    open fun executorService(): ListeningExecutorService
        = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadPoolSize))

}