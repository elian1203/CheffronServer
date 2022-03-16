package edu.unf.cheffron.server.service.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Endpoint 
{
    protected static final Logger LOG = Logger.getLogger("CheffronWebService");
    protected static final Gson GSON = new Gson();

    protected JsonObject getJsonBody(HttpExchange exchange) 
    {
        try 
        {
            String line = new String(exchange.getRequestBody().readAllBytes());
            JsonElement element = JsonParser.parseString(line);

            if (element == null || element.isJsonNull() || !element.isJsonObject()) 
            {
                respondError(exchange, 400, "Invalid json received.");
            } 
            else 
            {
                // return appropriate json object
                return element.getAsJsonObject();
            }

        } 
        catch (IOException ex) 
        {
            LOG.log(Level.WARNING, "Could not read line from request body!", ex);
            respondError(exchange, 500, "Could not read request");
        } 
        catch (JsonSyntaxException ex) 
        {
            LOG.log(Level.WARNING, "Received invalid JSON from client!", ex);
            respondError(exchange, 400, "Invalid json received: " + ex.getMessage());
        }

        // errored out
        return null;
    }

    protected void respond(HttpExchange exchange, int statusCode, String message) 
    {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        try 
        {
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream body = exchange.getResponseBody();

            body.write(bytes);
            body.flush();
            exchange.close();
        } 
        catch (IOException ex) 
        {
            LOG.log(Level.WARNING, "Could not respond to client!", ex);
        }
    }

    protected void respond(HttpExchange exchange, int statusCode, JsonObject body)
     {
        String json = GSON.toJson(body);

        respond(exchange, statusCode, json);
    }

    protected void respondError(HttpExchange exchange, int statusCode, String error) 
    {
        JsonObject object = new JsonObject();
        object.addProperty("error", error);

        respond(exchange, statusCode, object);
    }
}
