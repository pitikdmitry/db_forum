db_host=$1
db_port=$2

db_name=$3
db_user=$4
db_password=$5
db_dir=$6

export PGPASSWORD=postgres

function create_extensions {
#    psql -h ${db_host} -U "postgres" -d ${db_name} -c "CREATE EXTENSION \"${1}\";"
    psql -h ${db_host} -U "forum_user" -d ${db_name} -c "CREATE EXTENSION CITEXT;"
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

create_extensions
load_database_structure
load_database_constraints

