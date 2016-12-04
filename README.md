# basket

This application has been developed by Javier Lopez using JHipster application generator.<br><br>
To run the application:<br>
 1- go to the application directory: basket.<br>
 2- run "gradlew"<br>
 3- go in your browser to http://localhost:8080/<br><br>
 
There is a demo available in:<br>

https://youtu.be/z_XnWbTBy8U

<br>

You can find the UML data model in:<br>
https://jhipster.github.io/jdl-studio/#/view/entity%20Item%20%7B%0A%09name%20String,%0A%09price%20Long,%0A%20%20%20%20is3x2%20Boolean%0A%7D%0A%0Aentity%20OrderLine%20%7B%0A%09number%20Integer,%0A%20%20%20%20totalOrderLine%20Long%0A%7D%0A%0Aentity%20Basket%20%7B%0A%09totalPrice%20Long%0A%7D%0A%0Arelationship%20OneToOne%20%7B%0A%09OrderLine%7Bof%7D%20to%20Item%0A%7D%0A%0Arelationship%20OneToMany%20%7B%0A%09Basket%7Bcontain%7D%20to%20OrderLine%0A%7D
<br>


This application was generated using JHipster 3.10.0, you can find documentation and help at [https://jhipster.github.io/documentation-archive/v3.10.0](https://jhipster.github.io/documentation-archive/v3.10.0).

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:
1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools (like
[Bower][] and [BrowserSync][]). You will only need to run this command when dependencies change in package.json.

    npm install

We use [Gulp][] as our build system. Install the Gulp command-line tool globally with:

    npm install -g gulp-cli

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./gradlew
    gulp

Bower is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in `bower.json`. You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

## Building for production

To optimize the basket application for production, run:

    ./gradlew -Pprod clean bootRepackage

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar build/libs/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./gradlew test

### Client tests

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in `src/test/javascript/` and can be run with:

    gulp test


### Other tests

Performance tests are run by [Gatling][] and written in Scala. They're located in `src/test/gatling` and can be run with:

    ./gradlew gatlingRun

For more information, refer to the [Running tests page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the `src/main/docker` folder to launch required third party services.
For example, to start a mysql database in a docker container, run:

    docker-compose -f src/main/docker/mysql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/mysql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./gradlew bootRepackage -Pprod buildDocker

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`yo jhipster:docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To set up a CI environment, consult the [Setting up Continuous Integration][] page.

[JHipster Homepage and latest documentation]: https://jhipster.github.io
[JHipster 3.10.0 archive]: https://jhipster.github.io/documentation-archive/v3.10.0

[Using JHipster in development]: https://jhipster.github.io/documentation-archive/v3.10.0/development/
[Using Docker and Docker-Compose]: https://jhipster.github.io/documentation-archive/v3.10.0/docker-compose
[Using JHipster in production]: https://jhipster.github.io/documentation-archive/v3.10.0/production/
[Running tests page]: https://jhipster.github.io/documentation-archive/v3.10.0/running-tests/
[Setting up Continuous Integration]: https://jhipster.github.io/documentation-archive/v3.10.0/setting-up-ci/

[Gatling]: http://gatling.io/
[Node.js]: https://nodejs.org/
[Bower]: http://bower.io/
[Gulp]: http://gulpjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
