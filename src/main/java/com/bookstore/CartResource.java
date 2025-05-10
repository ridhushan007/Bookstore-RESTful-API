package com.bookstore;

import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.CartNotFoundException;
import com.bookstore.exception.CustomerNotFoundException;
import com.bookstore.exception.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/customers/{customerId}/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    private static Map<Integer, List<CartItem>> cartStore = new HashMap<>();
    private static Map<Integer, Book> bookStore = BookResource.getBookStore();
    private static Map<Integer, Customer> customerStore = CustomerResource.getCustomerStore();

    @GET
    public Response viewCart(@PathParam("customerId") int customerId) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        List<CartItem> cart = cartStore.getOrDefault(customerId, new ArrayList<>());
        return Response.ok(cart).build();
    }

    @POST
    @Path("/items")
    public Response addItemToCart(@PathParam("customerId") int customerId, CartItem item) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        if (!bookStore.containsKey(item.getBookId())) {
            throw new BookNotFoundException("Book with ID " + item.getBookId() + " does not exist.");
        }

        List<CartItem> cart = cartStore.getOrDefault(customerId, new ArrayList<>());

        boolean found = false;
        for (CartItem ci : cart) {
            if (ci.getBookId() == item.getBookId()) {
                ci.setQuantity(ci.getQuantity() + item.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(item);
        }

        cartStore.put(customerId, cart);
        return Response.status(Response.Status.CREATED).entity(cart).build();
    }

    @PUT
    @Path("/items/{bookId}")
    public Response updateItemQuantity(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId, CartItem item) {
        List<CartItem> cart = cartStore.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }

        boolean found = false;
        for (CartItem ci : cart) {
            if (ci.getBookId() == bookId) {
                ci.setQuantity(item.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            throw new InvalidInputException("Book with ID " + bookId + " not found in customer's cart.");
        }

        return Response.ok(cart).build();
    }

    @DELETE
    @Path("/items/{bookId}")
    public Response removeItem(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId) {
        List<CartItem> cart = cartStore.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }

        boolean removed = cart.removeIf(ci -> ci.getBookId() == bookId);
        if (!removed) {
            throw new InvalidInputException("Book with ID " + bookId + " not found in customer's cart.");
        }

        return Response.noContent().build();
    }

    public static Map<Integer, List<CartItem>> getCartStore() {
        return cartStore;
    }
}