import geb.spock.GebReportingSpec
import groovy.util.logging.*
import geb.driver.CachingDriverFactory
import org.apache.log4j.Logger
import org.openqa.selenium.Keys
import spock.lang.*

@Log4j
class SEOSpec extends GebReportingSpec {


    @Shared
            jutils = new JSONUtils()

    private static final Logger logger = Logger.getLogger("ExternalAppLogger")

    def setup() {
        CachingDriverFactory.clearCache()
        browser.config.autoClearCookies = true
        driver.manage().window().maximize()
    }

    @Unroll
    "SEO tool"() {

        log.info("---------------------------------------CASE_ID: -----------------------------------------------")
        def vpnWait = jutils.getConfig("VPN_WAIT") * 1000

        // Connect to VPN and change ip address
        if (jutils.getConfig("IS_USE_HMA")) {
            Utils.execCmd(jutils.getConfig("HMA_PATH") + " -changeip")
            Thread.sleep(vnpWait)
        }

        driver.switchTo().defaultContent()
        when:
        to GoogleSearchPage
        log.info("At google search page")

        String winHandleBefore = driver.getWindowHandle()
        Thread.sleep(4000)
        for (String winHandle : driver.getWindowHandles()) {
            if (winHandle != winHandleBefore) {
                driver.switchTo().window(winHandle)
                driver.close()
            }
        }
        driver.switchTo().window(winHandleBefore)

        Thread.sleep(2000)
        try {
            driver.executeScript('''return window.open("", "_blank")''')
            for (String winHandle : driver.getWindowHandles()) {
                if (winHandle != winHandleBefore) {
                    driver.switchTo().window(winHandle)
                    driver.get("chrome-extension://lncaoejhfdpcafpkkcddpjnhnodcajfg/options.html")
                    Thread.sleep(2000)
                    driver.executeScript('''return $("input[name='account_key'").val("''' + jutils.getConfig("ANTI_CAPTCHA_KEY") + '''");''')
                    Thread.sleep(100)
                    driver.executeScript('''return $("#save").click();''')
                    Thread.sleep(2000)
                    driver.close()
                }
            }
            driver.switchTo().window(winHandleBefore)
        } catch (Exception e) {
            log.error(e.getMessage(), e)
        } catch (org.openqa.selenium.WebDriverException we) {
            log.error("WebDriver exception.")
        }

        try {
            driver.executeScript('''return window.open("", "_blank")''')
            for (String winHandle : driver.getWindowHandles()) {
                if (winHandle != winHandleBefore) {
                    driver.switchTo().window(winHandle)
                    driver.get("chrome-extension://hnmpcagpplmpfojmgmnngilcnanddlhb/html/reactPopUp.html")
                    $("input[placeholder='Username']").value(jutils.getConfig("VPN_EXTENSION_USERNAME"))
                    $("input[placeholder='Password']").value(jutils.getConfig("VPN_EXTENSION_PASS"))
                    $("button").click()
                    Thread.sleep(vpnWait)
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e)
        } catch (org.openqa.selenium.WebDriverException we) {
            log.error("WebDriver exception.")
        }
        driver.switchTo().window(winHandleBefore)

        Thread.sleep(10000)
        for (String winHandle : driver.getWindowHandles()) {
            if (winHandle != winHandleBefore) {
                driver.switchTo().window(winHandle)
                driver.close()
            }
        }
        driver.switchTo().window(winHandleBefore)

        try {
            driver.executeScript('''return window.open("", "_blank")''')
            for (String winHandle : driver.getWindowHandles()) {
                if (winHandle != winHandleBefore) {
                    driver.switchTo().window(winHandle)
                    driver.get("chrome-extension://hnmpcagpplmpfojmgmnngilcnanddlhb/html/reactPopUp.html")
                    Thread.sleep(3000)
                    $("a.locationChooser").click()
                    Thread.sleep(2000)
                    def locationElements = $("#locations .allowed")
                    def randInt = Utils.randomInt(4, 0)
                    log.info("Location: ${locationElements[randInt]}")
                    locationElements[randInt].click()
                    Thread.sleep(3000)
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e)
        } catch (org.openqa.selenium.WebDriverException we) {
            log.error("WebDriver exception.")
        }

        driver.switchTo().window(winHandleBefore)
        Thread.sleep(3000)
        for (String winHandle : driver.getWindowHandles()) {
            if (winHandle != winHandleBefore) {
                driver.switchTo().window(winHandle)
                driver.close()
            }
        }
        driver.switchTo().window(winHandleBefore)
        driver.navigate().refresh()

        Thread.sleep(7000)
        $("input[name='q']").value(keySearchVals.get(Utils.randomInt(keySearchVals.size(), 0)) + Keys.ENTER)

        Thread.sleep(7000)
        def urlElements = $("div#ires .g h3 a")
        int size = urlElements.size()
        boolean flag = false
        for(int i = 0; i < size; i++) {
            def text = urlElements[i].getAttribute("href")
            if (text.contains(urlVal)) {
                log.info("Link: ${text}")
                urlElements[i].click()
                flag = true
                break
            }
        }
        if (!flag) {
            log.info("Go to page 2")
            $("#foot td a")[1].click()
            Thread.sleep(3000)
            urlElements = $("div#ires .g h3 a")
            size = urlElements.size()
            if (size <= 0) {
                log.error("Cannot find any element: div#ires .g h3 a")
                assert false
            }
            for(int i = 0; i < size; i++) {
                def text = urlElements[i].getAttribute("href")
                if (text.contains(urlVal)) {
                    log.info("Link: ${text}")
                    urlElements[i].click()
                    break
                }
            }
        }

        then: "Close"
        Thread.sleep(7000)
        driver.executeScript('''return setTimeout(function () {
            window.scrollTo(0, 900);
        },5);''')
        Thread.sleep(3000)
        driver.executeScript('''return setTimeout(function () {
            window.scrollTo(900, 0);
        },5);''')
        cssSelectors.each {
            try {
                driver.executeScript('''document.querySelector("''' + it + '''").click();''')
            } catch (Exception e) {
                log.error(e.getMessage(), e)
            } catch (org.openqa.selenium.WebDriverException we) {
                log.error("WebDriver exception.")
            }
        }
        Thread.sleep(jutils.getConfig("WAIT_AT_PAGE") * 1000)
        driver.executeScript('''return setTimeout(function () {
            window.scrollTo(0, 900);
        },5);''')
        Thread.sleep(3000)
        driver.executeScript('''return setTimeout(function () {
            window.scrollTo(900, 0);
        },5);''')
        Thread.sleep(5000)
        driver.quit()

        where:
            keySearchVals << jutils.get("KEY_SEARCH")
            urlVal << jutils.get("URL")
            cssSelectors << jutils.get("LINK_CSS_SELECTOR")
    }

}