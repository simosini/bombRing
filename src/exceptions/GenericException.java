package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericException extends Throwable implements ExceptionMapper<Throwable> {

	private static final long serialVersionUID = 1L;

	@Override
	public Response toResponse(Throwable ex) {

		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
	}

}
