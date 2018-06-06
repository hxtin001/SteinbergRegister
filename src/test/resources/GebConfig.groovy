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
    UserAgentReader usrAgentReader = new UserAgentReader()
    ArrayList<String> agents = usrAgentReader.getAgents("User-agent.txt")

    ChromeOptions options = new ChromeOptions()
    options.addExtensions(new File(jutils.getConfig("ANTI_CAPTCHA_EXTENSION_PATH")))
    options.addExtensions(new File(jutils.getConfig("VPN_EXTENSION_PATH")))
    options.addArguments("test-type")
    options.addArguments("start-maximized")
    options.addArguments("disable-infobars")
//    options.addArguments("--disable-web-security")
    print("--user-agent=" + agents.get(Utils.randomInt(agents.size(), 0)))
    options.addArguments("--user-agent=" + agents.get(Utils.randomInt(agents.size(), 0)))
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

baseUrl = "https://www.google.com/"