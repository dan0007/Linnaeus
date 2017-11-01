import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;
import java.net.*;
import java.util.ArrayList;
public class WebCrawler {
    
    private static ArrayList<String> pastURLs = new ArrayList<>();
    private static ArrayList<Feature> crawledFeatureList = new ArrayList<>();
    int pageNum = 1000;

//    public static void main(String[] args) {
//        pastURLs = new ArrayList();
//
//        int pageNum = 0;
//        int maxDepth;
//        File dir = new File("pages");
//        if(dir.mkdir()) {
//            System.out.println("Pages directory created.");
//        }
//
//        for (File childFile : dir.listFiles()) {
//            childFile.delete();
//
//        }
//        String startURL;
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Starting URL: ");
//        startURL = scanner.nextLine();
//        pastURLs.add(startURL);
//        System.out.println("Depth: ");
//        maxDepth = Integer.parseInt(scanner.nextLine());
//
//        long startTime = System.currentTimeMillis();
//        pageNum = crawl(startURL, maxDepth, pageNum);
//        long endTime = System.currentTimeMillis();
//        System.out.println("Time took: " + (endTime - startTime) + " milliseconds");
//
//
//    }

    public static void main(String[] args) {
        short method = 2;
        short depth = 10;

        WebCrawler crawlDefault = new WebCrawler("http://www.Auburn.com",depth);

        for (int i = 0; i < crawledFeatureList.size(); i++){
            try {
                System.out.println(crawlDefault.crawledFeatureList.get(i).address + " "
                        + crawlDefault.crawledFeatureList.get(i).getGradientClassificaton() + " "
                        + crawlDefault.crawledFeatureList.get(i).Classification + " "
                        + Arrays.toString(crawlDefault.crawledFeatureList.get(i).FeaturePayload));
            } catch(IndexOutOfBoundsException e){
                System.out.println("Must not have found any pages");
            }
        }

        for (Feature temp : crawlDefault.crawledFeatureList){
            KthClassify anthrtmp = new KthClassify(temp,method,3);
        }
        for (int i = 0; i < crawledFeatureList.size(); i++) {

            try {
                System.out.println(crawlDefault.crawledFeatureList.get(i).address + " "
                        + crawlDefault.crawledFeatureList.get(i).getGradientClassificaton() + " "
                        + crawlDefault.crawledFeatureList.get(i).Classification + " "
                        + Arrays.toString(crawlDefault.crawledFeatureList.get(i).FeaturePayload));
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Must not have found any pages");
            }
        }
        System.out.println(crawledFeatureList.size() + " :number of features found");

        if (crawledFeatureList.get(0).quadrant1Sum < 10 || crawledFeatureList.get(0).quadrant2Sum < 10 ||
                crawledFeatureList.get(0).quadrant3Sum < 10 || crawledFeatureList.get(0).quadrant4Sum < 10){
            System.out.println("Q1: " + crawledFeatureList.get(0).quadrant1Sum + " Q2: " +
                    crawledFeatureList.get(0).quadrant2Sum + " Q3: " + crawledFeatureList.get(0).quadrant3Sum +
                    " Q4: " + crawledFeatureList.get(0).quadrant4Sum);
        }
        System.out.println((int)crawledFeatureList.get(0).TSetAccuracy + "%" + " :Accuracy of trainset found");

    }

    public WebCrawler(int aDepth){
        String theDefaultURL = "http://www.reddit.com";
        crawl(theDefaultURL,aDepth,1);

    }

    public WebCrawler(String aURL, int aDepth){
        crawl(aURL,aDepth,pageNum);

    }

    public static void crawl(String URL, int depth, int pageNumb) {
        String HTML;
        if (!pastURLs.contains(URL)){
            pastURLs.add(URL);
            try {
                HTML = getPage(URL); // Get HTML code. Throws Exception if URL not valid
                pageNumb++; //Increment pageNum if exception not thrown
                processPage(URL,HTML, pageNumb); //Create Unigram and Store HTML

                if (depth > 0) {
                    ArrayList<String> links = getLinks(HTML);
                    for (String link : links) {
                        crawl(link, depth-1, pageNumb);
                    }
                }
            } catch (MalformedURLException e) {
            System.out.println("Malformed URL:\n" + URL);
            } catch (IOException e) {
            System.out.println("HTML could not be retrieved:\n" + URL);
            }

        }
    }
    //Returns HTML string of page
    public static String getPage(String URL_link) throws MalformedURLException, IOException {
        String HTML = "";

        java.net.URL url = new URL(URL_link); //args[0]
        URLConnection connection = url.openConnection();
        DataInputStream in = new DataInputStream(connection.getInputStream());
        BufferedReader d = new BufferedReader(new InputStreamReader(in));
        String text;
        while ((text = d.readLine()) != null) {
            HTML += "  " + text + "\n";
        }
        return HTML;
    }
    //Creates UNI-Gram and store page
    public static void processPage(String aUrl, String HTML, int pageNumber) {

        char myChar;
        int indexOf;

        double[] uniGram = new double[95];
        int sumOfAllChar = 0;
        for (int pos = 0; pos < HTML.length(); ++pos) {//loop through all char in HTML page and build a featurePayload
            myChar = HTML.charAt(pos);                  // also add up the total chars found
            indexOf = (int) myChar;
            if (indexOf >= 32 && indexOf < 127) {
                sumOfAllChar += 1;
                uniGram[indexOf - 32]++;

            }
        }
        for (int pos = 0; pos < uniGram.length; ++pos) {
            if (sumOfAllChar != 0){//divide the each featurePayload unit by the total number of chars found
                uniGram[pos] = uniGram[pos] / sumOfAllChar;
            }
        }
//        if (pageNum < 2){
//            System.out.println("\nA unigraham: " + Arrays.toString(uniGram));
//        }

        crawledFeatureList.add(new Feature(aUrl,uniGram,0.0,pageNumber));

        String path = ""; //HTML Pages to file
        try {

            File file = new File("out/pages/" + aUrl.substring(7,aUrl.length()-5) + ".txt");
            path = file.getAbsolutePath();
            System.out.println(path + " -------------------------------------------------PATH of saved file");
            FileWriter writer = new FileWriter(file);
            writer.write(HTML);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("File could not be written:" + path);
        }

    }
    //Returns String arraylist of all links found in HTML
    private static ArrayList<String> getLinks(String HTML) {
        ArrayList<String> links = new ArrayList();
        Pattern p = Pattern.compile("href=\"([^\"]*)\"");
        Matcher m = p.matcher(HTML);
        while (m.find() && links.size() < 15) { //Improvement #1
            String href = m.group();
            int firstQuote = href.indexOf("\"");
            href = href.substring(firstQuote + 1);
            href = href.substring(0, href.length() - 1);
            if(href.charAt(href.length()-1) == 'l' && href.charAt(0) == 'h') {
                links.add(href);
            }
        }
        return links;
    }
}
