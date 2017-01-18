/*
 * Copyright 2017 the original author or authors.
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
package org.springframework.data.redis.connection.jedis;

import static org.assertj.core.api.Assertions.*;

import redis.clients.jedis.JedisPoolConfig;
import sun.net.www.protocol.https.DefaultHostnameVerifier;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import org.junit.Test;

/**
 * Unit tests for {@link JedisClientConfiguration}.
 *
 * @author Mark Paluch
 */
public class JedisClientConfigurationUnitTests {

	@Test // DATAREDIS-574
	public void shouldCreateEmptyConfiguration() {

		JedisClientConfiguration configuration = JedisClientConfiguration.create();

		assertThat(configuration.getClientName()).isEmpty();
		assertThat(configuration.getConnectTimeout()).isEqualTo(Duration.of(2, ChronoUnit.SECONDS));
		assertThat(configuration.getReadTimeout()).isEqualTo(Duration.of(2, ChronoUnit.SECONDS));
		assertThat(configuration.getHostnameVerifier()).isEmpty();
		assertThat(configuration.getPoolConfig()).isPresent();
		assertThat(configuration.getSslParameters()).isEmpty();
		assertThat(configuration.getSslSocketFactory()).isEmpty();
	}

	@Test // DATAREDIS-574
	public void shouldConfigureAllProperties() throws NoSuchAlgorithmException {

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		SSLParameters sslParameters = new SSLParameters();
		SSLContext context = SSLContext.getDefault();
		SSLSocketFactory socketFactory = context.getSocketFactory();

		JedisClientConfiguration configuration = JedisClientConfiguration.builder().clientName("my-client") //
				.connectTimeout(Duration.of(10, ChronoUnit.MINUTES)) //
				.readTimeout(Duration.of(5, ChronoUnit.DAYS)) //
				.useSsl() //
				.hostnameVerifier(new DefaultHostnameVerifier()) //
				.sslParameters(sslParameters) //
				.sslSocketFactory(socketFactory) //
				.and() //
				.usePooling().poolConfig(poolConfig) //
				.build();

		assertThat(configuration.getClientName()).contains("my-client");
		assertThat(configuration.getConnectTimeout()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
		assertThat(configuration.getReadTimeout()).isEqualTo(Duration.of(5, ChronoUnit.DAYS));
		assertThat(configuration.getHostnameVerifier()).isPresent();
		configuration.getHostnameVerifier().ifPresent(actual -> {
			assertThat(actual).isInstanceOf(DefaultHostnameVerifier.class);
		});

		assertThat(configuration.getPoolConfig()).contains(poolConfig);
		assertThat(configuration.getSslParameters()).contains(sslParameters);
		assertThat(configuration.getSslSocketFactory()).contains(socketFactory);
	}
}
