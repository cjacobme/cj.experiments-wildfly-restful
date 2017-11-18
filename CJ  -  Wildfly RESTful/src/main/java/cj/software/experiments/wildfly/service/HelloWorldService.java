package cj.software.experiments.wildfly.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class HelloWorldService
{
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response helloText()
	{
		Response lResult = Response.ok("Hello World from Wildfly Swarm as text").build();
		return lResult;
	}
}
