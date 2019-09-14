# Jackson JSON Module for Javimmutable Collections

This library provides an extension to allow the Jackson library to convert Jimmutable collections into JSON and back.  To use the module simply create a JImmutableModule and pass it to your ObjectMapper.

````
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JImmutableModule());
````

Once this has been done Jackson can automatically serialize and deserialize JImmutableLists and JImmutableSets.

This module requires JImmutable Collections 3.0.0 or higher and Jackson version 2.9.9 and databind version 2.9.9.3 or higher.

````
    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.9.3</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.9.9</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.javimmutable</groupId>
            <artifactId>javimmutable-collections</artifactId>
            <version>3.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
````

