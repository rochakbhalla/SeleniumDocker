package com.rochak.dockertest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.FileNotFoundException;
import java.net.URL;

import com.rochak.dockermain.DockerMain;


public class DockerTests {

	DockerMain etm = new DockerMain();	

	@BeforeTest
	public void setUp() throws FileNotFoundException, InterruptedException {
		etm.verifyImage();
	}

	@Test(priority = 0, enabled = false)
	public void testStartContianer() {

		etm.startContainer();
	}

	@Test(priority = 0, enabled = true)
	public void testStartContianerWithDebugPorts() {

		
		etm.startContainerWithDebugPorts();
	}

	@Test(priority = 1)
	public void testBrowserTest() throws Exception {
		DesiredCapabilities dcap = DesiredCapabilities.chrome();

        // Check the Port No here.
        URL selenium_standalone_host = new URL("http://0.0.0.0:32769/wd/hub");
       
        WebDriver driver = new RemoteWebDriver(selenium_standalone_host, dcap);
					Thread.sleep(20000);
        			System.out.println("****** Test Started ******");
        		   driver.get("http://www.spicejet.com");
        		  //driver.manage().window().maximize();
        		   Select s = new Select(driver.findElement(By.name("ctl00$mainContent$DropDownListCurrency")));
        		   s.selectByValue("USD");
        		   s.selectByIndex(2);
        		   s.selectByVisibleText("USD");
        		   driver.findElement(By.xpath(("//input[@name='ctl00_mainContent_ddl_originStation1_CTXT']"))).click();
        		   driver.findElement(By.xpath(("//a[@value='DHM']"))).click();
        		   Thread.sleep(60);
        		   driver.findElement(By.xpath("(//a[@value='GOI'])[2]")).click();
        		   System.out.println(driver.findElement(By.id("ctl00_mainContent_chk_friendsandfamily")).isSelected());
        		   Thread.sleep(1000);
        		   driver.findElement(By.id("ctl00_mainContent_chk_friendsandfamily")).click();
        		   System.out.println(driver.findElement(By.id("ctl00_mainContent_chk_friendsandfamily")).isSelected());
        		   Thread.sleep(1000);
        		   driver.findElement(By.id("ctl00_mainContent_chk_StudentDiscount")).click();
        		   driver.close();
        		   driver.quit();
        		   System.out.println("**** This is end of the test ****");
	}

	
	@AfterTest
	public void tearDown() {
		etm.stopContainer();
	}
	
}
