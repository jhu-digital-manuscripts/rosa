These resource maps need a resolvable URL, which may change in the future. Instead of inserting this URL into the resources themselves, a Maven property is substituted in place of the string ${baseUrl}.

To edit the base URL for these resources, the <baseUrl> value must be changed in the rosa-sharedcanvas-test-endpoint's pom.xml

As of 3/15/2013, this baseUrl = http://localhost:8080/rosa-sharedcanvas-test-endpoint/