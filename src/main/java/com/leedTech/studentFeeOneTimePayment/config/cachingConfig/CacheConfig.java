package com.leedTech.studentFeeOneTimePayment.config.cachingConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {
			
			@Value("${spring.cache.ttl-minutes}")
			private long ttlMinutes;
			
			public static final String USERS_CACHE        = "users";
			public static final String USER_ROLES_CACHE   = "userRoles";
			public static final String PAYMENTS_CACHE     = "payments";
			public static final String STUDENTS_CACHE     = "students";
			public static final String FEE_CACHE          = "fees";
			
			@Bean
			public RedisTemplate<String, Object> redisTemplate(
					RedisConnectionFactory connectionFactory
			) {
				RedisTemplate<String, Object> template = new RedisTemplate<>();
				template.setConnectionFactory(connectionFactory);
				template.setKeySerializer(new StringRedisSerializer());
				template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
				template.setHashKeySerializer(new StringRedisSerializer());
				template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
				template.afterPropertiesSet();
				return template;
			}
			
			private RedisCacheConfiguration defaultCacheConfig() {
				return RedisCacheConfiguration.defaultCacheConfig()
						       .entryTtl(Duration.ofMinutes(ttlMinutes))
						       .disableCachingNullValues()
						       .serializeKeysWith(
								       RedisSerializationContext.SerializationPair
										       .fromSerializer(new StringRedisSerializer())
						       )
						       .serializeValuesWith(
								       RedisSerializationContext.SerializationPair
										       .fromSerializer(new GenericJackson2JsonRedisSerializer())
						       );
			}
			
			@Bean
			public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
				
				Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
				
				cacheConfigurations.put(USERS_CACHE,
						defaultCacheConfig().entryTtl(Duration.ofMinutes(30))
				);
				cacheConfigurations.put(USER_ROLES_CACHE,
						defaultCacheConfig().entryTtl(Duration.ofHours(2))
				);
				cacheConfigurations.put(PAYMENTS_CACHE,
						defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
				);
				cacheConfigurations.put(STUDENTS_CACHE,
						defaultCacheConfig().entryTtl(Duration.ofMinutes(60))
				);
				cacheConfigurations.put(FEE_CACHE,
						defaultCacheConfig().entryTtl(Duration.ofHours(1))
				);
				
				return RedisCacheManager.builder(connectionFactory)
						       .cacheDefaults(defaultCacheConfig())
						       .withInitialCacheConfigurations(cacheConfigurations)
						       .build();
			}
}