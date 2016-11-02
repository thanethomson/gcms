package com.thanethomson.gcms.config

import com.thanethomson.gcms.storage.InMemoryStorageEngine
import com.thanethomson.gcms.storage.StorageEngine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
open class TestStorageConfig {

    @Bean
    open fun storageEngine(): StorageEngine = InMemoryStorageEngine()

}
