package kingsch.bot;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StartAccount extends Thread {

    public static void stopfile() {
        driver.quit();

    }

    public static void downloadFile(URL url, String outputFileName) throws IOException {
        try ( InputStream in = url.openStream();  ReadableByteChannel rbc = Channels.newChannel(in);  FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private void phonecaptchasolving(ChromeDriver driver) throws InterruptedException {
        Allwork:
        while (true) {
            while (true) {           //clicking solver
                try {
                    Thread.sleep(2000);
                    try {
                        driver.switchTo().frame(driver.findElementByXPath("(//*[@title=\"recaptcha challenge\"])[2]"));
                    } catch (Exception e) {
                    }

                    driver.findElement(By.xpath("//*[@class='button-holder help-button-holder']")).click();
                    String myString = driver.getPageSource();
                    break;
                } catch (Exception e) {
                    Thread.sleep(2000);
                    String value = new String();
                    value = driver.getPageSource();
                    if (!value.contains("If there are none, click skip")) {
                        break;

                    }
                }
            }

            verifying:
            while (true) {
                String value = driver.getPageSource();
                if (value.contains("Press PLAY to listen")) {

                    break verifying;
                } else {

                }
                var value2 = driver.getPageSource();
                if (value2.contains("Multiple correct solutions required - please solve more.")) {
                    driver.findElement(By.xpath("//*[@class=\"button-holder help-button-holder\"]")).click();
                    continue verifying;
                } else {
                    break verifying;
                }
            }

            String value = driver.getPageSource();
            if (value.contains("Try again")) {
                try {
                    driver.findElement(By.xpath("//*[@id=\"reset-button\"]")).click();
                    driver.switchTo().defaultContent();
                    driver.findElement(By.xpath("//*[@class='submit-btn']")).click();
                    continue Allwork;
                } catch (Exception e) {
                }
            }

            try {
                driver.switchTo().defaultContent();
                String value1 = driver.getPageSource();
                if (value1.contains("We have sent you an")) {
                    break Allwork;
                }
            } catch (Exception e) {
            }
            driver.switchTo().frame(2);
            String answer = "";
            String value1 = driver.getPageSource();

            try {

                if (value1.contains("Press PLAY to listen")) {

                    try {
                        URL url = new URL(driver.findElement(By.xpath("//*[@class=\"rc-audiochallenge-tdownload-link\"]")).getAttribute("href"));
                        downloadFile(url, "audio.mp3");
                    } catch (Exception e) {
                    }

                    driver.executeScript("window.open();");
                    ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
                    driver.switchTo().window(tabs2.get(1));
                    driver.switchTo().defaultContent();
                    driver.navigate().to("https://speech-to-text-demo.ng.bluemix.net/#text");
                    Thread.sleep(1000);
                    try {
                        driver.findElement(By.xpath("//*[@class=\"truste_box_overlay\"]"));
                        while (true) {
                            try {
                                driver.switchTo().frame(driver.findElement(By.xpath("//*[@title=\"TrustArc Cookie Consent Manager\"]")));
                                driver.findElement(By.xpath("//a[text()='Accept Default']")).click();
                                break;
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                    driver.findElement(By.xpath("//*[@type=\"file\"]")).sendKeys(System.getProperty("user.dir") + "/audio.mp3");
                    Thread.sleep(5000);
                    while (true) {
                        try {
                            answer = driver.findElement(By.xpath("//*[@data-id=\"Text\"]")).getText();
                            break;
                        } catch (Exception e) {
                        }
                    }
                    driver.close();
                    try {
                        ArrayList<String> tabs1 = new ArrayList<String>(driver.getWindowHandles());
                        driver.switchTo().window(tabs1.get(0));
                    } catch (Exception e) {
                    }
                    try {
                        driver.switchTo().frame(2);
                    } catch (Exception e) {
                    }

                    inputing:
                    while (true) {
                        try {
                            driver.findElement(By.xpath("//*[@id=\"audio-response\"]")).sendKeys(answer);
                            break inputing;
                        } catch (Exception e) {
                        }

                    }

                    verifybutton:
                    while (true) {
                        try {
                            driver.findElement(By.xpath("//*[@id=\"recaptcha-verify-button\"]")).click();
                            break verifybutton;
                        } catch (Exception e) {
                        }

                    }
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

    private void fillupforphonesignup(WebDriver driver) {
        driver.findElement(By.xpath("//*[@class='CountrySelect__wrapper']/div")).click();

        String searchingCountry = "//*[@class=\"CountrySelect__option\"]/span";
        try {
            for (int i = 0; i < 2; i++) {
                String adding = "[contains(text(),'" + Countryname[i].toString() + "')]";
                searchingCountry = searchingCountry + adding;
            }

        } catch (Exception e) {
        }

        while (true) {
            try {
                driver.findElement(By.xpath(searchingCountry)).click();
                break;
            } catch (Exception e) {
            }
        }

        driver.findElement(By.xpath("//*[@name=\"phone_number\"]")).sendKeys(Pnumber);
        driver.findElement(By.xpath("//*[@class=\"form-actions\"]")).click(); //*[@class="CountrySelect__option"]/span[contains(text(),'Malay')][contains(text(),'')]

    }

    private void fillupforsignup(WebDriver driver) {
        driver.findElement(By.xpath("//*[@name='email']")).sendKeys(email);
        driver.findElement(By.xpath("//*[@name='name']")).sendKeys(name);
        driver.findElement(By.xpath("//*[@name='username']")).sendKeys(name.replaceAll("\\s+", "") + "1234");
        driver.findElement(By.xpath("//*[@name='password']")).sendKeys("Q123456789");
        driver.findElement(By.xpath("//*[@class='submit-btn']")).click();
    }

    String email = "";
    String name = "";

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

    private void getname(WebDriver driver) {
        name = driver.findElement(By.xpath("(//*[@class=\"nameList\"]/li)[1]")).getText();
    }

    private void verfiyaccount(WebDriver driver) {   //🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏🤏
        driver.navigate().to("https://mail.tm/en/");

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

    private void login(WebDriver driver) {
        driver.findElement(By.xpath("//*[@placeholder=\"Username, Phone Number or E-mail\"]")).sendKeys(email);
        driver.findElement(By.xpath("//*[@placeholder=\"Password\"]")).sendKeys("Q123456789");
        driver.findElement(By.xpath("(//*[@class=\"submit-btn\"])[1]")).click();

    }

    private static ChromeDriver driver;

    private void savedata() {
        try {
            File accFile = new File("C:\\Program Files\\Common Files\\CSVS\\Account.csv");
            accFile.getParentFile().mkdirs();
            accFile.createNewFile(); // if file already exists will do nothing
            FileWriter writer = new FileWriter("C:\\Program Files\\Common Files\\CSVS\\Account.csv", true);
            BufferedWriter Bwriter = new BufferedWriter(writer);
            PrintWriter Pwriter = new PrintWriter(Bwriter);
            Pwriter.println(email + "," + "Q123456789" + "," + name + "," + name.replaceAll("\\s+", "") + ",");
            Pwriter.flush();
            Pwriter.close();
            System.out.println("saved");
        } catch (Exception e) {

        }
    }

    private void Emailwork() {
        try {
            OpenAndUpdateDriver("https://mail.tm/en/");
            getemail(driver);
        } catch (Exception ex) {

        }
    }
    String countrycode = "";
    String Pnumber = "";
    String Countryname[] = null;

    private void getphone(WebDriver driver) {

        driver.findElement(By.xpath("//*[@class='layui-btn clickA']")).sendKeys(Keys.SPACE);
        while (true) {

            try {
                Thread.sleep(3000);
            } catch (Exception e) {
            }
            if (driver.getCurrentUrl() != "https://mytempsms.com/receive-sms-online/malaysia-phone-number-1128652898.html") {
                try {
                    driver.findElement(By.xpath("//*[@class=\"copy\"]"));

                } catch (Exception e) {
                    continue;
                }

                break;
            }
        }

        while (true) {
            try {

                countrycode = driver.findElement(By.xpath("//*[@class=\"copy\"]/img")).getAttribute("src");
                Pattern intsOnly = Pattern.compile("\\d+");
                Matcher makeMatch = intsOnly.matcher(countrycode);
                makeMatch.find();
                String inputInt = makeMatch.group();
                countrycode = inputInt;
                Pnumber = driver.findElement(By.xpath("//*[@class=\"copy\"]")).getAttribute("data-clipboard-text");
                Countryname = driver.findElement(By.xpath("//*[@class=\"info-top-h1\"]/h1")).getText().replace("Free Receive SMS From ", "").split("(?=\\p{Upper})");
                System.out.println(Countryname[0]);
                break;
            } catch (Exception e) {
            }
        }
    }

    private void Phonework(WebDriver driver) {
        driver.navigate().to("https://mytempsms.com/receive-sms-online/uk-phone-number-5020203597.html");
        getphone(driver);
    }
    private boolean done = false;

    private void keepchecking() {
        if (KingschAccount.stopWork == true) {
            driver.quit();
        }
    }

    private static void unzip(Path source, Path target) throws IOException {

        try ( ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = false;
                // example 1.1
                // some zip stored files and folders separately
                // e.g data/
                //     data/folder/
                //     data/folder/file.txt
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

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
    // protect zip slip attack

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

    private void OpenAndUpdateDriver(String url) throws Exception {
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
                };
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
                };
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
                };
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
                break;
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
                String countrycode = "";
                String Pnumber = "";
                String Countryname[] = null;

                Timer t = new Timer();

                t.scheduleAtFixedRate(
                        new TimerTask() {
                    public void run() {

                        keepchecking();
                    }
                },
                        0, // run first occurrence immediately
                        1000);  // run every three seconds

                try {
                    KingschAccount.StatusLBL.setText("Setting...");

                    KingschAccount.StatusLBL.setText("Goging to get email...");
                    Emailwork();
                    //Phonework(driver);
                    KingschAccount.StatusLBL.setText("Goging to get random name...");
                    driver.navigate().to("http://random-name-generator.info/");
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
                    verfiyaccount(driver);
                    KingschAccount.StatusLBL.setText("Saving data into csv...");
                    savedata();
                    KingschAccount.StatusLBL.setText("Going to login for follow main account...");
                    driver.navigate().to("https://accounts.kingsch.at/?client_id=com.kingschat&scopes=%5B%22kingschat%22%5D&redirect_uri=https%3A%2F%2Fweb.kingsch.at%2Fsuperusers%2Fqubwebs");
                    login(driver);
                    KingschAccount.StatusLBL.setText("Waiting 1 second...");
                    Thread.sleep(1000);
                    KingschAccount.StatusLBL.setText("Clicking Follow button...");
                    while (true) {
                        try {
                            driver.findElement(By.xpath("//*[@class='SuperuserProfile__actions-item ripple']")).click();
                            System.out.println("followed main");
                            break;
                        } catch (Exception e) {
                        }
                    }
                    KingschAccount.StatusLBL.setText("Going to check if there was any url in the table...");
                    Thread.sleep(500);
                    DefaultTableModel model = (DefaultTableModel) KingschAccount.ContainingTable.getModel();
                    all:
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String url = model.getValueAt(i, 0).toString();

                        if (url.contains("https://")) {
                            KingschAccount.StatusLBL.setText("Going to " + url);
                            driver.navigate().to(url);
                            working:
                            while (true) {
                                try {
                                    KingschAccount.StatusLBL.setText("Trying to follow...");
                                    driver.findElement(By.xpath("//*[@class=\"SuperuserProfile__actions-item ripple\"]")).click();
                                    System.out.println("followed");
                                    KingschAccount.StatusLBL.setText("Followed...");
                                    KingschAccount.StatusLBL.setText("Verifying...");
                                    Thread.sleep(1000);
                                    KingschAccount.StatusLBL.setText("verified...");
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
