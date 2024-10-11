# Spring Boot and React Tutorial

Author: James Zhang

This tutorial discusses how to create a project with a Java backend and React Frontend. The commands shown here are primarily for the Windows operating system (since this is the OS that I'm using). I may also provide commands for MacOS and Linux, but they are not tested on my computer.

## Installation

Install the following applications:

- VS Code
- IntelliJ IDEA
- Java
- npm

## Backend

First, use [Spring Initializr](https://start.spring.io/) to create a project. Select "Maven" under Project (or use Gradle if you're more familiar to Gradle), "Java" under language, the latest non-Snapshot version of Spring Boot, and fill in the Project Metadata. The Group field is supposed to be a domain name that you control written backwards, like com.example. After you're done, click the Generate button to download the zip file to your computer. This will be the backend of our project.

Let's say that `Project` is the root directory for our project. Then we can unzip the backend inside `Project\` (or `Project/` for Mac/Linux). Let the name of the unzipped directory be `backend`. We can now open the `backend` directory in IntelliJ. It should already have files like `pom.xml`, `mvnw` (which stands for Maven Wrapper), and a `DemoApplication` class in `src\main\java\<package>\`.

We can now create a web controller for a web application (this is copied from the spring.io guide):

```java
// Controller.java
package com.example.demo;  // Your package name here

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @GetMapping("/")
    public String index() {
        return "Hello world!";
    }
}
```

If you see errors on the annotations and the imports, hover over `@RestController`. There is an option from IntelliJ to "Add 'spring-boot-starter-web' to classpath". Select that option to resolve the issue.

The annotation `@RestController` creates a RESTful web service. It is a shorthand for `@Controller` and `@ResponseBody`. By writing `@RestController`, we don't need to write a `@ResponseBody` annotation for every method that handles requests.

The `@GetMapping("/")` annotation handles requests to the endpoint `/`.

To run the application, `cd` to the directory containing `mvnw` and run `.\mvnw spring-boot:run` (or `./mvnw spring-boot:run` for Mac/Linux). If you are using Gradle, run `.\gradlew bootRun`. If we now go to `http://localhost:8080/` on Chrome, we should see "Hello world!", the string that is returned by this method. Use `Ctrl + C` to terminate the program.

## Frontend

We can now build the frontend. For React, I'm going to use Vite, although you can use other ways to set up your React environment (Note that Create React App is now deprecated).

In the `Project` directory, run `npm create vite@latest`. Then follow the instructions to create a React project. Select `JavaScript` as the language (or `TypeScript` if you prefer). I called the project `frontend` for simplicity, but you can call it whatever you like.

Now, run

```cmd
cd frontend
npm install
```

to create the project. Run `code .` to open the project in VS Code. Run `npm run dev` to run the project. If you now go to `http://localhost:5173/` (the port may be different, look at the URL from your terminal), you should see the Vite and React logo with an h1 heading of Vite + React, along with a button that says "count is 0", together with some other text. Type `q` and Enter in the terminal to quit (or you can use `Ctrl + C`).

## Putting Backend and Frontend Together

We can now start creating more endpoints in the backend.

### GET Request

Spring uses the generic class `ResponseEntity` to represent an HTTP response, including the status code, the headers, and the body. For example, we can modify the previous method that handles the `/` endpoint as follows:

```java
@GetMapping("/")
public ResponseEntity<String> hello() {
    return new ResponseEntity<>("Hello world!", HttpStatus.OK);
}
```

This returns the string "Hello world!" with the OK status, which has status code 200. You can also use the `ResponseEntity.BodyBuilder` pattern: `return ResponseEntity.ok().body("Hello world!");`

If you also need headers, you can do the following:

```java
@GetMapping("/")
public ResponseEntity<String> hello() {
    HttpHeaders headers = new HttpHeaders();
    // Add headers here
    return new ResponseEntity<>("Hello world!", headers, HttpStatus.OK);
}
```

Or, you can use the `BodyBuilder` pattern: `return ResponseEntity.ok().headers(headers).body("Hello world!");`

### GET Request with Template Variables

Sometimes, we want to handle get requests where the endpoint contains so called "template variables". For instance, you may have an endpoint `/user/{username}`, where the `username` is a variable that decides which user is displayed on the web page. We can handle these using the `@PathVariable` annotation:

```java
@GetMapping("/number/{num}")
public ResponseEntity<Integer> doubleNumber(@PathVariable("num") int n) {
    System.out.println(n);
    return ResponseEntity.ok().body(n * 2);
}
```

In this example, the `{num}` is read as an integer into the parameter `n`.

### Frontend: Sending Requests with Axios

We will use the Axios package to send requests and receive responses for the frontend. Run `npm install axios` to install Axios. In `App.jsx`, add an import statement `import axios from "axios";` to import the package.

Here is the basic syntax for sending GET requests using axios:

```javascript
axios.get('http://localhost:8080/',)
    .then((response) => {
        console.log(response.data);  // Should print out "Hello world!"
        // Handle data
    })
    .catch((error) => {
        console.log(error);
    });

axios.get('http://localhost:8080/number/42',)
    .then((response) => {
        console.log(response.data);  // Should print out 84
        // Handle data
    })
    .catch((error) => {
        console.log(error);
    });
```

### Aside: CORS Problem

If you followed all the steps above, you should notice that they do not work: The backend should be able to receive the requests, but the frontend cannot receive the responses. If you open the console, you should get an error message like `Access to XMLHttpRequest at 'http://localhost:8080/' from origin 'http://localhost:5173' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.` This is because modern browsers disabled Cross Origin Resource Sharing, which means sharing resources from one domain to a different domain. CORS can lead to security problems, so they are often disabled.

One of the ways to circumvent this issue is to manually add an `Access-Control-Allow-Origin` header in the response. You can either set `Access-Control-Allow-Origin` to your specific client domain (like `http://localhost:5173/`) or set it to `*` to allow all domains.

However, this only works for GET requests. For POST requests, it gets a little more complicated: Since POST requests are often "non-simple requests", the browser will first send a "preflight" OPTIONS request to verify that the server will accept the actual request. If the response signifies that the server will accept the actual request (with `Access-Control-Allow-Headers` and `Access-Control-Allow-Methods` that match the headers and methods of the actual request), then the actual request is sent.

If you don't handle the preflight, the following error message is shown:

```text
localhost/:1 Access to XMLHttpRequest at 'http://localhost:8080/addUser' from origin 'http://localhost:5173' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

You can read [this Stack Overflow post](https://stackoverflow.com/questions/10636611/how-does-the-access-control-allow-origin-header-work) to learn more about Cross Origin Resource Sharing and the Access-Control-Allow-Origin header.

The better way to solve this issue is to use a Spring annotation: `@CrossOrigin(origins = "http://localhost:5173")`. For example:

```java
@CrossOrigin(origins = "http://localhost:5173")
@GetMapping("/")
public ResponseEntity<String> hello() {
    return new ResponseEntity<>("Hello world!", HttpStatus.OK);
}
```

We can also add the `@CrossOrigin` annotation on the entire class which will enable CORS for every handler method. Note that we can use `@CrossOrigin(origins = "http://localhost")` to allow all ports from localhost and `@CrossOrigin(origins = "*")` to allow all sites.

With this in mind, let's now look at POST requests.

### POST Requests

Here's how we can send a POST request with Axios on the frontend:

```javascript
axios.post('http://localhost:8080/addUser', {
      name: "Bob",
      age: 20,
  }, {
      headers: {}  // Add headers here if needed
  })
      .then((response) => {
          console.log(response);
          console.log(response.data);
          setData(response.data);
          // Handle data
      })
      .catch((error) => {
          console.log(error);
      })
```

This sends a post request with the data

```json
{
    "name": "Bob",
    "age": 20,
}
```

On the backend side, we first need to create a User class:

```java
// User.java
package com.example.demo;  // Your package name here

public class User {
    String name;
    int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String toString() {
        return name + ", age " + age;
    }
}
```

You can also use getters and setters if you prefer your fields to be private.

Then, in the controller, we can write:

```java
@CrossOrigin(origins = "http://localhost:5173")
@PostMapping("/addUser")
public ResponseEntity<String> addUser(@RequestBody User user) {
    System.out.println("Name: " + user.name);
    System.out.println("Age: " + user.age);
    return new ResponseEntity<>(user.toString(), HttpStatus.OK);
}
```

Note that the fields in the request body are used to construct the new object, so the name of the fields in the request must match the name of the parameters in the constructor of User. For fields in the constructor that are missing in the request body, the default values (like `null` for objects, `0` for numbers, and `false` for booleans) are used to construct the object.

## Adding Spring Boot to Existing Java Project

To add Spring Boot support to an existing Java project, update the `pom.xml` file by adding the following lines:

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

Also copy `mvnw`, `mvnw.cmd`, `.mvn` and `.gitignore` from a project generated by Spring Initializr to the existing project.

## Useful links

- [Building an Application with Spring Boot](https://spring.io/guides/gs/spring-boot/)
- [React Course - The Odin Project](https://www.theodinproject.com/paths/full-stack-javascript/courses/react)
- [Access Control Allow Origin - Stack Overflow](https://stackoverflow.com/questions/10636611/how-does-the-access-control-allow-origin-header-work)
- [Documentation for the ResponseEntity Class](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html)
