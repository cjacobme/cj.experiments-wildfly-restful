package cj.software.experiments.wildfly.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class SimpleTest
{
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(
			wireMockConfig().dynamicPort().dynamicHttpsPort());

	@Test
	public void apacheHttp() throws IOException
	{
		int lPort = this.wireMockRule.port();
		int lHttpsPort = this.wireMockRule.httpsPort();
		System.out.printf("http %d, https %d\r\n", lPort, lHttpsPort);

		stubFor(
				get(urlPathMatching("/rest/hello")).willReturn(
						aResponse()
								.withStatus(200)
								.withHeader("Content-Type", MediaType.TEXT_PLAIN)
								.withBody("Hallo Welt")));
		try (CloseableHttpClient lClient = HttpClients.createDefault())
		{
			String lURI = String.format("http://localhost:%d/%s", lPort, "rest/hello");
			HttpGet lRequest = new HttpGet(lURI);
			HttpResponse lResponse = lClient.execute(lRequest);
			HttpEntity lEntity = lResponse.getEntity();
			Header lHeader = lEntity.getContentType();
			assertThat(lHeader.getValue()).as("Content Type").isEqualTo("text/plain");
			String lContent = this.convertHttpResponseToString(lResponse);
			assertThat(lContent).as("Content").isEqualTo("Hallo Welt");
		}
	}

	@Test
	public void clientBuilder()
	{
		int lPort = this.wireMockRule.port();

		stubFor(
				get(urlPathMatching("/rest/hello")).willReturn(
						aResponse()
								.withStatus(200)
								.withHeader("Content-Type", MediaType.TEXT_PLAIN)
								.withBody("Hallo Welt")));
		Client lClient = ClientBuilder.newClient();
		String lURI = String.format("http://localhost:%d/%s", lPort, "rest/hello");
		Response lResponse = lClient.target(lURI).request().get();
		assertThat(lResponse).isNotNull();
		String lString = lResponse.readEntity(String.class);
		assertThat(lString).isEqualTo("Hallo Welt");
	}

	private String convertHttpResponseToString(HttpResponse pResponse)
			throws UnsupportedOperationException,
			IOException
	{
		try (InputStream lIS = pResponse.getEntity().getContent())
		{
			String lResult = this.convertInputStreamToString(lIS);
			return lResult;
		}
	}

	private String convertInputStreamToString(InputStream pIS)
	{
		try (Scanner lScanner = new Scanner(pIS, "UTF-8"))
		{
			String lResult = lScanner.useDelimiter("\\Z").next();
			return lResult;
		}
	}
}
