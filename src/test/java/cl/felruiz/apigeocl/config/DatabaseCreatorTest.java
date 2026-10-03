package cl.felruiz.apigeocl.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.Ordered;
import org.springframework.mock.env.MockEnvironment;

/**
 * Tests unitarios para DatabaseCreator.
 *
 * Convención de nombres: método_escenario_resultadoEsperado
 */
@ExtendWith(MockitoExtension.class)
class DatabaseCreatorTest {

  private static final String URL = "jdbc:postgresql://localhost:5432/geo_cl?sslmode=disable";
  private static final String ADMIN_URL = "jdbc:postgresql://localhost:5432/postgres?sslmode=disable";

  @Mock
  private Log log;

  @Mock
  private Connection connection;

  @Mock
  private PreparedStatement preparedStatement;

  @Mock
  private ResultSet resultSet;

  @Mock
  private Statement statement;

  private MockedStatic<DriverManager> driverManager;

  private DatabaseCreator databaseCreator;

  private MockEnvironment environment;

  @BeforeEach
  void setUp() {
    driverManager = mockStatic(DriverManager.class);
    databaseCreator = new DatabaseCreator(supplier -> log);
    environment = new MockEnvironment()
        .withProperty("spring.datasource.url", URL)
        .withProperty("spring.datasource.username", "user")
        .withProperty("spring.datasource.password", "pass");
  }

  @AfterEach
  void tearDown() {
    driverManager.close();
  }

  private void stubExists(boolean exists) throws SQLException {
    driverManager.when(() -> DriverManager.getConnection(ADMIN_URL, "user", "pass")).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(exists);
  }

  @Test
  @DisplayName("No debe hacer nada si auto-create está desactivado")
  void postProcessEnvironment_autoCreateDesactivado_noConecta() {
    environment.setProperty("app.database.auto-create", "false");

    databaseCreator.postProcessEnvironment(environment, null);

    driverManager.verifyNoInteractions();
  }

  @Test
  @DisplayName("No debe hacer nada si no hay URL de datasource")
  void postProcessEnvironment_sinUrl_noConecta() {
    databaseCreator.postProcessEnvironment(new MockEnvironment(), null);

    driverManager.verifyNoInteractions();
  }

  @Test
  @DisplayName("No debe hacer nada si la URL no es PostgreSQL")
  void postProcessEnvironment_urlNoPostgres_noConecta() {
    environment.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb");

    databaseCreator.postProcessEnvironment(environment, null);

    driverManager.verifyNoInteractions();
  }

  @Test
  @DisplayName("No debe hacer nada si las credenciales no están definidas")
  void postProcessEnvironment_credencialesSinDefinir_noConecta() {
    environment.setProperty("spring.datasource.username", "${DB_USER}");

    databaseCreator.postProcessEnvironment(environment, null);

    driverManager.verifyNoInteractions();
  }

  @Test
  @DisplayName("No debe crear la base de datos si ya existe")
  void postProcessEnvironment_baseExiste_noCrea() throws SQLException {
    stubExists(true);

    databaseCreator.postProcessEnvironment(environment, null);

    verify(preparedStatement).setString(1, "geo_cl");
    verify(connection, never()).createStatement();
  }

  @Test
  @DisplayName("Debe crear la base de datos si no existe")
  void postProcessEnvironment_baseNoExiste_creaBase() throws SQLException {
    environment.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/geo\"cl?sslmode=disable");
    stubExists(false);
    when(connection.createStatement()).thenReturn(statement);

    databaseCreator.postProcessEnvironment(environment, null);

    verify(statement).executeUpdate("CREATE DATABASE \"geo\"\"cl\"");
    verify(log).info("Base de datos 'geo\"cl' creada");
  }

  @Test
  @DisplayName("Debe registrar un warning si no puede conectarse")
  void postProcessEnvironment_errorSql_registraWarning() {
    // El constructor de SQLException consulta DriverManager, así que se crea antes de stubear
    SQLException error = new SQLException("connection refused");
    driverManager.when(() -> DriverManager.getConnection(ADMIN_URL, "user", "pass")).thenThrow(error);

    databaseCreator.postProcessEnvironment(environment, null);

    verify(log).warn("No se pudo verificar/crear la base de datos 'geo_cl': connection refused");
  }

  @Test
  @DisplayName("Debe registrar un warning y cerrar la conexión si falla la creación")
  void postProcessEnvironment_sinPermisos_registraWarningYCierraConexion() throws SQLException {
    stubExists(false);
    SQLException error = new SQLException("permission denied");
    when(connection.createStatement()).thenReturn(statement);
    when(statement.executeUpdate(anyString())).thenThrow(error);

    databaseCreator.postProcessEnvironment(environment, null);

    verify(connection).close();
    verify(log).warn("No se pudo verificar/crear la base de datos 'geo_cl': permission denied");
  }

  @Test
  @DisplayName("Debe ejecutarse con la menor precedencia")
  void getOrder_retornaLowestPrecedence() {
    assertThat(databaseCreator.getOrder()).isEqualTo(Ordered.LOWEST_PRECEDENCE);
  }
}
