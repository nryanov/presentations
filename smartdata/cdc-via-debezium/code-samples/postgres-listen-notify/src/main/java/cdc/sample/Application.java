package cdc.sample;

import com.impossibl.postgres.jdbc.PGDataSource;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Application {
    public static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        var main = new Main();

        main.configure().addRoutesBuilder(new PostgresRouteBuilder());

        var pgDatasource = new PGDataSource();
        pgDatasource.setHost("localhost");
        pgDatasource.setPort(5432);
        pgDatasource.setDatabaseName("postgres");
        pgDatasource.setPassword("postgres");
        pgDatasource.setUser("postgres");

        var datasource = new PGSimpleDataSource();
        datasource.setUser("postgres");
        datasource.setPassword("postgres");
        datasource.setDatabaseName("postgres");
        datasource.setURL("jdbc:postgresql://localhost:5432/");

        main.bind("pgEventDatasource", pgDatasource);
        main.bind("pgSimpleDatasource", datasource);

        main.run(args);
    }

    public static class PostgresRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("pgevent:ignored:0000/ignored/my_channel?datasource=#pgEventDatasource")
                    .routeId("postgres-listener-alternative")
                    .log("Alternative route - Received: ${body}")
                    .process(exchange -> {
                        String message = exchange.getIn().getBody(String.class);
                        System.out.println("Alternative processing: " + message);
                    });

            var random = new Random();

            // every 10 seconds
            from("timer:randomQuery?period=10000")
                    .routeId("sql-query")
                    .process(exchange -> {
                        var op = random.nextInt(3);

                        String sql;
                        if (op == 0) {
                            sql = "INSERT INTO data(value) VALUES(gen_random_uuid()::text)";
                        } else if (op == 1) {
                            sql = "UPDATE data SET value = gen_random_uuid()::text WHERE id = (SELECT min(id) FROM data)";
                        } else {
                            sql = "DELETE FROM data WHERE id = (SELECT min(id) FROM data)";
                        }

                        exchange.getIn().setBody(sql);
                    })
                    .toD("sql:${body}?dataSource=#pgSimpleDatasource")
                    .log("Dynamic query result: ${body}");
        }
    }
}
