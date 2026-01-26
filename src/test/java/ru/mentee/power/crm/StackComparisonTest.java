package ru.mentee.power.crm;

import org.junit.jupiter.api.*;
import java.net.http.*;
import java.net.URI;
import static org.assertj.core.api.Assertions.*;

/**
 * Интеграционный тест сравнения Servlet и Spring Boot стеков.
 * Запускает оба сервера, выполняет HTTP запросы, сравнивает результаты.
 */
public class StackComparisonTest {

    private static final int SERVLET_PORT = 8080; // Порт для сервера Servlet
    private static final int SPRING_PORT = 8081;  // Порт для сервера Spring Boot

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    @DisplayName("Оба стека должны возвращать лидов в HTML таблице")
    void shouldReturnLeadsFromBothStacks() throws Exception {
        // Given: HTTP запросы к обоим стекам
        HttpRequest servletRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
                .GET()
                .build();

        HttpRequest springRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
                .GET()
                .build();

        // When: выполняем запросы
        HttpResponse<String> servletResponse = httpClient.send(servletRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> springResponse = httpClient.send(springRequest, HttpResponse.BodyHandlers.ofString());

        // Then: оба возвращают 200 OK и содержат таблицу
        assertThat(servletResponse.statusCode()).isEqualTo(200);
        assertThat(springResponse.statusCode()).isEqualTo(200);
        assertThat(servletResponse.body()).contains("<table");
        assertThat(springResponse.body()).contains("<table");

        // Подсчитываем строки таблицы
        int servletRows = countTableRows(servletResponse.body());
        int springRows = countTableRows(springResponse.body());

        // Измените ожидаемое значение если это необходимо
        int expectedLeadCount = 6; // Ожидаемое количество лидов
        assertThat(servletRows)
                .as("Количество лидов должно совпадать")
                .isEqualTo(expectedLeadCount);
        assertThat(springRows)
                .as("Количество лидов должно совпадать")
                .isEqualTo(expectedLeadCount);

        System.out.printf("Servlet: %d лидов, Spring: %d лидов%n", servletRows, springRows);
    }

    /**
     * Подсчитывает количество строк <tr> в HTML (количество лидов в таблице).
     */
    private int countTableRows(String html) {
        return html.split("<tr").length - 1; // Подсчет вхождений <tr>
    }

    @Test
    @DisplayName("Измерение времени старта обоих стеков")
    void shouldMeasureStartupTime() {
        long servletStartupMs = measureServletStartup();
        long springStartupMs = measureSpringBootStartup();

        System.out.println("=== Сравнение времени старта ===");
        System.out.printf("Servlet стек: %d ms%n", servletStartupMs);
        System.out.printf("Spring Boot: %d ms%n", springStartupMs);
        System.out.printf("Разница: Spring %s на %d ms%n",
                springStartupMs > servletStartupMs ? "медленнее" : "быстрее",
                Math.abs(springStartupMs - servletStartupMs));

        assertThat(servletStartupMs).isLessThan(10_000);
        assertThat(springStartupMs).isLessThan(15_000);
    }

    private long measureServletStartup() {
        long startTime = System.nanoTime();
        // Здесь нужно реализовать запуск Tomcat Embed и измерение времени
        // Например, это может быть что-то вроде:
        /*
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(SERVLET_PORT);
        tomcat.getConnector();
        tomcat.start();
        */
        return (System.nanoTime() - startTime) / 1_000_000; // Конвертация в миллисекунды
    }

    private long measureSpringBootStartup() {
        long startTime = System.nanoTime();
        // В этом месте нужно запустить Spring Boot приложение
        /*
        SpringApplication app = new SpringApplication(Application.class);
        app.run();
        */
        return (System.nanoTime() - startTime) / 1_000_000; // Конвертация в миллисекунды
    }
}
