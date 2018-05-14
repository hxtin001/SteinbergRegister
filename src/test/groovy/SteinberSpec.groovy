import geb.spock.GebReportingSpec
import groovy.util.logging.*
import geb.driver.CachingDriverFactory
import org.apache.log4j.Logger
import spock.lang.*

@Log4j
class SteinberSpec extends GebReportingSpec {


    @Shared
            jutils = new JSONUtils()
    @Shared
            mail1 = new MailReader().getMails(jutils.getConfig("MAIL_PATH_1"))
    @Shared
            mail2 = new MailReader().getMails(jutils.getConfig("MAIL_PATH_2"))

    private static final Logger logger = Logger.getLogger("ExternalAppLogger")

    def setup() {
        CachingDriverFactory.clearCache()
        browser.config.autoClearCookies = true
        driver.manage().window().maximize()
    }

    @Unroll
    "Steinberg register"() {

        log.info("---------------------------------------CASE_ID: -----------------------------------------------")
        if (mail1.size() <= 0) {
            log.error("Can not find any email for register")
            System.exit(0)
            assert false
        }
        if (mail2.size() <= 0) {
            log.error("Please check list2mail.txt")
            assert false
        }
        long waitTime = jutils.getConfig("SIGN_UP_WAIT") * 1000

        // Connect to VPN and change ip address
//        Utils.execCmd("\"c:\\Program Files (x86)\\HMA! Pro VPN\\bin\\HMA! Pro VPN.exe\" -changeip")
//        Thread.sleep(25000)

        driver.switchTo().defaultContent()
        when:
        to SteinbergRegisterPage
        log.info("Steinberg opened")

        // Set up anti-captcha
        try {
            String winHandleBefore = driver.getWindowHandle()
            driver.executeScript('''return window.open("google.com", "_blank")''')
            for (String winHandle : driver.getWindowHandles()) {
                if (winHandle != winHandleBefore) {
                    driver.switchTo().window(winHandle)
                    driver.get("chrome-extension://lncaoejhfdpcafpkkcddpjnhnodcajfg/options.html")
                    Thread.sleep(2000)
                    driver.executeScript('''return $("input[name='account_key'").val("f8228269a43caf2e25ebfafef82515dd");''')
                    Thread.sleep(100)
                    driver.executeScript('''return $("#save").click();''')
                    sleep(1000)
                    driver.close()
                }
            }
            driver.switchTo().window(winHandleBefore)
        } catch (Exception e) {

        } catch (org.openqa.selenium.WebDriverException we) {
            log.error("WebDriver exception.")
        }

        then: "Click agree button"
        Utils.clickElement(agreeBtn, "Clicked agree button", true)
        Thread.sleep(5000)

        then: "Input user name"
        String[] mailInfos = mail1.get(0).split("@")
        if (mailInfos.size() > 1) {
            Utils.selectByValue(username, mailInfos[0], "User name")
        } else {
            Utils.selectByValue(username, mail1.get(0), "User name")
        }

        then: "Input email"
        Utils.selectByValue(email, mail1.get(0), "Email")

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
        }

        then: "Input captcha"
        Utils.clickElement(submitBtn, "Clicked submit button", true)
        Thread.sleep(9000000)
        driver.close()
        where:
            domainValue << jutils.get("DOMAIN")
            passwordValue << jutils.get("PASSWORD")
            countryCodeValue << jutils.get("COUNTRY_CODE")
            zipCodeValue << jutils.get("ZIP_CODE")
            genderValue << jutils.get("GENDER")
    }

}