package io.codifica.playground.springr2dbcjsontransformation.product;

import io.codifica.playground.springr2dbcjsontransformation.config.ContainerizedReactivePostgresTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ContainerizedReactivePostgresTestConfiguration.class})
@EnableAutoConfiguration(exclude = {ConnectionFactoryAutoConfiguration.class})
public class ProductTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testCreateProduct() {
        Product product = Product.builder()
                .name("New Product")
                .ownerId(32L)
                .parameters(Collections.singletonMap("someProperty", "someValue"))
                .build();

        productRepository.save(product)
                .as(StepVerifier::create)
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    public void testCreateAndReadProduct() {
        Product product = Product.builder()
                .name("New Product")
                .ownerId(32L)
                .parameters(Collections.singletonMap("someProperty", "someValue"))
                .build();

        productRepository.save(product)
                .as(StepVerifier::create)
                .expectNextCount(1L)
                .verifyComplete();

        productRepository.findAll()
                .as(StepVerifier::create)
                .expectNextMatches(prod -> {
                    log.debug("Product is {}", prod);
                    return prod.getParameters().get("someProperty").equals("someValue");
                }).expectComplete()
                .verify(Duration.ofSeconds(2));

    }

}
