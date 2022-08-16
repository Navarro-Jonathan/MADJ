package com.madj;

import java.sql.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
/**
 * <h1>MADJ Server</h1>
 * The server backend to the MADJ website.
 * <p>
 * <b>Note:</b> This server uses localhost port 8080 by default.
 *
 * @author  Jonathan Navarro
 * @author Mitchel Mercer
 * @version 1.0
 * @since   2022-08-10
 */
@SpringBootApplication
@RestController
public class Driver
{
    /**
     * The main entry point for the server.
     * @param args Used in SpringBoot initialization.
     * @see SpringApplication
     * @see GCloudConnector
     */
    public static void main (String[] args)
    {
        // SpringBoot Starts
        SpringApplication.run(Driver.class, args);

        // Close the Google Cloud JDBC connection on server stop.
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                Connection conn = GCloudConnector.getInstance().connection;
                if (conn != null)
                {
                    try
                    {
                        conn.close ();
                        System.out.println ("Database connection terminated");
                    }
                    catch (Exception e) { }
                }
            }
        });
    }

}