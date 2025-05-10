package com.bookstore;

import com.bookstore.exception.AuthorNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    private static Map<Integer, Author> authorStore = new HashMap<>();
    private static int idCounter = 1;

    @GET
    public Response getAllAuthors() {
        return Response.ok(new ArrayList<>(authorStore.values())).build();
    }

    @GET
    @Path("/{id}")
    public Response getAuthorById(@PathParam("id") int id) {
        Author author = authorStore.get(id);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        return Response.ok(author).build();
    }

    @POST
    public Response addAuthor(Author author) {
        
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Author Data",
                        "message", "Author name must not be empty."
                    ))
                    .build());
        }

        author.setId(idCounter++);
        authorStore.put(author.getId(), author);
        return Response.status(Response.Status.CREATED).entity(author).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") int id, Author updatedAuthor) {
        Author existingAuthor = authorStore.get(id);
        if (existingAuthor == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }

        // ✅ Validation during update also
        if (updatedAuthor.getName() == null || updatedAuthor.getName().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Author Data",
                        "message", "Author name must not be empty during update."
                    ))
                    .build());
        }

        updatedAuthor.setId(id);
        authorStore.put(id, updatedAuthor);
        return Response.ok(updatedAuthor).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") int id) {
        Author removedAuthor = authorStore.remove(id);
        if (removedAuthor == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/books")
    public Response getBooksByAuthor(@PathParam("id") int authorId) {
        Author author = authorStore.get(authorId);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + authorId + " does not exist.");
        }

        List<Book> booksByAuthor = new ArrayList<>();
        for (Book book : BookResource.getBookStore().values()) {
            if (book.getAuthorId() == authorId) {
                booksByAuthor.add(book);
            }
        }
        return Response.ok(booksByAuthor).build();
    }

    public static Map<Integer, Author> getAuthorStore() {
        return authorStore;
    }
}