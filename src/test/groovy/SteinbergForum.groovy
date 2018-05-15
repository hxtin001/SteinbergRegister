class SteinbergForum extends geb.Page {

    static at = { assert title.contains("Forums") }

    static content = {

        username {$("li#username_logged_in span.username")}

        profile {$("div.dropdown a[title='Profile']")}

    }

}
