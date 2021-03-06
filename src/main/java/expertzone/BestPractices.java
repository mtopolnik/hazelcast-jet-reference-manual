package expertzone;

import com.hazelcast.jet.Util;
import com.hazelcast.jet.core.DAG;
import com.hazelcast.jet.core.Vertex;
import com.hazelcast.jet.core.test.TestSupport;

import static com.hazelcast.jet.aggregate.AggregateOperations.counting;
import static com.hazelcast.jet.core.Edge.from;
import static com.hazelcast.jet.core.processor.DiagnosticProcessors.peekInputP;
import static com.hazelcast.jet.core.processor.Processors.combineByKeyP;
import static com.hazelcast.jet.core.processor.Processors.mapP;
import static com.hazelcast.jet.core.processor.SinkProcessors.writeFileP;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

class BestPractices {

    private static
    //tag::s1[]
    DAG dag = new DAG();
    //end::s1[]

    static void s1() {
        //tag::s1[]
        Vertex combine = dag.newVertex("combine",
                combineByKeyP(counting(), Util::entry)
        );
        //end::s1[]
    }

    static void s2() {
        //tag::s2[]
        Vertex combine = dag.newVertex("combine",
                peekInputP(combineByKeyP(counting(), Util::entry))
        );
        //end::s2[]
    }

    static void s3() {
        Vertex tokenize = null;
        //tag::s3[]
        Vertex diagnose = dag
                .newVertex("diagnose", writeFileP(
                        "tokenize-output", Object::toString, UTF_8, false))
                .localParallelism(1);
        dag.edge(from(tokenize, 1).to(diagnose));
        //end::s3[]
    }

    static void s4() {
        //tag::s4[]
        TestSupport.verifyProcessor(mapP((String s) -> s.toUpperCase()))
                   .disableCompleteCall()       // <1>
                   .disableLogging()            // <1>
                   .disableProgressAssertion()  // <1>
                   .disableSnapshots()          // <1>
                   .cooperativeTimeout(2000)                         // <2>
                   .outputChecker(TestSupport.SAME_ITEMS_ANY_ORDER)  // <3>
                   .input(asList("foo", "bar"))                      // <4>
                   .expectOutput(asList("FOO", "BAR"));
        //end::s4[]
    }
}
