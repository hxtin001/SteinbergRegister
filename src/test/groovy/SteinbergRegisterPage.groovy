class SteinbergRegisterPage extends geb.Page {

    static at = { assert title.contains("Register") }

    static content = {

        agreeBtn {$("#agreed")}

        username {$("#username")}

        email {$("#email")}

        password {$("#new_password")}

        confirmPassword {$("#password_confirm")}

        lang {$("select#lang")}

        timezoneDate {$("#tz_date")}

        timezone {$("#timezone")}

        submitBtn {$("#submit")}

    }
}
