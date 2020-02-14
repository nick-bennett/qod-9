package edu.cnm.deepdive.qod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class QuoteOfTheDayApplication {

  public static void main(String[] args) {
    SpringApplication.run(QuoteOfTheDayApplication.class, args);
  }

}
