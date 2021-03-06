/*
	This is the Geb configuration file.

	See: http://www.gebish.org/manual/current/configuration.html
*/


import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions

File findDriverExecutable() {
    return Utils.getDriver()
}

driver = {
    def jutils = new JSONUtils()
    ChromeOptions options = new ChromeOptions()
    options.addExtensions(new File(jutils.getConfig("ANTI_CAPTCHA_EXTENSION_PATH")))
    options.addArguments("test-type")
    options.addArguments("start-maximized")
    options.addArguments("disable-infobars")
    options.addArguments("--disable-web-security")
    options.addArguments("--user-agent=" + jutils.getConfig("USER_AGENT"))
    ChromeDriverService service = new ChromeDriverService.Builder()
            .usingAnyFreePort()
            .usingDriverExecutable(findDriverExecutable())
            .build()
    return new ChromeDriver(service, options)
}


waiting {
    timeout = 60
    retryInterval = 1.0
    includeCauseInMessage = true
}

reportsDir = "target/reports"

baseUrl = "https://www.steinberg.net/forums/ucp.php?mode=register"