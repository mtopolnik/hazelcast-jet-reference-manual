import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.core.DAG;
import com.hazelcast.jet.pipeline.Pipeline;

import java.util.List;

import static com.hazelcast.jet.core.JobStatus.FAILED;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.counting;

public class ManageJob {
    private static JetInstance jet = Jet.newJetInstance();

    static void s1() {
        //tag::s1[]
        Pipeline pipeline = buildPipeline();
        jet.newJob(pipeline).join();
        //end::s1[]

        //tag::s2[]
        DAG dag = buildDag();
        jet.newJob(dag).join();
        //end::s2[]

        //tag::s3[]
        JobConfig cfg = new JobConfig();
        cfg.setName("my-job");
        jet.newJob(pipeline, cfg);
        //end::s3[]
    }

    static DAG buildDag() {
        return new DAG();
    }

    static Pipeline buildPipeline() {
        return Pipeline.create();
    }

    static void s4() {
        //tag::s4[]
        int completed = 0;
        int failed = 0;
        int inProgress = 0;
        long fiveMinutesAgo = System.currentTimeMillis() - MINUTES.toMillis(5);
        for (Job job : jet.getJobs()) {
            if (job.getSubmissionTime() < fiveMinutesAgo) {
                continue;
            }
            switch (job.getStatus()) {
                case COMPLETED:
                    completed++;
                    break;
                case FAILED:
                    failed++;
                    break;
                default:
                    inProgress++;
            }
            System.out.format(
                "In the last five minutes Jet has completed %d jobs, " +
                "%d jobs failed, and %d jobs are still running.%n",
                completed, failed, inProgress);
        }
        //end::s4[]
    }

    static void s5() {
        //tag::s5[]
        List<Job> myJobs = jet.getJobs("my-job");
        long failedCount = myJobs.stream().filter(j -> j.getStatus() == FAILED).count();
        System.out.format("Jet ran my-job %d times and it failed %d times.%n",
                myJobs.size(), failedCount);
        //end::s5[]
    }

    static void s6() {
        //tag::s6[]
        //end::s6[]
    }
}
