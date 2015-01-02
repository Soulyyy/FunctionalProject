package controllers;

import java.time.Instant;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import model.ResponseHolder;

import game.ScalaClass;

@Path("/terescala")
public class HelloScalaServlet {

	@Path("/join")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response joinGame(@QueryParam("name") String name) {
		Instant begin = Instant.now();

		ScalaClass sc = new ScalaClass();
		String data = sc.helloResponse(name);

		return Response.ok(new ResponseHolder(begin, data)).build();
	}
}
