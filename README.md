# jooq-combined

Project to illustrate integrating:

* jOOQ
* Liquibase
* Spring Transactions

Databases used:

* H2
* HyperSQL
* MariaDB
* MySQL
* PostgreSQL

`mvn install`

Spin up a MySQL Docker image:

```
# docker-machine ip default

docker run -p 3306:3306 --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:5.7
sleep 10
docker run -it --link some-mysql:mysql --rm mysql:5.7 sh -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" -P"$MYSQL_PORT_3306_TCP_PORT" -uroot -p"$MYSQL_ENV_MYSQL_ROOT_PASSWORD"'

mysql> CREATE DATABASE `jooq`;
Query OK, 1 row affected (0.00 sec)
mysql> USE `jooq`;
Database changed
```

Create `prod-liquibase.properties`:

```
driver: com.mysql.jdbc.Driver
url: jdbc:mysql://192.168.99.100:3306/jooq
username: root
password: my-secret-pw
changeLogFile: src/main/resources/changelog/jooq.changelog-master.xml
contexts: prod,!test
```

Run from the `jooq-models` directory:

```
mvn liquibase:update -Dliquibase.propertyFileWillOverride=true -Dliquibase.propertyFile=prod-liquibase.properties
```

In the Docker `mysql` command line client:

```
mysql> select * from AUTHOR;
+----+------------+-------------+
| ID | FIRST_NAME | LAST_NAME   |
+----+------------+-------------+
|  1 | Cay        | Horstmann   |
|  2 | Venkat     | Subramaniam |
|  3 | Richard    | Warburton   |
|  4 | Brian      | Goetz       |
+----+------------+-------------+
4 rows in set (0.00 sec)

mysql> select * from BOOK;
+----+-----------+-------------------------------------------------------+----------+
| ID | AUTHOR_ID | TITLE                                                 | LANGUAGE |
+----+-----------+-------------------------------------------------------+----------+
|  1 |         1 | Core Java for the Impatient                           | English  |
|  2 |         1 | Scala for the Impatient                               | English  |
|  3 |         2 | Functional Programming in Java                        | Spanish  |
|  4 |         3 | Java 8 Lambdas: Functional Programming For The Masses | German   |
|  5 |         4 | Java Concurrency in Practice                          | Italian  |
+----+-----------+-------------------------------------------------------+----------+
5 rows in set (0.00 sec)
```
