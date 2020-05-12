## Prerequisites
* Make sure the config values in config.properties in resources have correct values.
* Make sure there is an instance of ElasticSearch running and listening on localhost on it's default port.

## Running
1. First you need to fetch Java files. Do this by running the Main file in githubsearch/crawler. This will start an application that fetches a bunch of Java files by cloning git repositories. This will take a while.
2. Run the indexer over the Java files, don't change anything in the config. Do this by running the main file in githubsearch. This will start an application that parses the previously fetched Java files, resolves, ranks and finally indexes them into ElasticSearch.
3. Finally switch to the "frontend" branch using `git checkout frontend`. This is where the web frontend is located, there are further instructions there on how to start the interface.


