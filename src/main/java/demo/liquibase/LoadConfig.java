package demo.liquibase;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class LoadConfig {
    public static String className;
    public static String user;
    public static String password;
    public static int port;
    public static String serverName;
    public static String targetDb;
    public static String referenceDb;
    public static String changeLogMigrate;
    public static String changeLogDiff;
    public static String future;
    public static String version;

    public static Integer loadAppConfig() {
        int result;
        try {
            Path path = Paths.get(System.getProperty("user.dir"), "config", "appconfig.properties");
            File cFile = path.toFile();

            if (cFile.exists() && !cFile.isDirectory()) {
                FileInputStream inputStream = new FileInputStream(cFile);
                Properties cProp = new Properties();
                cProp.load(inputStream);
                inputStream.close();

                LoadConfig.className = cProp.getProperty("dataSourceClassName", "com.microsoft.sqlserver.jdbc.SQLServerDataSource");
                LoadConfig.user = cProp.getProperty("dataSource.user", "");
                LoadConfig.password = cProp.getProperty("dataSource.password", "");
                LoadConfig.port = Integer.parseInt(cProp.getProperty("dataSource.portNumber", "1433"));
                LoadConfig.serverName = cProp.getProperty("dataSource.serverName", "");
                LoadConfig.targetDb = cProp.getProperty("dataSource.databaseName.target", "");
                LoadConfig.referenceDb = cProp.getProperty("dataSource.databaseName.reference", "");
                LoadConfig.changeLogMigrate = cProp.getProperty("file.changelog.migrate", "");
                LoadConfig.changeLogDiff = cProp.getProperty("file.changelog.diff", "");
                LoadConfig.future = cProp.getProperty("app.process.future","migrate");
                LoadConfig.version = cProp.getProperty("app.database.rollback.version","");
                result = 0;
            } else {
                result = 1;
            }
        } catch (Exception e) {
            result = -1;
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return result;
    }
}
