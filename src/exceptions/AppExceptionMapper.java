package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppException> {

	@Override
	public Response toResponse(AppException ex) {
		return Response.status(Status.NOT_FOUND).entity(ex.getMessage()).build();
	}

}
