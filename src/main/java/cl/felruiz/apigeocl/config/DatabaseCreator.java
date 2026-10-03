package cl.felruiz.apigeocl.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Crea la base de datos PostgreSQL si aún no existe, antes de que Spring
 * inicialice el DataSource y Flyway.
 *
 * Se conecta a la base de mantenimiento "postgres" del mismo servidor con las
 * credenciales configuradas y ejecuta CREATE DATABASE si hace falta. Si no
 * puede (sin permisos, servidor caído), solo deja un warning y el arranque
 * sigue su curso normal.
 *
 * Se registra en META-INF/spring.factories.
 */
public class DatabaseCreator implements EnvironmentPostProcessor, Ordered {

  /** jdbc:postgresql://host:puerto/nombre_bd?parametros */
  private static final Pattern POSTGRES_URL = Pattern.compile("^(jdbc:postgresql://[^/]+/)([^?;]+)(.*)$");

  private final Log log;

  public DatabaseCreator(DeferredLogFactory logFactory) {
    this.log = logFactory.getLog(DatabaseCreator.class);
  }

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    if (!environment.getProperty("app.database.auto-create", Boolean.class, true)) {
      return;
    }

    String url = environment.getProperty("spring.datasource.url");
    if (url == null) {
      return;
    }

    Matcher matcher = POSTGRES_URL.matcher(url);
    if (!matcher.matches()) {
      return; // No es PostgreSQL (ej. H2 en tests)
    }

    String dbName = matcher.group(2);
    String adminUrl = matcher.group(1) + "postgres" + matcher.group(3);
    String username;
    String password;
    try {
      username = environment.getProperty("spring.datasource.username");
      password = environment.getProperty("spring.datasource.password");
    } catch (IllegalArgumentException e) {
      return; // Credenciales sin definir (ej. CI sin .env): nada que crear
    }

    try (Connection connection = DriverManager.getConnection(adminUrl, username, password)) {
      if (!exists(connection, dbName)) {
        try (Statement statement = connection.createStatement()) {
          statement.executeUpdate("CREATE DATABASE " + quoteIdentifier(dbName));
        }
        log.info("Base de datos '" + dbName + "' creada");
      }
    } catch (SQLException e) {
      log.warn("No se pudo verificar/crear la base de datos '" + dbName + "': " + e.getMessage());
    }
  }

  private static boolean exists(Connection connection, String dbName) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
      statement.setString(1, dbName);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    }
  }

  private static String quoteIdentifier(String identifier) {
    return "\"" + identifier.replace("\"", "\"\"") + "\"";
  }

  /** Después de ConfigDataEnvironmentPostProcessor, para leer application.yaml y .env */
  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
