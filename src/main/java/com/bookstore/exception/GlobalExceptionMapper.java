package com.bookstore.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        Map<String, String> error = new HashMap<>();
        int status = 400; // Default: Bad Request

        if (exception instanceof BookNotFoundException ||
            exception instanceof AuthorNotFoundException ||
            exception instanceof CustomerNotFoundException ||
            exception instanceof CartNotFoundException ||
            exception instanceof OrderNotFoundException) {
            status = 404; // Not Found
        }

        error.put("error", exception.getClass().getSimpleName().replace("Exception", "").replace("NotFound", " Not Found"));
        error.put("message", exception.getMessage());

        return Response.status(status).entity(error).build();
    }
}