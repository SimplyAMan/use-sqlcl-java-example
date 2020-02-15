package ua.huryn.sqlcl;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.*;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

public class RunSQLScriptTest {

    private static String db_url;
    private static String db_user;
    private static String db_password;

    @BeforeClass
    public static void beforeAll() {
        Properties properties = new Properties();
        InputStream inputStream = RunSQLScript.class.getResourceAsStream("/database.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        db_url = properties.getProperty("datasource.url","localhost:1818/XE");
        db_user = properties.getProperty("datasource.username", "system");
        db_password = properties.getProperty("datasource.password", "oracle");
    }

    @Test
    public void testConditional() throws UnsupportedEncodingException, SQLException {
        RunSQLScript app = new RunSQLScript(db_url, db_user, db_password);
        String folder = app.getFileFromURL().getAbsolutePath();

        String mainScript = Paths.get(folder,"test_conditional.sql").toString();

        String results = app.runScript(mainScript);
        System.out.println(results);
    }

    @Test
    public void testSubstitution() throws UnsupportedEncodingException, SQLException {
        RunSQLScript app = new RunSQLScript(db_url, db_user, db_password);
        String folder = app.getFileFromURL().getAbsolutePath();

        String mainScript = Paths.get(folder,"test_substitution.sql").toString();
        System.out.println("run " + mainScript);

        String results = app.runScript(mainScript);
        System.out.println(results);
    }

    @Test
    public void testRunOneFromAnother() throws UnsupportedEncodingException, SQLException {
        RunSQLScript app = new RunSQLScript(db_url, db_user, db_password);
        String folder = app.getFileFromURL().getAbsolutePath();

        String mainScript = Paths.get(folder,"one-from-another","run_myfile.sql").toString();
        System.out.println("run " + mainScript);

        String results = app.runScript(mainScript);
        System.out.println(results);
    }

    @Test
    public void testRunManyFromOne() throws UnsupportedEncodingException, SQLException {
        RunSQLScript app = new RunSQLScript(db_url, db_user, db_password);
        String folder = app.getFileFromURL().getAbsolutePath();

        String mainScript = Paths.get(folder,"run-many-from-one","run.sql").toString();
        System.out.println("run " + mainScript);

        String results = app.runScript(mainScript);
        System.out.println(results);
    }

    /*@Test
    @Ignore
    public void deployUtPLSQL() throws UnsupportedEncodingException, SQLException {
        RunSQLScript app = new RunSQLScript(db_url, db_user, db_password);
        String folder = app.getFileFromURL().getAbsolutePath();

        String mainScript = Paths.get(folder,"utPLSQL_v3.1.9","source","install_headless.sql").toString();

        String results = app.runScript(mainScript);
        System.out.println(results);
    }*/
}