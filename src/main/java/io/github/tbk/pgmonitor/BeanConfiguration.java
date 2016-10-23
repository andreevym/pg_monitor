package io.github.tbk.pgmonitor;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeanConfiguration {

    private AppConfiguration appConfig;

    @Autowired
    public BeanConfiguration(AppConfiguration appConfig) {
        this.appConfig = appConfig;
    }

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public StaticServer staticServer() {
        return new StaticServer(appConfig);
    }

}
