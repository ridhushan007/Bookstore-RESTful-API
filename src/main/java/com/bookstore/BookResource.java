package com.bookstore;

import com.bookstore.exception.BookNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    private static Map<Integer, Book> bookStore = new HashMap<>();
    private static int idCounter = 1;

    @GET
    public Response getAllBooks() {
        return Response.ok(new ArrayList<>(bookStore.values())).build();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") int id) {
        Book book = bookStore.get(id);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        return Response.ok(book).build();
    }

    @POST
    public Response addBook(Book book) {
        // ✅ Validation before creating book
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Book Data",
                        "message", "Book title must not be empty."
                    ))
                    .build());
        }

        if (book.getPrice() <= 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Book Data",
                        "message", "Book price must be greater than 0."
                    ))
                    .build());
        }

        if (book.getStock() < 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Book Data",
                        "message", "Book stock cannot be negative."
                    ))
                    .build());
        }

        book.setId(idCounter++);
        bookStore.put(book.getId(), book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") int id, Book updatedBook) {
        Book existingBook = bookStore.get(id);
        if (existingBook == null) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }

        
        if (updatedBook.getTitle() == null || updatedBook.getTitle().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Book Data",
                        "message", "Book title must not be empty during update."
                    ))
                    .build());
        }

        if (updatedBook.getPrice() <= 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Book Data",
                        "message", "Book price must be greater than 0 during update."
                    ))
                    .build());
        }

        if (updatedBook.getStock() < 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Book Data",
                        "message", "Book stock cannot be negative during update."
                    ))
                    .build());
        }

        updatedBook.setId(id);
        bookStore.put(id, updatedBook);
        return Response.ok(updatedBook).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") int id) {
        Book removedBook = bookStore.remove(id);
        if (removedBook == null) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        return Response.noContent().build();
    }

    public static Map<Integer, Book> getBookStore() {
        return bookStore;
    }
}