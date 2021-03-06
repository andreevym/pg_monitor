package io.github.tbk.pgmonitor;

import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class StaticServer extends AbstractVerticle {

    private AppConfiguration configuration;

    StaticServer(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        String webroot = getRoot();
        log.info("Using '{}' as static webroot", webroot);

        router.route().handler(StaticHandler.create(webroot));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(configuration.httpPort());
    }

    private String getRoot() {
        final String pathWhenInsideJarFile = "BOOT-INF/classes/webroot";
        boolean insideJarFile = new ClassPathResource(pathWhenInsideJarFile).exists();
        return insideJarFile ? pathWhenInsideJarFile : "webroot";
    }
}
