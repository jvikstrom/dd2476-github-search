package githubsearch.crawler;

import java.io.IOException;

/**
 * This runs the crawler routine. It clones a bunch of git repositories, grabs all java files in them and saves them in the specified directory.
 */
public class Main {
    final static String tmpRepositoryRootPath = "/home/jovi/school/search/raw-repos"; // The folder where we will save all raw repositories.
    final static String javaFileRootPath = "/home/jovi/school/search/java-files"; // An empty folder where all java files will be saved.
    final static String javaFileIndexPath = "/home/jovi/school/search/java-index"; // Will create the file at this path containing the file metadatas (the folders must exist)
    final static String cloneCachePath = "/home/jovi/school/search/clone-cache";

    // This contains a list of "notable"/big Java projects that are publicly available. We want to make sure we clone and index these.
    final static String[] repositoryURLs = new String[]{
            // First some Apache projects
            "https://github.com/apache/zookeeper.git", // Zookeeper
            "https://github.com/apache/cassandra.git", // Cassandra
            "https://github.com/apache/lucene-solr.git", // Lucene and Solr
            "https://github.com/apache/helix.git", // Helix
            "https://github.com/apache/bookkeeper.git", // BookKeeper
            "https://github.com/apache/beam.git", // Beam
            "https://github.com/apache/kafka.git", // Kafka
            "https://github.com/apache/hbase.git", // HBase
            "https://github.com/apache/tomcat.git", // Tomcat
            "https://github.com/apache/wicket.git", // Wicket
            "https://github.com/apache/storm.git",
            "https://github.com/apache/activemq-artemis.git", // Artemis (MQ)
            "https://github.com/apache/incubator-iceberg.git",
            "https://github.com/apache/pulsar.git", // Pulsar pub-sub
            "https://gitbox.apache.org/repos/asf/maven-sources.git", // Maven
            "https://github.com/apache/cloudstack.git", // Cloudstack
            "https://github.com/apache/ignite.git", // Ignite
            "https://github.com/apache/sis.git", // SIS (Geo-stuff)
            "https://gitbox.apache.org/repos/asf/syncope.git", // Syncope (IAM)
            // Apache common language stuff
            "https://gitbox.apache.org/repos/asf/commons-lang.git",
            "https://gitbox.apache.org/repos/asf/commons-bcel.git",
            "https://gitbox.apache.org/repos/asf/commons-beanutils.git",
            "https://gitbox.apache.org/repos/asf/commons-bsf.git",
            "https://gitbox.apache.org/repos/asf/commons-chain.git",
            "https://gitbox.apache.org/repos/asf/commons-cli.git",
            "https://gitbox.apache.org/repos/asf/commons-codec.git",
            "https://gitbox.apache.org/repos/asf/commons-collections.git",
            "https://gitbox.apache.org/repos/asf/commons-compress.git",
            "https://gitbox.apache.org/repos/asf/commons-configuration.git",
            "https://gitbox.apache.org/repos/asf/commons-crypto.git",
            "https://gitbox.apache.org/repos/asf/commons-csv.git",
            "https://gitbox.apache.org/repos/asf/commons-daemon.git",
            "https://gitbox.apache.org/repos/asf/commons-dbcp.git",
            "https://gitbox.apache.org/repos/asf/commons-dbutils.git",
            "https://gitbox.apache.org/repos/asf/commons-digester.git",
            "https://gitbox.apache.org/repos/asf/commons-email.git",
            "https://gitbox.apache.org/repos/asf/commons-exec.git",
            "https://gitbox.apache.org/repos/asf/commons-fileupload.git",
            "https://gitbox.apache.org/repos/asf/commons-functor.git",
            "https://gitbox.apache.org/repos/asf/commons-geometry.git",
            "https://gitbox.apache.org/repos/asf/commons-imaging.git",
            "https://gitbox.apache.org/repos/asf/commons-io.git",
            "https://gitbox.apache.org/repos/asf/commons-jci.git",
            "https://gitbox.apache.org/repos/asf/commons-jcs.git",
            "https://gitbox.apache.org/repos/asf/commons-jelly.git",
            "https://gitbox.apache.org/repos/asf/commons-jexl.git",
            "https://gitbox.apache.org/repos/asf/commons-jxpath.git",
            "https://gitbox.apache.org/repos/asf/commons-lang.git",
            "https://gitbox.apache.org/repos/asf/commons-logging.git",
            "https://gitbox.apache.org/repos/asf/commons-math.git",
            "https://gitbox.apache.org/repos/asf/commons-net.git",
            "https://gitbox.apache.org/repos/asf/commons-numbers.git",
            "https://gitbox.apache.org/repos/asf/commons-ognl.git",
            "https://gitbox.apache.org/repos/asf/commons-pool.git",
            "https://gitbox.apache.org/repos/asf/commons-proxy.git",
            "https://gitbox.apache.org/repos/asf/commons-rdf.git",
            "https://gitbox.apache.org/repos/asf/commons-rng.git",
            "https://gitbox.apache.org/repos/asf/commons-scxml.git",
            "https://gitbox.apache.org/repos/asf/commons-statistics.git",
            "https://gitbox.apache.org/repos/asf/commons-text.git",
            "https://gitbox.apache.org/repos/asf/commons-vfs.git",
            "https://gitbox.apache.org/repos/asf/commons-weaver.git",
            // Start of the Spring projects.
            "https://github.com/spring-projects/spring-session.git",
            "https://github.com/spring-projects/spring-security-oauth.git",
            "https://github.com/spring-projects/spring-boot-data-geode.git",
            "https://github.com/spring-projects/spring-batch.git",
            "https://github.com/spring-projects/spring-data-elasticsearch.git",
            "https://github.com/spring-projects/spring-kafka.git",
            "https://github.com/spring-projects/spring-security.git",
            "https://github.com/spring-projects/spring-integration.git",
            "https://github.com/spring-projects/spring-boot.git",
            "https://github.com/spring-projects/spring-hateoas.git",
            "https://github.com/spring-projects/spring-framework.git",
            "https://github.com/spring-projects/spring-data-jdbc.git",
            "https://github.com/spring-projects/spring-amqp.git",
            "https://github.com/spring-projects/spring-data-geode.git",
            "https://github.com/spring-projects/spring-data-gemfire.git",
            "https://github.com/spring-projects/spring-webflow.git",
            "https://github.com/spring-projects/atom-cf-manifest-yaml.git",
            "https://github.com/spring-projects/spring-data-dev-tools.git",
            "https://github.com/spring-projects/spring-credhub.git",
            "https://github.com/spring-projects/spring-ldap.git",
            "https://github.com/spring-projects/toolsuite-distribution.git",
            "https://github.com/spring-projects/spring-integration-kafka.git",
            "https://github.com/spring-projects/spring-integration-samples.git",
            "https://github.com/spring-projects/spring-data-jpa.git",
            "https://github.com/spring-projects/sts4.git",
            "https://github.com/spring-projects/spring-vault.git",
            // That's the first page of: https://github.com/spring-projects, still have 6 more to go
    };
    public static void main(String[] args) throws IOException {

        MultiGitCloner cloner = new MultiGitCloner(repositoryURLs, 1, 2000, tmpRepositoryRootPath, javaFileRootPath, javaFileIndexPath, cloneCachePath);
        cloner.crawl();
    }
}
