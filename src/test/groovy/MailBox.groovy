class MailBox extends geb.Page {

    static at = { assert title.contains("MinuteInbox") }

    static content = {

        email {$("#email")}

    }

}
