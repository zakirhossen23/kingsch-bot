package kingsch.bot;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StartAccount extends Thread {

    private static ChromeDriver driver;
    String email = "";
    String name = "";
    String countrycode = "";
    String Pnumber = "";
    String Countryname = null;
    String typeRun = "";
    private boolean done = false;

    public static void stopfile() {
        driver.quit();

    }

    public static void downloadFile(URL url, String outputFileName) throws IOException {
        try ( InputStream in = url.openStream();  ReadableByteChannel rbc = Channels.newChannel(in);  FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private static void unzip(Path source, Path target) throws IOException {

        try ( ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = zipEntry.getName().endsWith(File.separator);
                // example 1.1
                // some zip stored files and folders separately
                // e.g data/
                //     data/folder/
                //     data/folder/file.txt

                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    java.nio.file.Files.createDirectories(newPath);
                } else {

                    // example 1.2
                    // some zip stored file path only, need create parent directories
                    // e.g data/folder/file.txt
                    if (newPath.getParent() != null) {
                        if (java.nio.file.Files.notExists(newPath.getParent())) {
                            java.nio.file.Files.createDirectories(newPath.getParent());
                        }
                    }

                    // copy files, nio
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);

                    zis.close();
                    // copy files, classic
                    /*try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }*/
                }

                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();

        }

    }

    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
            throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());
        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }

    private String textTocapitalize(String message) {
        // stores each characters to a char array
        char[] charArray = message.toCharArray();
        boolean foundSpace = true;

        for (int i = 0; i < charArray.length; i++) {

            // if the array element is a letter
            if (Character.isLetter(charArray[i])) {

                // check space is present before the letter
                if (foundSpace) {

                    // change the letter into uppercase
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                } else {
                    // change the letter into toLowerCase
                    charArray[i] = Character.toLowerCase(charArray[i]);
                }
            } else {
                // if the new character is not character
                foundSpace = true;
            }
        }
        message = String.valueOf(charArray);
        return message;
    }

    //------------------------------------------------------------------------------Captcha Solving------------------------------------------------------------
    private void phonecaptchasolving(ChromeDriver driver) throws InterruptedException {
        Allwork:
        while (true) {
            try {
                String value1 = driver.getPageSource();
                if (value1.contains("We have sent")) {
                    break Allwork;
                }
            } catch (Exception e) {
            }
        }
    }

    private void captchasolving(ChromeDriver driver) throws InterruptedException {
        Allwork:
        while (true) {           //clicking solver
            KingschAccount.StatusLBL.setText("Verifying..");

            driver.switchTo().defaultContent();
            String value5 = driver.getPageSource();
            if (value5.contains("We have sent you a")) {
                break Allwork;

            }
        }
    }

    //------------------------------------------------------------------------------Fill UP signup------------------------------------------------------------
    private void fillupforphonesignup(WebDriver driver) {
        driver.findElement(By.xpath("//*[@class='CountrySelect__wrapper']/div")).click();

        String searchingCountry = "//*[@class=\"CountrySelect__option\"]/span";
        try {
            restart:
            while (true) {
                Countryname = textTocapitalize(Countryname);
                driver.findElement(By.xpath("//*[contains(text(),'" + Countryname + "')]/parent::*/*[contains(text(),'" + countrycode + "')]/parent::*")).click();
                //driver.findElement(By.xpath("//*[contains(text(),'" + Countryname + "')]")).click();
                break;
            }

        } catch (Exception e) {
        }

        driver.findElement(By.xpath("//*[@name=\"phone_number\"]")).sendKeys(Pnumber.replace("+", ""));
        driver.findElement(By.xpath("//*[@class=\"form-actions\"]")).click(); //*[@class="CountrySelect__option"]/span[contains(text(),'Malay')][contains(text(),'')]

    }

    private void fillupforsignup(WebDriver driver) {

        driver.findElement(By.xpath("//*[@name='email']")).sendKeys(email);
        driver.findElement(By.xpath("//*[@name='name']")).sendKeys(name);
        Random r = new Random();
        int low = 1950;
        int high = 3000;
        int x = r.nextInt(high - low) + low;
        driver.findElement(By.xpath("//*[@name='username']")).sendKeys(name.replaceAll("\\s+", "") + x);
        driver.findElement(By.xpath("//*[@name='password']")).sendKeys("Q123456789");
        driver.findElement(By.xpath("//*[@class='submit-btn']")).click();

    }

    //------------------------------------------------------------------------------Get Email/Name------------------------------------------------------------
    private void getemail(WebDriver driver) {
        while (true) {
            try {
                String result = driver.findElement(By.id("address")).getAttribute("value");
                if (!result.contains("..")) {
                    email = result;
                    break;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void getemailGmail(WebDriver driver) {
        while (true) {
            try {
                ((JavascriptExecutor) driver).executeScript("document.getElementById('option-3').click()");
                ((JavascriptExecutor) driver).executeScript("document.getElementById('generate_button').click()");
                Thread.sleep(1500);
                String result = driver.findElement(By.id("email_address")).getAttribute("value");
                if (result.contains("@")) {
                    email = result;
                    driver.findElement(By.id("button_go")).click();
                    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                    break;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        driver.manage().timeouts().implicitlyWait(35, TimeUnit.SECONDS);
    }

    private void getname(WebDriver driver) {
        name = driver.findElement(By.xpath("(//*[@class=\"nameList\"]/li)[1]")).getText();
    }

    //---------------------------------------------------------------------------------Verify------------------------------------------------------------
    private void verfiyaccountPhone(WebDriver driver) {   //🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏
        ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs2.get(0));

        driver.switchTo().defaultContent();

        Again:
        while (true) {
            try {
                driver.switchTo().frame(0);
            } catch (Exception e) {
            }
            try {
                driver.findElement(By.xpath("(//*[@aria-current=\"page\"])[3]")).click();
                Thread.sleep(200);
                break;
            } catch (Exception e) {
            }
        }
        while (true) {
            try {
                driver.switchTo().defaultContent();
            } catch (Exception e) {
            }
            try {
                try {
                    driver.switchTo().defaultContent();
                } catch (Exception e) {
                }
                driver.findElement(By.xpath("//*[@id=\"__layout\"]/div/div[2]/main/div/div[2]/ul/li/a/div")).click();   //click mail
                break;
            } catch (Exception e) {
                try {
                    driver.switchTo().frame(0);
                } catch (Exception e2) {
                }
                try {
                    driver.findElement(By.xpath("(//*[@aria-current=\"page\"])[3]")).click();
                } catch (Exception e23) {
                }
            }
        }
        driver.switchTo().defaultContent();
        try {
            driver.switchTo().frame(0);
        } catch (Exception e) {
        }

        while (true) {
            try {
                driver.switchTo().defaultContent();

                driver.switchTo().frame(0);

                driver.navigate().to(driver.findElement(By.xpath("//*[contains(text(),'verify_email?')]")).getAttribute("href"));  //go to verification
                break;
            } catch (Exception e) {

            }

        }
    }

    private void verfiyaccount(WebDriver driver) {
        try {
            if (typeRun != "gmail") {
                verfiyaccountTEMP(driver);
            } else {
                verfiyaccountGMAIL(driver);
            }
        } catch (Exception ex) {

        }
    }

    private void verfiyaccountTEMP(WebDriver driver) {
        driver.close();
        ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs2.get(0));

        driver.switchTo().defaultContent();

        Again:
        while (true) {
            try {
                driver.switchTo().frame(0);
            } catch (Exception e) {
            }
            try {
                driver.findElement(By.xpath("(//*[@aria-current=\"page\"])[3]")).click();
                Thread.sleep(200);
                break;
            } catch (Exception e) {
            }
        }
        while (true) {
            try {
                driver.switchTo().defaultContent();
            } catch (Exception e) {
            }
            try {
                try {
                    driver.switchTo().defaultContent();
                } catch (Exception e) {
                }
                driver.findElement(By.xpath("//*[@id=\"__layout\"]/div/div[2]/main/div/div[2]/ul/li/a/div")).click();   //click mail
                break;
            } catch (Exception e) {
                try {
                    driver.switchTo().frame(0);
                } catch (Exception e2) {
                }
                try {
                    driver.findElement(By.xpath("(//*[@aria-current=\"page\"])[3]")).click();
                } catch (Exception e23) {
                }
            }
        }
        driver.switchTo().defaultContent();
        try {
            driver.switchTo().frame(0);
        } catch (Exception e) {
        }

        while (true) {
            try {
                driver.switchTo().defaultContent();

                driver.switchTo().frame(0);

                driver.navigate().to(driver.findElement(By.xpath("//*[contains(text(),'verify_email?')]")).getAttribute("href"));  //go to verification
                break;
            } catch (Exception e) {

            }

        }
    }

    private void verfiyaccountGMAIL(WebDriver driver) {
        try {
            Thread.sleep(1500);
            driver.close();
            ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
            driver.switchTo().window(tabs2.get(0));
            Thread.sleep(1000);
            driver.switchTo().defaultContent();
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            driver.navigate().refresh();
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            while (true) {
                try {
                    String result = driver.findElement(By.xpath("//a[contains(string(),'Kings')]")).getAttribute("href");
                    if (result.contains("https://")){
                     driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                        driver.navigate().to(result);
                }
                    break;
                } catch (Exception e) {
                }
                   ((JavascriptExecutor)driver).executeScript("document.getElementById('button_reload').click()");   
                Thread.sleep(200);
            }


            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            while (true) {
                driver.switchTo().frame(3);
                try {
                   String result =  new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'verify_email')]"))).getAttribute("href");
                  if (result.contains("https://")) {
                        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                        driver.navigate().to(result);
                        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                        break;
                    }
                } catch (Exception e) {
                }
                
            }
            driver.switchTo().defaultContent();
        } catch (InterruptedException ex) {
            Logger.getLogger(StartAccount.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    //---------------------------------------------------------------------------------Login------------------------------------------------------------
    private void login(WebDriver driver) {
        driver.findElement(By.xpath("//*[@placeholder=\"Username, Phone Number or E-mail\"]")).sendKeys(email);
        driver.findElement(By.xpath("//*[@placeholder=\"Password\"]")).sendKeys("Q123456789");
        driver.findElement(By.xpath("(//*[@class=\"submit-btn\"])[1]")).click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
        }
    }

    //-------------------------------------------------------------------------------Save data------------------------------------------------------------
    private void savedata() {
        try {

            FileWriter writer = new FileWriter("C:\\Program Files\\Common Files\\CSVS\\Account.csv", true);
            BufferedWriter Bwriter = new BufferedWriter(writer);
            PrintWriter Pwriter = new PrintWriter(Bwriter);
            Pwriter.println(email + "," + "Q123456789" + "," + name + "," + name.replaceAll("\\s+", "") + ",");
            Pwriter.flush();
            Pwriter.close();
            System.out.println("saved");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //---------------------------------------------------------------------------------Email------------------------------------------------------------
    private void Emailwork() {
        try {
            if (typeRun != "gmail") {
                OpenAndUpdateDriver("https://mail.tm/en/");
                getemail(driver);
            } else {
                OpenAndUpdateDriver("https://www.gmailnator.com/");

                getemailGmail(driver);
            }
        } catch (Exception ex) {

        }
    }

    //---------------------------------------------------------------------------------Phone------------------------------------------------------------
    private void getphone(WebDriver driver) {
        try {
            driver.findElement(By.xpath("//*[contains(text(),'another number')]/parent::*")).click();
        } catch (Exception e) {
            System.out.println(e);
        }
        String numberStr = "+16465106465";
        while (true) {

            try {
                Thread.sleep(4000);
            } catch (Exception e) {
            }
            if (driver.getCurrentUrl() != "https://receive-smss.com/sms/16465106465/") {
                try {
                    numberStr = "+" + driver.findElement(By.xpath("//*[@class=\"tooltip\"]/a")).getAttribute("data-attrib");

                } catch (Exception e) {
                    continue;
                }

                break;
            }
        }

        while (true) {
            try {

                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                try {
                    PhoneNumber numberProto = phoneUtil.parse(numberStr, "");

                    System.out.println("Country code: " + numberProto.getCountryCode());
                    countrycode = String.valueOf(numberProto.getCountryCode());
                    //This prints "Country code: 91"
                } catch (Exception e) {
                    System.err.println("NumberParseException was thrown: " + e);
                }

                Pnumber = numberStr.replace(countrycode, "");
                Countryname = driver.findElement(By.xpath("//h2[@class='page-description']")).getText().replace(" MOBILE PHONE NUMBER.", "");

                Countryname = Countryname.replace(numberStr + " IS A ", "");

                System.out.println(Countryname);
                break;
            } catch (Exception e) {
            }
        }
    }

    private void Phonework(WebDriver driver) {
        try {
            driver = OpenAndUpdateDriver("https://receive-smss.com/sms/16465106465/#:~:text=%E2%86%BB%20Give%20me%20another%20number");
            getphone(driver);
        } catch (Exception ex) {

        }
    }

    private void keepchecking() {
        if (KingschAccount.stopWork == true) {
            driver.quit();
        }
    }

    private ChromeDriver OpenAndUpdateDriver(String url) throws Exception {
        boolean update = false;
        String ChromeVersion = "";
        String ChromeDriverVersion = "";
        while (true) {
            if (update == true) {

                //Get Chromedriver Path
                BufferedReader reader = null;
                URL urlLink = new URL("https://chromedriver.storage.googleapis.com/?delimiter=/&prefix=" + ChromeVersion);
                reader = new BufferedReader(new InputStreamReader(urlLink.openStream()));
                String s = null;
                while ((s = reader.readLine()) != null) {
                    break;
                }

                InputSource textSource = new InputSource(new StringReader(s));
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                DocumentBuilder builder = factory.newDocumentBuilder();

                Document doc = builder.parse(textSource);
                NodeList nodelist = doc.getElementsByTagName("Prefix");

                Element element = (Element) nodelist.item(nodelist.getLength() - 2);
                ChromeDriverVersion = element.getTextContent();

                String Destination = "ChromeDriver\\chromedriver_win32.zip";
                String link = "https://chromedriver.storage.googleapis.com/" + ChromeDriverVersion + "chromedriver_win32.zip";
                //Closing ChromeDriver
                KingschAccount.StatusLBL.setText("Closing ChromeDriver");
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                } catch (Exception e) {
                }
                KingschAccount.StatusLBL.setText("Closed ChromeDriver");
                KingschAccount.StatusLBL.setText("Deleting Old ChromeDriver...");
                File Des = new File("ChromeDriver\\chromedriver.exe");
                //Deleting Existed one
                Des.delete();

                KingschAccount.StatusLBL.setText("Downloading Latest ChromeDriver...");
                URL urlChromeDriver = new URL(link);
                HttpURLConnection http = (HttpURLConnection) urlChromeDriver.openConnection();
                double fileSize = (double) http.getContentLengthLong();
                BufferedInputStream in = new BufferedInputStream(http.getInputStream());
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(Destination);
                } catch (Exception e) {
                    System.out.println(e);
                }
                //Closing ChromeDriver
                KingschAccount.StatusLBL.setText("Closing ChromeDriver");
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                } catch (Exception e) {
                }
                KingschAccount.StatusLBL.setText("Closed ChromeDriver");
                BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

                byte[] buffer = new byte[1024];
                double downloaded = 0.00;
                int read = 0;
                double percentDownloaded = 0.00;
                while ((read = in.read(buffer, 0, 1024)) >= 0) {
                    bout.write(buffer, 0, read);
                    downloaded += read;
                    percentDownloaded = (downloaded * 100) / fileSize;
                    String percent = String.format("%.2f", percentDownloaded);
                    KingschAccount.StatusLBL.setText(percent + "% Downloaded");
                }

                reader.close();
                bout.close();
                fos.close();
                in.close();
                Thread.sleep(1000);
                //Closing ChromeDriver
                KingschAccount.StatusLBL.setText("Closing ChromeDriver");
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                } catch (Exception e) {
                }
                KingschAccount.StatusLBL.setText("Closed ChromeDriver");
                Thread.sleep(1000);
                //Uzipping
                KingschAccount.StatusLBL.setText("Uzipping");
                Path source = Paths.get("ChromeDriver/chromedriver_win32.zip");
                Path target = Paths.get("ChromeDriver/");

                try {

                    unzip(source, target);
                    System.out.println("Done");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                KingschAccount.StatusLBL.setText("Uzipped");

            }

            try {
                KingschAccount.StatusLBL.setText("Starting...");
                System.setProperty("webdriver.chrome.driver", "ChromeDriver\\chromedriver.exe");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--mute-audio");
                KingschAccount.StatusLBL.setText("Adding extension...");
                 options.addExtensions(new File("C:\\Program Files\\Common Files\\ChromeDriver\\anticaptcha.crx"));
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                KingschAccount.StatusLBL.setText("Setting...");
                driver = new ChromeDriver(capabilities);
                driver.navigate().to(url);
                return driver;
            } catch (Exception e) {
                // String to be scanned to find the pattern.
                String line = e.getMessage();
                String pattern = "[0-9]+[.]";
                // Create a Pattern object
                Pattern r = Pattern.compile(pattern);
                // Now create matcher object.
                Matcher m = r.matcher(line);
                if (m.find()) {
                    ChromeVersion = m.group(0);
                }

                update = true;
                continue;
            }
        }

    }

    @Override
    public void run() {
        while (true) {
            try {

                Timer t = new Timer();

                t.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        keepchecking();
                    }
                }, 0, 1000);  // run every three seconds

                try {
                    KingschAccount.StatusLBL.setText("Setting...");

                    KingschAccount.StatusLBL.setText("Going to get email...");
                    if (typeRun == "phone") {
                        Phonework(driver);
                    } else {
                        Emailwork();
                    }


                    driver.executeScript("window.open();");
                    ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
                    driver.switchTo().window(tabs2.get(1));

                    KingschAccount.StatusLBL.setText("Going to get random name...");
                    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
                    driver.navigate().to("http://random-name-generator.info/");
                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

                    getname(driver);
                    KingschAccount.StatusLBL.setText("going to KingsChat signup page...");
                    if (email.isEmpty()) {
                        driver.navigate().to("https://accounts.kingsch.at/signup/signup-with-phone");
                    } else {
                        driver.navigate().to("https://accounts.kingsch.at/signup/signup-with-email");
                    }
                    KingschAccount.StatusLBL.setText("Fillingup signup page...");
                    if (email.isEmpty()) {
                        fillupforphonesignup(driver);
                    } else {
                        fillupforsignup(driver);
                    }
                    KingschAccount.StatusLBL.setText("Captcha solving...");
                    if (email.isEmpty()) {
                        phonecaptchasolving(driver);
                    } else {
                        captchasolving(driver);
                    }
                    KingschAccount.StatusLBL.setText("Verifying Account...");
                    if (email.isEmpty()) {
                        verfiyaccountPhone(driver);
                    } else {
                        verfiyaccount(driver);
                    }


                    Thread.sleep(5000);

                    KingschAccount.StatusLBL.setText("Saving data into csv...");
                    savedata();
                    KingschAccount.StatusLBL.setText("Doing login...");
                    driver.navigate().to("https://accounts.kingsch.at/");
                    Thread.sleep(2000);
                    login(driver);
                    Thread.sleep(5000);
                    driver.navigate().to("https://kingschat.online/user/qubwebs");

                    KingschAccount.StatusLBL.setText("Waiting 1.5 second...");
                    Thread.sleep(1500);
                    KingschAccount.StatusLBL.setText("Clicking Follow button...");
                    while (true) {
                        try {
                            driver.findElement(By.xpath("//*[@class=\"FollowButton\"]")).click();
                            System.out.println("followed main");
                            break;
                        } catch (Exception e) {
                        }
                    }
                    KingschAccount.StatusLBL.setText("Going to check if there was any url in the table...");
                    Thread.sleep(800);
                    DefaultTableModel model = (DefaultTableModel) KingschAccount.ContainingTable.getModel();
                    all:
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String url = model.getValueAt(i, 0).toString();

                        if (url.contains("https://")) {
                            KingschAccount.StatusLBL.setText("Going to " + url);
                            driver.navigate().to(url);
                            Thread.sleep(800);
                            working:
                            while (true) {
                                try {
                                    KingschAccount.StatusLBL.setText("Trying to follow...");
                                    driver.findElement(By.xpath("//*[@class=\"FollowButton\"]")).click();
                                    System.out.println("followed");
                                    KingschAccount.StatusLBL.setText("Followed...");
                                    Thread.sleep(1500);
                                    break working;
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                    KingschAccount.StatusLBL.setText("all Done...");
                    driver.quit();
                    done = true;
                } catch (Exception e) {
                    break;
                }
            } catch (Exception e) {
                break;
            }

        }
    }
}
