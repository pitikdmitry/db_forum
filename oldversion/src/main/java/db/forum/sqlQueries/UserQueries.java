package db.forum.sqlQueries;

public class UserQueries {

    public static String createUser = "CREATE OR REPLACE FUNCTION createUser(p_about TEXT, p_email TEXT, p_fullname TEXT, p_nickname TEXT)\n" +
            "RETURNS TABLE(user_id INTEGER, about TEXT, email TEXT, fullname TEXT, nickname TEXT)\n" +
            "AS\n" +
            "$BODY$\n" +
            "  BEGIN\n" +
            "  RETURN QUERY\n" +
            "    INSERT INTO userTable (about, email, fullname, nickname)\n" +
            "    VALUES (p_nickname, p_email, p_about, p_fullname)\n" +
            "    RETURNING userTable.user_id, userTable.about, userTable.email, userTable.fullname, userTable.nickname;\n" +
            "  END;\n" +
            "$BODY$\n" +
            "LANGUAGE plpgsql VOLATILE;";

    public static String getCreateUser() { return createUser; }
}
