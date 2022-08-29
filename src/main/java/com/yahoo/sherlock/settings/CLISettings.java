/*
 * Copyright 2017, Yahoo Holdings Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

package com.yahoo.sherlock.settings;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;
import com.yahoo.sherlock.utils.DefaultSslContextProvider;
import com.yahoo.sherlock.utils.DefaultSecretProvider;
import com.yahoo.sherlock.utils.Utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Helper class for command line args.
 */
@Slf4j
public class CLISettings {

    /**
     * Help message.
     */
    @Parameter(names = "--help", help = true, description = "Prints help message.")
    public static boolean HELP = false;

    /**
     * Path to a configuration file. If a configuration
     * file is specified, settings will attempt to be read
     * from the file and will overwrite default commandline
     * settings.
     */
    @Parameter(names = "--config", description = "Path to a configuration file")
    public static String CONFIG_FILE = null;

    /**
     * Version of sherlock.
     */
    @Parameter(names = "--version", description = "Version of sherlock.")
    public static String VERSION = "v0.0.0";

    /**
     * Egads config file path.
     */
    @Parameter(names = "--egads-config-filename", description = "EGADS config file. (it uses default if not provided)")
    public static String EGADS_CONFIG_FILENAME = "src/main/resources/egads_config.ini";

    /**
     * Server port.
     */
    @Parameter(names = "--port", description = "Port for the server to build on. (default 4080)")
    public static int PORT = 4080;

    /**
     * Number of minutes to lookback.
     */
    @Parameter(names = "--interval-minutes", description = "training period for egads model for minute granularity. (default 240)")
    public static int INTERVAL_MINUTES = 180;

    /**
     * Number of hours to lookback.
     */
    @Parameter(names = "--interval-hours", description = "training period for egads model for hour granularity. (default 672)")
    public static int INTERVAL_HOURS = 672;

    /**
     * Number of days to lookback.
     */
    @Parameter(names = "--interval-days", description = "training period for egads model for day granularity. (default 28)")
    public static int INTERVAL_DAYS = 28;

    /**
     * Number of weeks to lookback.
     */
    @Parameter(names = "--interval-weeks", description = "training period for egads model for week granularity. (default 12)")
    public static int INTERVAL_WEEKS = 12;

    /**
     * Number of months to lookback.
     */
    @Parameter(names = "--interval-months", description = "training period for egads model for month granularity. (default 6)")
    public static int INTERVAL_MONTHS = 6;

    /**
     * Enable email service.
     */
    @Parameter(names = "--enable-email", description = "training period for egads model for week granularity.")
    public static boolean ENABLE_EMAIL = false;

    /**
     * From email setting.
     */
    @Parameter(names = "--from-mail", description = "FROM MAIL setting for email service.", validateWith = EmailValidator.class)
    public static String FROM_MAIL;

    /**
     * Reply to email setting.
     */
    @Parameter(names = "--reply-to", description = "REPLY TO email setting for email service.", validateWith = EmailValidator.class)
    public static String REPLY_TO;

    /**
     * SMTP host setting.
     */
    @Parameter(names = "--smtp-host", description = "SMTP HOST setting for email service.")
    public static String SMTP_HOST;

    /**
     * SMTP port setting.
     */
    @Parameter(names = "--smtp-port", description = "SMTP PORT setting for email service (default 25).")
    public static int SMTP_PORT = 25;

    /**
     * SMTP user setting.
     */
    @Parameter(names = "--smtp-user", description = "SMTP USER setting for email service.")
    public static String SMTP_USER;

    /**
     * SMTP password setting.
     */
    @Parameter(names = "--smtp-password", description = "SMTP USER setting for email service.")
    public static String SMTP_PASSWORD;

    /**
     * Job email address for failures.
     */
    @Parameter(names = "--failure-email", description = "email to recieve pipeline failures.", validateWith = EmailValidator.class)
    public static String FAILURE_EMAIL;

    /**
     * Job execution delay.
     */
    @Parameter(names = "--execution-delay", description = "the number of seconds between each check(ping to redis) on jobs. (default 30)")
    public static int EXECUTION_DELAY = 30;

    /**
     * Comma-delimited list of valid email domains.
     */
    @Parameter(names = "--valid-domains", description = "Comma-separated list of valid domains, e.g. 'yahoo,gmail,hotmail'")
    public static String VALID_DOMAINS = null;

    /**
     * The hostname for the server that is hosting the
     * redis database, e.g. '127.0.0.1'.
     */
    @Parameter(names = "--redis-host", description = "The hostname for the redis server. (default 127.0.0.1)")
    public static String REDIS_HOSTNAME = "127.0.0.1";

    /**
     * The port for the redis server, e.g. '6379'.
     */
    @Parameter(names = "--redis-port", description = "The port for the redis server. (default 6379)")
    public static int REDIS_PORT = 6379;

