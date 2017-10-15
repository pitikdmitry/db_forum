FROM maven:3-ibmjava-8

MAINTAINER Pitik Dmitry

# Обвновление списка пакетов
RUN apt-get -y update

#
# Установка postgresql
#
ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER

# Run the rest of the commands as the ``postgres`` user created by the ``postgres-$PGVER`` package when it was ``apt-get installed``
USER postgres

# Create a PostgreSQL role named ``docker`` with ``docker`` as the password and
# then create a database `docker` owned by the ``docker`` role.
RUN /etc/init.d/postgresql start &&\
    /etc/init.d/postgresql stop
#    psql --command "ALTER USER postgres WITH SUPERUSER PASSWORD 'password';" &&\
#    createdb -O db_tp_proj postgres &&\
#    /etc/init.d/postgresql stop

# Adjust PostgreSQL configuration so that remote connections to the
# database are possible.
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/$PGVER/main/pg_hba.conf

# And add ``listen_addresses`` to ``/etc/postgresql/$PGVER/main/postgresql.conf``
RUN echo "listen_addresses='*'" >> /etc/postgresql/$PGVER/main/postgresql.conf

# Expose the PostgreSQL port
EXPOSE 5432

# Add VOLUMEs to allow backup of config, logs and databases
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

# Back to the root user
USER root

#
# Сборка проекта
#

#RUN apt-get install -y maven

# Копируем исходный код в Docker-контейнер
ENV WORK /project
ADD . $WORK/java-project/
# ADD common/ $WORK/common/

# Собираем и устанавливаем пакет
WORKDIR $WORK/java-project/
RUN mvn package

# Объявлем порт сервера
EXPOSE 5000

#
# Запускаем PostgreSQL и сервер
#
USER postgres

ENV DBHOST=postgres
ENV DBPORT=5432
ENV DBNAME=forum_server
ENV DBUSER=forum_server
ENV DBPASS=forum_server
ENV DATABASE=/tmp/database

CMD service postgresql start && \
    bash $WORK/java-project/database/loader/db_loader.sh $DBHOST $DBPORT $DBNAME $DBUSER $DBPASS $DATABASE && \
    java -Xmx300M -Xmx300M -jar $WORK/java-project/target/DataBaseProject-0.1.0.jar
