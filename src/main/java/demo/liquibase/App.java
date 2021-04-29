package demo.liquibase;

import liquibase.CatalogAndSchema;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.integration.commandline.CommandLineUtils;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.structure.DatabaseObject;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, LiquibaseException, ParserConfigurationException, IOException {

        int result = LoadConfig.loadAppConfig();
        Class.forName(LoadConfig.className);
        String ip = LoadConfig.serverName;
        int port = LoadConfig.port;
        String databaseName = LoadConfig.targetDb;
        String databaseName2 = LoadConfig.referenceDb;
        String user = LoadConfig.user;
        String password = LoadConfig.password;

        String filelogMigrate = LoadConfig.changeLogMigrate;
        Database database = genDatabase(ip, port, databaseName, user, password);
        Database database2 = genDatabase(ip, port, databaseName2, user, password);
        if(LoadConfig.future.equalsIgnoreCase("migrate")){
            migrate(filelogMigrate, database);
        }else if(LoadConfig.future.equalsIgnoreCase("diff")){
            diff(database, database2);
        }else if(LoadConfig.future.equalsIgnoreCase("rollback")){
            rollback(filelogMigrate, database, LoadConfig.version);
        }
    }

    private static void diff(Database referenceDatabase, Database targetDatabase) throws LiquibaseException, ParserConfigurationException, IOException {
        Liquibase liquibase = new Liquibase("", new FileSystemResourceAccessor(),referenceDatabase);
        DiffResult diffResult = liquibase.diff(referenceDatabase, targetDatabase, new CompareControl());
//        new FileOutputStream("src/main/resources/db.liquibase/changelog_difference.xml").close();
//        new DiffToChangeLog(diffResult, new DiffOutputControl()).print("src/main/resources/db.liquibase/changelog_difference.xml");
//        migrate("db.liquibase/changelog_difference.xml", targetDatabase);
//        new DiffToChangeLog(diffResult, new DiffOutputControl()).print(System.out);

        new FileOutputStream(LoadConfig.changeLogDiff).close();
        CommandLineUtils.doDiffToChangeLog(LoadConfig.changeLogDiff, targetDatabase, referenceDatabase, new DiffOutputControl(false, false, true, null), null, null);
//        CommandLineUtils.doGenerateChangeLog(LoadConfig.changeLogDiff, referenceDatabase, "", "", "", "", "", "", new DiffOutputControl());
        File file = new File(LoadConfig.changeLogDiff);
        if(file.length()==0){
            System.out.println("File empty");
            return;
        }
//        migrate(LoadConfig.changeLogDiff, referenceDatabase);
    }

    private static void diffObject(Database referenceDatabase, Database targetDatabase) throws LiquibaseException, IOException, ParserConfigurationException {
        CommandLineUtils.doDiffToChangeLog(LoadConfig.changeLogDiff, targetDatabase, referenceDatabase, new DiffOutputControl(false, false, true, null), null, null);
    }

    private static void migrate(String fileLog, Database database) throws LiquibaseException {
        Liquibase liquibase = new Liquibase(fileLog, new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts());
    }

    private static Database genDatabase (String ip, int port, String databaseName, String user, String password) throws ClassNotFoundException, SQLException, DatabaseException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = String.format("jdbc:sqlserver://%s:%d;databaseName=%s;user=%s;password=%s", ip, port, databaseName, user, password);
        Connection connection = DriverManager.getConnection(url);
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        return database;
    }

    private static void rollback(String fileLog, Database database, String version) throws LiquibaseException {
        Liquibase liquibase = new Liquibase(fileLog, new ClassLoaderResourceAccessor(), database);
        liquibase.rollback(version, new Contexts());
    }
}