    /**
     * Whether to use SSL when communicating with the Redis server.
     */
    @Parameter(names = "--redis-ssl", description = "Use SSL when connecting to Redis")
    public static boolean REDIS_SSL = false;

    /**
     * Timeout in milliseconds when making connection to redis server.
     */
    @Parameter(names = "--redis-timeout", description = "Timeout when connecting to Redis. (default 5000)")
    public static int REDIS_TIMEOUT = 5000;

    /**
     * The password to use when authenticating a Redis server,
     * if the server requires a password.
     */
    @Parameter(names = "--redis-password", description = "Password to use when authenticating Redis")
    public static String REDIS_PASSWORD;

    /**
     * Whether the Redis backend is clustered.
     */
    @Parameter(names = "--redis-clustered", description = "Whether the Redis backend is a cluster")
    public static boolean REDIS_CLUSTERED = false;

    @Parameter(names = "--prophet-url", description = "Prophet Service URL, e.g. 'prophet-service.com:8000'")
    public static String PROPHET_URL = "127.0.0.1:4080";

    @Parameter(names = "--prophet-timeout", description = "Timeout when querying the Prophet Service in milliseconds. (default 120 sec)")
    public static int PROPHET_TIMEOUT = 120000;

    @Parameter(names = "--prophet-principal", description = "Principal of the Prophet Service")
    public static String PROPHET_PRINCIPAL = "prophet-principal";
    /**
     * Whether debug routes should be enabled.
     */
    @Parameter(names = "--debug-mode", description = "Set to true to enable debug mode")
    public static boolean DEBUG_MODE = false;

    /**
     * Whether disable security filter on routes.
     */
    @Parameter(names = "--disable-security-filter", description = "Set to true to disable security filter on routes")
    public static boolean DISABLE_SECURITY_FILTER = false;

    /**
     * The Project name to display on UI.
     */
    @Parameter(names = "--timeseries-completeness", description = "This defines minimum fraction of datapoints needed in the timeseries to consider it as a valid timeseries o/w sherlock ignores such timeseries. (default value 60 i.e. 0.6 in fraction)")
    public static int TIMESERIES_COMPLETENESS = 60;

    /**
     * The Project name to display on UI.
     */
    @Parameter(names = "--project-name", description = "Name of the project to display on UI.")
    public static String PROJECT_NAME;

    /**
     * File path for external files.
     */
    @Parameter(names = "--external-file-path", description = "Specify the path to external files via this arg.")
    public static String EXTERNAL_FILE_PATH;

    /**
     * Timeout in milliseconds for http client.
     */
    @Parameter(names = "--http-client-timeout", description = "Timeout for http client. (default 20000)")
    public static int HTTP_CLIENT_TIMEOUT = 20000;

    /**
     * Backup redis DB local json dump file path.
     */
    @Parameter(names = "--backup-redis-db-path", description = "Backup redis DB local json dump file path. (default null i.e no backup)")
    public static String BACKUP_REDIS_DB_PATH;

    /**
     * File path to specify access control list of permitted druid broker hosts.
     */
    @Parameter(names = "--druid-brokers-list-file", description = "File to add access control list of permitted druid broker hosts. Format: <host1>:<port>,<host2>:<port>... (default null i.e any host is allowed)")
    public static String DRUID_BROKERS_LIST_FILE;

    /**
     * Path to specify truststore location for mTLS connections.
     */
    @Parameter(names = "--truststore-path", description = "Path to specify truststore location for mTLS connections. (default null)")
    public static String TRUSTSTORE_PATH;

    /**
     * Param to specify truststore type for mTLS connections.
     */
    @Parameter(names = "--truststore-type", description = "Param to specify truststore type for mTLS connections. (default \"jks\")")
    public static String TRUSTSTORE_TYPE = "jks";

    /**
     * Param to specify truststore location for mTLS connections.
     */
    @Parameter(names = "--truststore-password", description = "Param to specify truststore password for mTLS connections. (default null)")
    public static String TRUSTSTORE_PASSWORD;

    /**
     * Path to specify keystore location for mTLS connections.
     */
    @Parameter(names = "--keystore-path", description = "Path to specify keystore location for mTLS connections. (default null)")
    public static String KEYSTORE_PATH;

    /**
     * Param to specify keystore type for mTLS connections.
     */
    @Parameter(names = "--keystore-type", description = "Param to specify keystore type for mTLS connections. (default \"jks\")")
    public static String KEYSTORE_TYPE = "jks";

    /**
     * Param to specify keystore password for mTLS connections.
     */
    @Parameter(names = "--keystore-password", description = "Param to specify keystore password for mTLS connections. (default null)")
    public static String KEYSTORE_PASSWORD;

