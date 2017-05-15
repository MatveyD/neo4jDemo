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
            String questionType = "Who";
            String label1 = "";
            String label2 = "";
            String relation = "ACTED_IN";
            String relationPropertyType1 = "roles:";
            String relationToken1 = "'Jimmy Dugan'";
            String tokenType1 ="name";
            String token1 ="Tom Hanks";
            if (questionType.equals("Who"))
                label1 =":Person";
            if (!relation.equals("")) {
                relationPropertyType1 = "{" + relationPropertyType1;
                relationToken1 = relationToken1 + "}";
            }
            //String query ="MATCH (a:"+label+") WHERE a."+relation+" = {from} RETURN a.name AS name, a.from AS title";
            //"MATCH (a:"+label+") WHERE a.from = {from} RETURN a.name AS name, a.from AS title"
            //String query ="MATCH (n1:"+label+")-[r:"+relation+"]->(n2) where n2.title = {title} RETURN n1, r, n2";
            String query = "MATCH (n1"+label1+" {"+tokenType1+": {token}})-[r:"+relation+ relationPropertyType1 + relationToken1 +"]->(n2"+ label2 +") RETURN n1.name ,n2.title";
            // tag::minimal-example[]
            Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", ""));

            try (Session session = driver.session()) {

                try (Transaction tx = session.beginTransaction()) {
                    tx.run("CREATE (a:Person {name: {name}, title: {title}})",
                            parameters("name", "Arthur", "title", "King"));
                    tx.success();
                }

                try (Transaction tx = session.beginTransaction()) {
                    StatementResult result = tx.run(query,
                            parameters("token", token1));
                    while (result.hasNext()) {
                        Record record = result.next();
                        System.out.println(String.format("%s %s", record.get("n1.name").asString(), record.get("n2.title").asString()));
                    }
                }

            }

            driver.close();
            // end::minimal-example[]
        }
    }
}