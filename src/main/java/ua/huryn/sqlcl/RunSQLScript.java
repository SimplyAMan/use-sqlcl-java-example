package ua.huryn.sqlcl;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import oracle.dbtools.db.ResultSetFormatter;
import oracle.dbtools.raptor.newscriptrunner.*;

public class RunSQLScript {
    private static Logger logger;// = Logger.getLogger(RunSQLScript.class.getCanonicalName());;

    static {
        InputStream inputStream = RunSQLScript.class.getResourceAsStream("/logging.properties");
        if (null != inputStream) {
            try {
                LogManager.getLogManager().readConfiguration(inputStream);
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "init logging system", e);
            }
            logger = Logger.getLogger(RunSQLScript.class.getCanonicalName());
        }
    }

    private String db_url;
    private String db_user;
    private String db_password;

    private File resourceFile;

    public RunSQLScript(String db_url, String db_user, String db_password) {
        this.db_url = db_url;
        this.db_user = db_user;
        this.db_password = db_password;
    }

    public File getFileFromURL() {
        if (resourceFile == null) {
            URL url = this.getClass().getClassLoader().getResource("sql");
            try {
                resourceFile = new File(url.toURI());
            } catch (URISyntaxException e) {
                resourceFile = new File(url.getPath());
            }
        }
        return resourceFile;
    }

    /**
     * Run one sql script using sqlcl
     *
     * @param scriptPath absolutely path to sql script
     * @throws UnsupportedEncodingException
     */
    String runScript(String scriptPath) throws UnsupportedEncodingException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@" + db_url, db_user, db_password);
        connection.setAutoCommit(false);

        String results = "";
        if (connection != null) {
            try {
                // create sqlcl
                ScriptExecutor sqlcl = new ScriptExecutor(connection);

                // setup the context
                ScriptRunnerContext ctx = new ScriptRunnerContext();

                // set the output max rows
                ResultSetFormatter.setMaxRows(10000);
                // set the context
                sqlcl.setScriptRunnerContext(ctx);
                ctx.setBaseConnection(connection);

                // Capture the results without this it goes to STDOUT
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                BufferedOutputStream buf = new BufferedOutputStream(bout);
                sqlcl.setOut(buf);

                // run a whole file
                // adjust the path as it needs to be absolute
                String commandToRun = "@" + scriptPath;
                logger.info("Command to run - " + commandToRun);
                sqlcl.setStmt(commandToRun);

//                printProperties(sqlcl);
                sqlcl.run();
                sqlcl.getScriptRunnerContext().write("test");
//                printProperties(sqlcl);

                results = bout.toString("UTF8");
                results = results.replaceAll(" force_print\n", "");
                logger.fine("result - " + results);

                connection.close();
            } catch (SQLException e) {
                logger.severe("Connection Failed! Check output console - " + e);
            }
        } else {
            logger.severe("Failed to make connection!");
        }
        return results;
    }

    private void printProperties(ScriptExecutor sqlcl) {
        ScriptRunnerContext scriptRunnerContext = sqlcl.getScriptRunnerContext();
        System.out.println("Properties");
        for (Map.Entry<String, Object> entry: scriptRunnerContext.getProperties().entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

}