    /**
     * Param to specify key directory containing multiple keys(per different druid cluster) for mTLS connections.
     */
    @Parameter(names = "--key-dir", description = "Param to specify key directory containing multiple keys(for different clusters) for mTLS connections (default null). "
                                                  + "\nThis is used when \"Principal Name\" is given in druid cluster form."
                                                  + "\nIt looks for filename containing \"Principal Name\" under this dir."
                                                  + "\nIf --key-dir and --cert-dir values are same then the filename should also contain the identifier \"key\" for private key file and \"cert\" for public key file.")
    public static String KEY_DIR;

    /**
     * Param to specify cert directory containing multiple certs(per different druid cluster) for mTLS connections.
     */
    @Parameter(names = "--cert-dir", description = "Param to specify cert directory containing multiple certs(for different clusters) for mTLS connections (default null)."
                                                   + "\nThis is used when \"Principal Name\" is given in druid cluster form."
                                                   + "\nIt looks for file name containing \"Principal Name\" under this dir."
                                                   + "\nIf --key-dir and --cert-dir values are same then the filename should also contain the identifier \"key\" for private key file and \"cert\" for public key file.")

    public static String CERT_DIR;

    /**
     * Param to enable/disable https hostname verification for mTLS connections.
     */
    @Parameter(names = "--https-hostname-verification", description = "Param to enable/disable https hostname verification for mTLS connections. (default true i.e. hostname verification enabled)")
    public static boolean HTTPS_HOSTNAME_VERIFICATION = true;

    /**
     * Param to specify custom ssl context provider class for mTLS connections.
     */
    @Parameter(names = "--custom-ssl-context-provider-class", description = "Param to specify custom ssl context provider class for mTLS connections. (default \"com.yahoo.sherlock.utils.DefaultSslContextProvider\" which returns SSLContext with validation)")
    public static String CUSTOM_SSL_CONTEXT_PROVIDER_CLASS = DefaultSslContextProvider.class.getCanonicalName();

    /**
     * Param to specify custom secret provider class for passwords.
     */
    @Parameter(names = "--custom-secret-provider-class", description = "Param to specify custom secret provider class for passwords. (default \"com.yahoo.sherlock.utils.DefaultSecretProvider\" which returns secrets specified from CLISettings)")
    public static String CUSTOM_SECRET_PROVIDER_CLASS = DefaultSecretProvider.class.getCanonicalName();

    /**
     * Parameters to ignore when printing fields.
     */
    private static String[] PRINT_IGNORED = {"log", "HELP", "REDIS_PASSWORD", "PRINT_IGNORED", "TRUSTSTORE_PASSWORD", "KEYSTORE_PASSWORD"};

    /**
     * Print all the settings.
     */
    public void print() {
        Set<String> ignore = new HashSet<>(Lists.newArrayList(PRINT_IGNORED));
        // Get all fields in the class
        Field[] fields = this.getClass().getDeclaredFields();
        // Print the settings
        log.info("Printing all CLI settings...");
        for (Field field : fields) {
            try {
                // Print all columns (skip "log" and "HELP")
                if (!ignore.contains(field.getName())) {
                    log.info("{}: {}", field.getName(), field.get(this));
                }
            } catch (Exception e) {
                log.error("Error getting content of {}", field.getName());
            }
        }
    }

    /**
     * Attempt to load configuration elements from a
     * file if the path to the config file has been
     * specified in the commandline settings.
     *
     * @throws IOException if an error reading/location the file occurs
     */
    public void loadFromConfig() throws IOException {
        // If the config file is defined, attempt to load settings
        // from a properties file
        if (CONFIG_FILE == null) {
            log.info("No configuration file specified, using default/passed settings");
            return;
        }
        log.info("Attempting to read config file at: {}", CONFIG_FILE);
        Field[] configFields = Utils.findFields(getClass(), Parameter.class);
        Properties props = new Properties();
        InputStream is = new FileInputStream(CONFIG_FILE);
        props.load(is);
        for (Field configField : configFields) {
            String configName = configField.getName();
            Class<?> type = configField.getType();
            String configValue = props.getProperty(configName);
            if (configValue != null && configValue.length() > 0) {
                configField.setAccessible(true);
                try {
                    if (type.getName().equals("int")) {
                        configField.set(null, Integer.valueOf(configValue));
                    } else if (type.getName().equals("boolean")) {
                        configField.set(null, Boolean.valueOf(configValue));
                    } else {
                        configField.set(null, configValue);
                    }
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    log.error("Could not set {} to {}: {}", configName, configValue, e.toString());
                    throw new IOException(String.format(
                            "Failed to set %s to value %s",
                            configName,
                            configValue
                    ));
                }
            }
        }
    }
}
