package edu.unf.cheffron.server;

import edu.unf.cheffron.server.database.MySQLDatabase;
import edu.unf.cheffron.server.service.WebService;

import java.io.IOException;
import java.sql.SQLException;

public class CheffronServer {

    private static final int PORT = 8808;

    public static void main(String[] args) {
        // connect to database and instantiate database object
        MySQLDatabase database = connectToDatabase(args);

        if (database == null) {
            System.exit(1);
            return;
        }

        try {
            WebService webService = new WebService(database, PORT);
            webService.listen();
            System.out.println("Started web service on port " + PORT);
        } catch (IOException e) {
            System.err.println("Could not start web service! Check port availability/permissions");
        }
    }

    private static MySQLDatabase connectToDatabase(String[] args) {
        System.out.println("Attempting connection to database...");

        if (args.length < 5) {
            System.err.println("Invalid starting parameters. Enter database information as command line arguments");
            System.err.println("Format: [db host] [db name] [db user] [db pass] [db port]");
            return null;
        }

        String host = args[0];
        String name = args[1];
        String user = args[2];
        String pass = args[3];
        String port = args[4];

        if (!port.matches("[0-9]+")) {
            System.err.println("Port must be a valid integer!");
            return null;
        }

        MySQLDatabase database = new MySQLDatabase(host, name, user, pass, Integer.parseInt(port));

        // test connection
        try {
            database.connect();
        } catch (SQLException e) {
            System.err.println("Could not connect database! Check host and credentials");
            e.printStackTrace();
            return null;
        }

        System.out.println("Successfully connected to database.");

        return database;
    }
}
