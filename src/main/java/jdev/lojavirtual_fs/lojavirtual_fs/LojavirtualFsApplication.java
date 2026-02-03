package jdev.lojavirtual_fs.lojavirtual_fs;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EntityScan(basePackages = "jdev.lojavirtual_fs.lojavirtual_fs.model")
@ComponentScan(basePackages = {"jdev.*"})//.*
@EnableJpaRepositories(basePackages = {"jdev.lojavirtual_fs.lojavirtual_fs.repository"})
@EnableTransactionManagement
public class LojavirtualFsApplication implements WebMvcConfigurer {
	public static void main(String[] args) {

		SpringApplication.run(LojavirtualFsApplication.class, args);
	}

	// Gestão de E-mail
	@Bean
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Assincrono-Thread-");
		executor.initialize();
		return executor;
	}

	// CORS Configuration for Java 17/Spring Boot 2.7+/3.x
	@Override
	public void addCorsMappings(CorsRegistry registry) {

		registry.addMapping("/**")
				.allowedOriginPatterns("*")  // Mudou de allowedOrigins para allowedOriginPatterns
				.allowedHeaders("*")
				.allowedMethods("*")
				.exposedHeaders("*")
				.allowCredentials(false); // Se for usar true, especifique as origens

		// WebMvcConfigurer.super.addCorsMappings(registry); // Esta linha não é mais necessária
	}
}
