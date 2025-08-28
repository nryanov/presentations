package cdc.sample;

import com.impossibl.postgres.jdbc.PGDataSource;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.pgevent.PgEventComponent;
import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    public static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        var main = new Main();

        main.configure().addRoutesBuilder(new PostgresRouteBuilder());

        var ds = new PGDataSource();
        ds.setHost("localhost");
        ds.setPort(5432);
        ds.setDatabaseName("postgres");
        ds.setPassword("postgres");
        ds.setUser("postgres");

        main.bind("datasource", ds);

        main.run(args);
    }

    public static class PostgresRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("pgevent:ignored:0000/ignored/my_channel?datasource=#datasource")
                    .routeId("postgres-listener-alternative")
                    .log("Alternative route - Received: ${body}")
                    .process(exchange -> {
                        String message = exchange.getIn().getBody(String.class);
                        System.out.println("Alternative processing: " + message);
                    });
        }
    }
}
