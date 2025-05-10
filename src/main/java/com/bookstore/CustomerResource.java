package com.bookstore;

import com.bookstore.exception.CustomerNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private static Map<Integer, Customer> customerStore = new HashMap<>();
    private static int idCounter = 1;

    @GET
    public Response getAllCustomers() {
        return Response.ok(new ArrayList<>(customerStore.values())).build();
    }

    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") int id) {
        Customer customer = customerStore.get(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }
        return Response.ok(customer).build();
    }

    @POST
    public Response addCustomer(Customer customer) {
        // ✅ Validation for missing name
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Customer Data",
                        "message", "Customer name must not be empty."
                    ))
                    .build());
        }

        // ✅ Validation for missing email
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Customer Data",
                        "message", "Customer email must not be empty."
                    ))
                    .build());
        }

        customer.setId(idCounter++);
        customerStore.put(customer.getId(), customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") int id, Customer updatedCustomer) {
        Customer existingCustomer = customerStore.get(id);
        if (existingCustomer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }

     
        if (updatedCustomer.getName() == null || updatedCustomer.getName().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Customer Data",
                        "message", "Customer name must not be empty during update."
                    ))
                    .build());
        }

        
        if (updatedCustomer.getEmail() == null || updatedCustomer.getEmail().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Invalid Customer Data",
                        "message", "Customer email must not be empty during update."
                    ))
                    .build());
        }

        updatedCustomer.setId(id);
        customerStore.put(id, updatedCustomer);
        return Response.ok(updatedCustomer).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") int id) {
        Customer removedCustomer = customerStore.remove(id);
        if (removedCustomer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }
        return Response.noContent().build();
    }

    public static Map<Integer, Customer> getCustomerStore() {
        return customerStore;
    }
}