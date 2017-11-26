FROM ubuntu:16.04

MAINTAINER Pitik Dmitry

RUN apt-get -y update
ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER

# Run the rest of the commands as the ``postgres`` user created by the ``postgres-$PGVER`` package when it was ``apt-get installed``
USER postgres
# Create a PostgreSQL role named ``docker`` with ``docker`` as the password and
# then create a database `docker` owned by the ``docker`` role.
RUN /etc/init.d/postgresql start &&\
    psql --command "CREATE USER forum_user WITH SUPERUSER PASSWORD 'forum_user';" &&\
    /etc/init.d/postgresql stop

RUN echo "local all all trust" >> /etc/postgresql/$PGVER/main/pg_hba.conf
RUN echo "host  all all 127.0.0.1/32 trust" >> /etc/postgresql/$PGVER/main/pg_hba.conf

RUN echo "host  all all ::1/128 trust" >> /etc/postgresql/$PGVER/main/pg_hba.conf
RUN echo "host  all all 0.0.0.0/0 trust" >> /etc/postgresql/$PGVER/main/pg_hba.conf

RUN cat /etc/postgresql/$PGVER/main/pg_hba.conf

RUN echo "listen_addresses='*'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "synchronous_commit = off" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "fsync = off" >> /etc/postgresql/$PGVER/main/postgresql.conf

RUN echo "log_statement = none" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_duration = off " >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_lock_waits = on" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_min_duration_statement = 50" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_filename = 'query.log'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_directory = '/var/log/postgresql'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "log_destination = 'csvlog'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "logging_collector = on" >> /etc/postgresql/$PGVER/main/postgresql.conf
# Expose the PostgreSQL port
EXPOSE 5432
# Add VOLUMEs to allow backup of config, logs and databases
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]
# Back to the root user
USER root
#JDK
RUN apt-get install -y openjdk-8-jdk-headless
#maven
#RUN apt-get install -y maven

ENV WORK /project
ADD . $WORK/java-project/
WORKDIR $WORK/java-project/
#RUN mvn package

# Объявлем порт сервера
EXPOSE 5000

#
# Запускаем PostgreSQL и сервер
#
USER postgres

ENV DBHOST=postgres
ENV DBPORT=5432
ENV DBNAME=forum_user
ENV DBUSER=forum_user
ENV DBPASS=forum_user
ENV DATABASE=$WORK/java-project/database

CMD service postgresql start && psql --command "UPDATE pg_database SET datistemplate = FALSE WHERE datname = 'template1';" && \
     psql --command "DROP DATABASE template1;" && \
     psql --command "CREATE DATABASE template1 WITH TEMPLATE = template0 ENCODING = 'UNICODE';" && \
     psql --command "UPDATE pg_database SET datistemplate = TRUE WHERE datname = 'template1';" && \
     psql --command "\c template1" && \
     psql --command "VACUUM FREEZE;" && \
     psql --command "CREATE DATABASE forum_user WITH ENCODING 'UTF8';" && \
     psql -f $WORK/java-project/database/loader/database_loader/create_sql.sql forum_user postgres && \
     java -jar $WORK/java-project/target/DataBaseProject-1.0.jar
