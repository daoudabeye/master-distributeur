/*
 * Copyright 2016-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ml.iks.md;

import ml.iks.md.events.AppCallbackManager;
import ml.iks.md.events.listeners.InscriptionListener;
import ml.iks.md.events.listeners.PaymentListener;
import ml.iks.md.model.Car;
import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.iks.md.util.Utils;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * @author rmpestano
 */
@SpringBootApplication
public class AdminBootApplication extends SpringBootServletInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootApplication.class);

	@Inject
	private Utils utils;

    @Bean
    public List<Car> getCars() {
        return utils.getCars();
    }

    @Override
    public void run(String... args) throws Exception {
        startGateway();
    }

    private void startGateway() {
        log.error("Starting gateway sevice");
        try {
            BeanLocator.find(GatewayService.class).startup();
            BeanLocator.find(AppCallbackManager.class).start();

            BeanLocator.find(AppCallbackManager.class).setPaymentMessageCallback(new PaymentListener());
            BeanLocator.find(AppCallbackManager.class).setInscriptionCallback(new InscriptionListener());
        } catch (IllegalArgumentException e) {
            log.info("Illegal / incorrect arguments!");
        } catch (Exception e) {
            log.error("Unhandled exception!", e);
            System.exit(1);
        }
    }

    @PreDestroy
    public void stop() {
        try {
            BeanLocator.find(GatewayService.class).shutdown();
            BeanLocator.find(AppCallbackManager.class).stop();
        } catch (Exception e) {
            log.error("App did not stop properly ", e);
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }


}
