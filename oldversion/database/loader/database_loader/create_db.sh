db_host=$1
db_port=$2

db_name=$3
db_user=$4
db_password=$5
db_dir=$6

export PGPASSWORD=postgres
export PATH=$PATH:/bin/
PGOPTIONS='--client-min-messages=warning'
export PGOPTIONS

SOURCE="${BASH_SOURCE[0]}"
echo ${SOURCE}
cd -P "$( dirname "$SOURCE" )"

function create_extensions {
    psql -h ${db_host} -U "postgres" -d ${db_name} -c "CREATE EXTENSION \"${1}\";"
}


function re_create_database {
    echo delete from "${db_host}" database ${db_name}
    psql -h ${db_host} -U "postgres" -c "drop database ${1}"
    psql -h ${db_host} -U "postgres" -c "create database ${1}"
    psql -h ${db_host} -U "postgres" -c "create user ${2} with password '${3}'"
    psql -h ${db_host} -U "postgres" -c "grant all privileges on database ${1} to ${2}"
    create_extensions "citext"
    export PGPASSWORD=$db_password
}



function execute_sql_file {
    echo ${1}
    psql -q -h ${db_host} -p ${db_port} -U ${db_user} -d ${db_name} -f "${1}"
}

function execute_folder_recursively {
    if [ -d "${1}" ]
    then
        for file in $(find "${1}");
        do
            if [[ "${file}" == *psql ]];
            then
                execute_sql_file "${file}"
            fi
        done
    fi
}

function load_database_structure {
    echo "load database structure" ${db_dir}
    for dirname in ${db_dir}/*;
    do
        if [ -d "$dirname" ]; then
            execute_folder_recursively "$dirname"/tables
            execute_folder_recursively "$dirname"/functions
        fi
    done
}

function load_database_constraints {
    echo "load database constraints"
    for dirname in ${db_dir}/*;
    do
        if [ -d "$dirname" ]; then
            execute_folder_recursively "$dirname"/constraints
            execute_folder_recursively "$dirname"/indexes
        fi
    done
}

function wait_postgres {
    echo "Waiting postgres to run on ${db_host} ${db_port}..."

    while ! psql -h ${db_host} -U "postgres" -c "SELECT datname FROM pg_database LIMIT 1" >&/dev/null;
    do
      echo "waiting postgres sleep 5sec"
      sleep 5
    done

    echo "Postgres launched"
}


if [[ ${db_host} == postgres* ]] ;
then
    wait_postgres
fi

echo "db_user = ${db_user}; db_password = ${db_password}; db_name = ${db_name}"
re_create_database "${db_name}" "${db_user}" "${db_password}"
load_database_structure
load_database_constraints

