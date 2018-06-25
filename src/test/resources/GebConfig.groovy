/*
	This is the Geb configuration file.

	See: http://www.gebish.org/manual/current/configuration.html
*/


//import org.openqa.selenium.chrome.ChromeDriver
//import org.openqa.selenium.chrome.ChromeDriverService
//import org.openqa.selenium.chrome.ChromeOptions

import org.openqa.selenium.firefox.FirefoxBinary
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.remote.DesiredCapabilities

File findDriverExecutable() {
    return Utils.getDriver()
}

driver = {
    // Chrome
    def jutils = new JSONUtils()
//    UserAgentReader usrAgentReader = new UserAgentReader()
//    ArrayList<String> agents = usrAgentReader.getAgents("User-agent.txt")
//
//    ChromeOptions options = new ChromeOptions()
//    options.addExtensions(new File(jutils.getConfig("ANTI_CAPTCHA_EXTENSION_PATH")))
//    options.addExtensions(new File(jutils.getConfig("VPN_EXTENSION_PATH")))
//    options.addArguments("test-type")
//    options.addArguments("start-maximized")
//    options.addArguments("disable-infobars")
////    options.addArguments("--disable-web-security")
//    println("--user-agent=" + agents.get(Utils.randomInt(agents.size(), 0)))
//    options.addArguments("--user-agent=" + agents.get(Utils.randomInt(agents.size(), 0)))
//    ChromeDriverService service = new ChromeDriverService.Builder()
//            .usingAnyFreePort()
//            .usingDriverExecutable(findDriverExecutable())
//            .build()
//    return new ChromeDriver(service, options)

    // Firefox
    System.setProperty("webdriver.gecko.driver", jutils.getConfig("GOCKO_DRIVER"))
    File pathBinary = new File(jutils.getConfig("FIREFOX"))
    FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary)

    // Add on
//    File vnpAddon = new File(jutils.getConfig("VPN_EXTENSION_PATH"))
//    FirefoxProfile firefoxProfile = new FirefoxProfile()
//    firefoxProfile.addExtension(vnpAddon)

    // Firefox options
    FirefoxOptions options = new FirefoxOptions()
//    options.setProfile(firefoxProfile)

    DesiredCapabilities desired = DesiredCapabilities.firefox()
    desired.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options.setBinary(firefoxBinary))

    return new FirefoxDriver(options)
}


waiting {
    timeout = 60
    retryInterval = 1.0
    includeCauseInMessage = true
}

reportsDir = "target/reports"

baseUrl = "https://www.google.com/"