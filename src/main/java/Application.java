/**
 * Created by matv1 on 22.03.2017.
 */
// NOTE: Be careful about auto-formatting here: All imports should be between the tags below.
// tag::minimal-example-import[]

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.neo4j.driver.v1.*;

import java.io.IOException;

import static org.neo4j.driver.v1.Values.parameters;
// end::minimal-example-import[]

public class Application {
    public static void main(String[] args) throws InvalidFormatException,
            IOException {
        {
            String label ="Place";
            String query ="MATCH (a:"+label+") WHERE a.from = {from} RETURN a.name AS name, a.from AS title";
            // tag::minimal-example[]
            Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));

            try (Session session = driver.session()) {

                try (Transaction tx = session.beginTransaction()) {
                    tx.run("CREATE (a:Person {name: {name}, title: {title}})",
                            parameters("name", "Arthur", "title", "King"));
                    tx.success();
                }

                try (Transaction tx = session.beginTransaction()) {
                    StatementResult result = tx.run(query,
                            parameters("from", "Sweden"));
                    while (result.hasNext()) {
                        Record record = result.next();
                        System.out.println(String.format("%s %s", record.get("name").asString(), record.get("title").asString()));
                    }
                }

            }

            driver.close();
            // end::minimal-example[]
        }
    }
}