package com.bookstore;

import com.bookstore.exception.CartNotFoundException;
import com.bookstore.exception.CustomerNotFoundException;
import com.bookstore.exception.OrderNotFoundException;
import com.bookstore.exception.OutOfStockException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/customers/{customerId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private static Map<Integer, List<Order>> orderStore = new HashMap<>();
    private static Map<Integer, Customer> customerStore = CustomerResource.getCustomerStore();
    private static Map<Integer, List<CartItem>> cartStore = CartResource.getCartStore();
    private static Map<Integer, Book> bookStore = BookResource.getBookStore();

    private static int orderIdCounter = 1;

    @POST
    public Response placeOrder(@PathParam("customerId") int customerId) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }

        List<CartItem> cart = cartStore.get(customerId);
        if (cart == null || cart.isEmpty()) {
            throw new CartNotFoundException("Cart is empty or does not exist for customer ID " + customerId);
        }

        double total = 0;
        for (CartItem item : cart) {
            Book book = bookStore.get(item.getBookId());
            if (book == null) {
                throw new OutOfStockException("Book with ID " + item.getBookId() + " does not exist anymore.");
            }
            if (book.getStock() < item.getQuantity()) {
                throw new OutOfStockException("Book '" + book.getTitle() + "' does not have enough stock.");
            }
            total += book.getPrice() * item.getQuantity();
        }

        // Deduct stock
        for (CartItem item : cart) {
            Book book = bookStore.get(item.getBookId());
            book.setStock(book.getStock() - item.getQuantity());
        }

        Order newOrder = new Order(orderIdCounter++, customerId, new ArrayList<>(cart), total);
        orderStore.computeIfAbsent(customerId, k -> new ArrayList<>()).add(newOrder);
        cartStore.put(customerId, new ArrayList<>()); // clear cart

        return Response.status(Response.Status.CREATED).entity(newOrder).build();
    }

    @GET
    public Response getAllOrders(@PathParam("customerId") int customerId) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        List<Order> orders = orderStore.getOrDefault(customerId, new ArrayList<>());
        return Response.ok(orders).build();
    }

    @GET
    @Path("/{orderId}")
    public Response getOrderById(@PathParam("customerId") int customerId, @PathParam("orderId") int orderId) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        List<Order> orders = orderStore.get(customerId);
        if (orders == null) {
            throw new OrderNotFoundException("No orders found for customer ID " + customerId);
        }
        for (Order o : orders) {
            if (o.getOrderId() == orderId) {
                return Response.ok(o).build();
            }
        }
        throw new OrderNotFoundException("Order ID " + orderId + " not found for customer ID " + customerId);
    }
}