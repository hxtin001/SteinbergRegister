import geb.spock.GebReportingSpec
import groovy.util.logging.*
import geb.driver.CachingDriverFactory
import org.apache.log4j.Logger
import spock.lang.*

@Log4j
class SteinberSpec extends GebReportingSpec {


    @Shared
            jutils = new JSONUtils()

    private static final Logger logger = Logger.getLogger("ExternalAppLogger")

    def setup() {
        CachingDriverFactory.clearCache()
        browser.config.autoClearCookies = true
        driver.manage().window().maximize()
    }

    @Unroll
    "Steinberg register"() {

        log.info("---------------------------------------CASE_ID: -----------------------------------------------")

        // Connect to VPN and change ip address
//        Utils.execCmd("\"c:\\Program Files (x86)\\HMA! Pro VPN\\bin\\HMA! Pro VPN.exe\" -changeip")
//        Thread.sleep(25000)

        driver.switchTo().defaultContent()
        when:
        to SteinbergRegisterPage
        log.info("Steinberg opened")

        then: "Click agree button"
        Utils.clickElement(agreeBtn, "Clicked agree button", true)
        Thread.sleep(1000)

        String mailValue = ""
        String winHandleBefore = driver.getWindowHandle()
        // Set up anti-captcha and create mail box
        try {
            driver.executeScript('''return window.open("google.com", "_blank")''')
            for (String winHandle : driver.getWindowHandles()) {
                if (winHandle != winHandleBefore) {
                    driver.switchTo().window(winHandle)
                    driver.get("chrome-extension://lncaoejhfdpcafpkkcddpjnhnodcajfg/options.html")
                    Thread.sleep(2000)
                    driver.executeScript('''return $("input[name='account_key'").val("''' + jutils.getConfig("ANTI_CAPTCHA_KEY") + '''");''')
                    Thread.sleep(100)
                    driver.executeScript('''return $("#save").click();''')
                    sleep(1000)

                    driver.get(mailBox)
                    Thread.sleep(5000)
                    mailValue = $("#email").text()
                    if (mailValue.isEmpty()) {
                        log.error("Cannot create email at ${mailBox}")
                        assert false
                    }
                    log.info("At mail box page")
                    log.info("Email: ${mailValue}")
                }
            }
            driver.switchTo().window(winHandleBefore)
        } catch (Exception e) {
            log.error(e.getMessage(), e)
            driver.close()
        } catch (org.openqa.selenium.WebDriverException we) {
            log.error("WebDriver exception.")
            driver.close()
        }

        then: "Input user name"
        String[] mailInfos = mailValue.split("@")
        String usernameValue = mailInfos[0].replaceAll("\\.", "${Utils.randomInt(999, 1)}")
        if (mailInfos.size() > 1) {
            Utils.selectByValue(username, usernameValue, "User name")
        }

        then: "Input email"
        Utils.selectByValue(email, mailValue, "Email")

        then: "Input password"
        Utils.selectByValue(password, passwordValue, "Password")

        then: "Confirm password"
        Utils.selectByValue(confirmPassword, passwordValue, "Confirm password")
        Thread.sleep(1000)

        String captchaStatus = $("div.antigate_solver a.status").text()
        int counter = 0
        if (captchaStatus != null) {
            while(!captchaStatus.isEmpty() && (counter < 50)) {
                log.info("Captcha status: ${captchaStatus}")
                if (captchaStatus.contains("Solving")) {
                    Thread.sleep(2000 )
                    captchaStatus = $(".antigate_solver a.status").text()
                } else if (captchaStatus.contains("Outdated")) {
                    $("div.antigate_solver a.control.reload").click()
                    Thread.sleep(3000)
                    captchaStatus = $("div.antigate_solver a.status").text()
                } else {
                    break
                }
                counter++
            }
            if (counter == 50) {
                log.error("Cannot resolve captcha")
                driver.close()
                assert false
            }
        }

        then: "Input captcha"
        Utils.clickElement(submitBtn, "Clicked submit button", true)
        Thread.sleep(7000)

        String errMsg = $("#register dl dd.error").text()
        log.info("Message: ${errMsg}")

        when: "At information page"
        at InformationPage
        log.info("At information page")
        String info = $("#message p").text()
        String message
        String verifyUrl
        if (info.contains("created")) {
            log.info(info)
            for (String winHandle : driver.getWindowHandles()) {
                if (winHandle != winHandleBefore) {
                    driver.switchTo().window(winHandle)
                    driver.navigate().refresh()
                    Thread.sleep(3000)
                    $("#schranka td.from")[0].click()
                    driver.switchTo().frame("iframeMail")
                    message = $("body").text()
                    log.info("Email: ${message}")
                    if (message.contains("Welcome to www.steinberg.net")) {
                        def matcher = message =~ /(account: https:\/\/)(.+\/)+(.+\?)(\w+=\w+&?)+/
                        if (matcher.find()) {
                            verifyUrl = matcher.group().replace("account: ", "")
                            log.info("Verify url: ${verifyUrl}")
                        } else {
                            log.error("Cannot found verify url in email from steinberg.net")
                        }
                    } else {
                        log.error("Cannot found email from steinberg.net")
                    }
                }
            }
        }
        then: "Go to verify url"
        driver.get(verifyUrl)
        Thread.sleep(10000)
        Utils.selectByValue($("#username"), usernameValue, "Login with username")
        Thread.sleep(500)
        Utils.selectByValue($("#password"), passwordValue, "Login with password")
        Utils.clickElement($("input[type='submit']"), "Clicked button login", true)
        Thread.sleep(7000)

        when: "At steinberg forum"
        at SteinbergForum
        log.info("At steinberg forum")
        // Go to steinberg profile page
        then: "Go to edit profile page"
        driver.get("https://www.steinberg.net/forums/ucp.php?i=ucp_profile&mode=profile_info")
        Thread.sleep(7000)

        when: "At edit profile page"
        at ProfilePage
        log.info("At edit profile page")
        Utils.selectByValue($("#pf_phpbb_website"), profileWebsite, "Profile website")
        Utils.clickElement($("input[type='submit']"), "Clicked button submit", true)
        Thread.sleep(10000)
        /**
         * Welcome to www.steinberg.net forums Please keep this email for your records. Your account information is as follows: ---------------------------- Username: elyna203mariajose Board URL: https://www.steinberg.net/forums ---------------------------- Please visit the following link in order to activate your account: https://www.steinberg.net/forums/ucp.php?mode=activate&u=109699&k=1EYOAM2P Your password has been securely stored in our database and cannot be retrieved. In the event that it is forgotten, you will be able to reset it using the email address associated with your account. Thank you for registering. -- Steinberg Media Technologies GmbH, Beim Strohhause 31,20097 Hamburg, Germany Phone: +49 (40) 21035-0 | www.steinberg.net President: Andreas Stelling Managing Director: Thomas Schöpe, Yoshiyuki Tsugawa
         */
        then: "Close"
        Thread.sleep(2000)
        driver.quit()

        where:
            passwordValue << jutils.get("PASSWORD")
            mailBox << jutils.get("MAIL_BOX")
            profileWebsite << jutils.get("PROFILE_WEBSITE")
    }

}