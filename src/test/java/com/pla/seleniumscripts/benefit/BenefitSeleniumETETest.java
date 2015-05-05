/*
 * Copyright (c) 3/31/15 12:01 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.seleniumscripts.benefit;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BenefitSeleniumETETest {

    private String baseUrl;
    private WebDriver driver;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    public String benefitName;
    Random random = new Random();
    String randomString = "Benefit"+random.nextInt(1000);
    @Before
    public void setUp() throws Exception {

        // Setup firefox binary to start in Xvfb
        String Xport = System.getProperty("lmportal.xvfb.id", ":1");
        final File firefoxPath = new File(System.getProperty("lmportal.deploy.firefox.path", "/usr/bin/firefox"));
        FirefoxBinary firefoxBinary = new FirefoxBinary(firefoxPath);
        firefoxBinary.setEnvironmentProperty("DISPLAY", Xport);
        driver = new FirefoxDriver(firefoxBinary, null);
        //driver =new FirefoxDriver();
		baseUrl = "http://5.9.249.195:9090";
        System.out.println("******************************Navigating to URL******************");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(baseUrl + "/myportal/control/main");
        driver.findElement(By.name("USERNAME")).clear();
        driver.findElement(By.name("USERNAME")).sendKeys("admin");
        driver.findElement(By.name("PASSWORD")).clear();
        driver.findElement(By.name("PASSWORD")).sendKeys("ofbiz");
        driver.findElement(By.cssSelector("input[type=\"image\"]")).click();
        System.out.println("***************Login Sccessfully****************");
        driver.findElement(By.xpath("//li[5]/a/b")).click();
        System.out.println(driver.getCurrentUrl());
        System.out.println("***************Insurance****************");
        driver.navigate().to("http://5.9.249.195:6443/pla/core/benefit/listbenefit");
        System.out.println(driver.getCurrentUrl());

    }



    @Test()
    public void createBenefit() throws Exception {
        System.out.println("***************Create button****************");
        driver.findElement(By.xpath("(//button[@type='button'])[7]")).click();
        System.out.println(driver.getCurrentUrl());
        Thread.sleep(1000);
        System.out.println("***************Create Benefit****************");
        driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).clear();
        driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).sendKeys(randomString);
        driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button")).click();

        try {
            System.out.println("Verify Success Message");
            Thread.sleep(1000);
            assertEquals("Benefit created successfully", driver.findElement(By.xpath("//form[@id='createBenefit']/div/div")).getText());
        } catch (Error e) {
            verificationErrors.append(e.toString());
        }
        //Click Done button
        driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button[2]")).click();
            System.out.println("***************Create Second Benefit****************");
            driver.findElement(By.xpath("(//button[@type='button'])[7]")).click();
            System.out.println(driver.getCurrentUrl());
            Thread.sleep(1000);
            System.out.println("***************Create Benefit****************");
            driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).clear();
            driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).sendKeys(randomString+"_007");
            driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button")).click();

            try {
                System.out.println("Verify Success Message");
                Thread.sleep(1000);
                assertEquals("Benefit created successfully", driver.findElement(By.xpath("//form[@id='createBenefit']/div/div")).getText());
            } catch (Error e) {
                verificationErrors.append(e.toString());
            }
            //Click Done button
            driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button[2]")).click();
        System.out.println("***************Create Benefit Duplicate****************");
        driver.findElement(By.xpath("(//button[@type='button'])[7]")).click();
        System.out.println(driver.getCurrentUrl());
        Thread.sleep(1000);
        System.out.println("***************Create Benefit****************");
        driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).clear();
        driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).sendKeys(randomString + "_007");
        driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button")).click();

        try {
            System.out.println("Verify Success Message");
            Thread.sleep(500);
            assertEquals("Benefit already described", driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[2]")).getText());
        } catch (Error e) {
            verificationErrors.append(e.toString());
        }
        //Click Done button
        driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button[2]")).click();
           System.out.println("****Update Functionality*****");
        System.out.println("SEARCH BENEFIT......");
        Thread.sleep(5000);
        driver.findElement(By.xpath("//div[@id='benefit-table_filter']/label/input")).sendKeys(randomString);
        //Click Update button of benefit
        driver.findElement(By.xpath("//table[@id='benefit-table']/tbody/tr/td[2]/button")).click();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        //Type-in updated benefit name in Update screen
        driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).clear();
        driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).sendKeys(randomString + "_123");
        driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button")).click();
        try {
            Thread.sleep(1000);
            assertEquals("Benefit updated successfully", driver.findElement(By.xpath("//form[@id='createBenefit']/div/div")).getText());
        } catch (Error e) {
            verificationErrors.append(e.toString());
        }
        //Click on Done button
        driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button[2]")).click();
        System.out.println("****Update Functionality Duplicate*****");
        System.out.println(driver.getCurrentUrl());
        System.out.println("SEARCH BENEFIT......");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//div[@id='benefit-table_filter']/label/input")).sendKeys(randomString+"_007");
        //Click Update button of benefit
        driver.findElement(By.xpath("//table[@id='benefit-table']/tbody/tr/td[2]/button")).click();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        //Type-in updated benefit name in Update screen
        driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).clear();
        driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[3]/div/input")).sendKeys(randomString + "_123");
        driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button")).click();
        try {
            Thread.sleep(1000);
            assertEquals("Benefit already described", driver.findElement(By.xpath("//form[@id='createBenefit']/div/div[2]")).getText());
        } catch (Error e) {
            verificationErrors.append(e.toString());
        }
        //Click on Done button
        driver.findElement(By.xpath("//form[@id='createBenefit']/div[2]/button[2]")).click();
    }


    @After
    public void tearDown() throws Exception {
       driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }
}
