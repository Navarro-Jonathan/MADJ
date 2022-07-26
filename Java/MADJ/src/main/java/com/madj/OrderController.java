package com.madj;

import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Rest controller for orders to handle front-end and back-end connection. All methods are mapped to <b>/api</b>.
 * @author  Jonathan Navarro
 * @author Mitchel Mercer
 * @version 1.0
 * @since 2022-08-11
 * @see Order
 * @see RestController
 */
@RestController
@RequestMapping("/api")
public class OrderController {
    /**
     * Method to send order information to server from client. Mapped to <b>/api/send-order</b>. Called from api using POST request.
     * @param orderJSONInformation JSON formatted to fit the OrderJSONInformation properties. Included in the request's body.
     * @see OrderJSONInformation
     */
    @CrossOrigin(origins = "https://madjsite.web.app")
    @RequestMapping(
            value = "send-order",
            method = RequestMethod.POST)
   public void ReceiveOrder(@RequestBody OrderJSONInformation orderJSONInformation){
        // Initialize variables. Cache product and quantity lists
        HashMap<Product, Integer> orderProducts = new HashMap<>();
        List<Integer> products = orderJSONInformation.getProductIDs();
        List<Integer> quantities = orderJSONInformation.getQuantities();
        // Stores products and their quantities in a hashmap instead of two lists
        for(int i = 0; i < products.size(); i++){
            int id = products.get(i);
            Product pr = ProductController.globalRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
            orderProducts.put(pr, quantities.get(i));
        }
        // Create the new order entry in the online database
        Order order = new Order(orderJSONInformation.getBillingName(), orderJSONInformation.getBillingAddress(), orderJSONInformation.getCustomerName(), orderJSONInformation.getEmail(), orderJSONInformation.getCardInfo(), orderProducts);
        saveOrderToDB(order);
        sendConfirmationEmail(order);
        System.out.println("Received data: " + orderJSONInformation.toString());
    }

    /**
     * Helper method to send the confirmation email using the Mailer class.
     * @param order
     * @see Mailer
     */
    private void sendConfirmationEmail(Order order){
        // Format the message body in HTML and make
        String body = "<h1>Thank you for your order, " + order.getCustomerName() + ".</h1>\n" +
                "<p>\n" +
                "Billing name: \n" + order.getBillingName() +
                "</p>\n" +
                "<p>\n" +
                "Billing address: \n" + order.getBillingAddress() +
                "</p>\n" +
                "<p>\n" +
                "Customer name: \n" + order.getCustomerName() +
                "</p>" +
                "<p>\n" +
                "Total: $" + (((float)order.getTotal()) / 100) +
                "</p>";
        body += "<p><h3><u>Products</u><h3></p>";
        ArrayList<Order.ProductInformation> products = order.getProductsInfo();
        for(int i = 0; i < order.getProductsInfo().size(); i++){
            body += products.get(i).getAsHTMLText();
        }
        // Use the Mailer helper class to send the confirmation email
        Mailer.send(order.getEmail(), "MADJ Order Confirmation", body);
    }

    /**
     * Helper method to save a given order to the Google Cloud database.
     * @param order
     * @see GCloudConnector
     */
    private void saveOrderToDB(Order order){
        // Send a new order entry to the Google Cloud database
        String query = "INSERT INTO orders (billing_name, billing_address, email_address, customer_name, card_information, total)\n" +
                " values (" +
                "\"" + order.getBillingName() + "\"" +
                ",\"" + order.getBillingAddress() + "\"" +
                ",\"" + order.getEmail() + "\"" +
                ",\"" + order.getCustomerName() + "\"" +
                ",\"" + order.getCardInfo() + "\"" +
                ",\"" + order.getTotal() + "\"" +
                ");";
        try(Statement statement = GCloudConnector.getInstance().connection.createStatement()){
            statement.executeUpdate(query);

            // Get the order id of this order to be used in the order_items entries
            int orderId = -1;
            ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            /* Send the products and quantities to the database using in order_items table.
             * The order_items entries are in a many-to-one relationship with the orders entries.
             * All order_items are associated with a product and an order. The product_ids and order_ids are foreign keys.
             */
            for(Order.ProductInformation product : order.getProductsInfo()){
                query = "INSERT INTO order_items (order_id, product_id, quantity)\n" +
                        " values (" +
                        "\"" + orderId + "\"" +
                        ",\"" + product.product.getId() + "\"" +
                        ",\"" + product.quantity + "\"" +
                        ");";
                statement.executeUpdate(query);
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
