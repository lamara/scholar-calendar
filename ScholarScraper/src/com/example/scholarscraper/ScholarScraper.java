package com.example.scholarscraper;

import java.util.List;
import org.jsoup.nodes.Element;
import com.example.scholarscraper.UpdateFragment.PageLoadListener;
import java.text.ParseException;
import android.os.AsyncTask;
import org.jsoup.Jsoup;
import java.util.Map;
import com.example.casexample.exceptions.WrongLoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.os.SystemClock;
import android.webkit.WebViewClient;
import android.app.AlertDialog;
import android.webkit.JavascriptInterface;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.content.Context;
import android.webkit.WebView;

// -------------------------------------------------------------------------
/**
 *  A Scholar web scraper used to retrieve courses and course
 *  assignments/information from scholar.
 *
 *  @author Alex Lamar
 *  @version Apr 6, 2013
 */

public class ScholarScraper
{
    /* An android webview is used internally to handle AJAX parsing */
    private WebView webView;

    private String username;
    private String password;
    private String mainPageHtml;
    private String assignmentPageHtml;
    private Context context;
    private Map<String, String> casCookies;
    private List<Course> courses;
    private JavaScriptHtmlParser jsInstance;
    private boolean mainPageLoaded;

    private PageLoadListener listener;

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0";
    private final String LOGIN_PAGE = "https://auth.vt.edu/login?service=https%3A%2F%2Fscholar.vt.edu%2Fsakai-login-tool%2Fcontainer";
    private final String MAIN_PAGE = "https://scholar.vt.edu/portal";
    private final String GET_HTML = "javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');";
    protected static final int ACTION_DOWN = 0;
    protected static final int KEYCODE_ENTER = 66;

    // ----------------------------------------------------------
    /**
     * Create a new ScholarScraper object.
     * @param username Username of the scholar account being accessed
     * @param password Password of the scholar account being accessed
     * @param context An android activity associated with the scraper
     * @throws IOException
     * @throws WrongLoginException
     */
    public ScholarScraper(String username, String password, Context context, WebView webView,
                          PageLoadListener listener) throws WrongLoginException, IOException {
        this.username = username;
        this.password = password;
        this.context = context;
        this.webView = webView;
        this.mainPageHtml = null;
        this.mainPageLoaded = false;
        this.listener = listener;
        courses = new ArrayList<Course>();
        this.jsInstance = new JavaScriptHtmlParser();

        initalizeWebView(); //loads webview, as well as loading the main page
        loadMainPage();     //html into the mainPageHTML field
    }


    /**
     * Logs into the Scholar main page while keeping a reference to its HTML data
     * This method is partly asynchronous, but will still hang the main thread
     * for a few seconds. An event is dispatched on retrieval of the HTML data
     * to the class's listener.
     */
    private void loadMainPage() {
        /* handles logging into the Scholar main page */
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view1, String url1) {
                        System.out.println("retrieving main page html");
                        mainPageHtml = getHtml();
                        mainPageLoaded = true;
                        System.out.println(mainPageHtml);
                        clearOnPageFinished();
                        listener.mainPageLoaded();
                    }
                });
                /* calls 2nd nested onPageFinished when completed, unless
                 * the main page is already loaded */
                if (webView.getUrl().equals(MAIN_PAGE)) {
                    mainPageHtml = getHtml();
                    mainPageLoaded = true;
                    System.out.println(mainPageHtml);
                    clearOnPageFinished();
                    listener.mainPageLoaded();
                    return;
                }
                webView.loadUrl("javascript:document.getElementById('username').value = '" + username
                    + "';document.getElementById('password').value='"+ password +"';"
                    + "document.getElementsByName('submit')[0].click();");
                System.out.println("main page loading");
            }
        });
        webView.loadUrl(LOGIN_PAGE); //async, calls 1st nested onPageFinished when done
    }

    /**
     * Initializes the webview environment
     */
    private void initalizeWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUserAgentString(USER_AGENT);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webView.addJavascriptInterface(jsInstance, "HTMLOUT");
    }

    /**
     * Parses the scholar main page for course information.
     * Returns a class list for the specified semester based on data from the
     * main page.
     * @throws WrongLoginException
     * @throws IOException
     */
    public void retrieveCourses(String semester) throws WrongLoginException, IOException {
        if (mainPageHtml == null) {
            throw new NullPointerException("Main page's HTML has not been retrieved yet");
        }
        System.out.println("executing main page parsing/course retrieval");
        Document mainPage = Jsoup.parse(mainPageHtml);
        String[] htmls = retrieveCourseHtmls(mainPage, "Spring 2013");
        if (htmls.length == 0) {
            System.out.println("no classes found");
        }
        for (int i = 0; i < htmls.length; i++) {
            parseCourseHtml(htmls[i]);
        }
        webView.clearCache(true);
        listener.coursesLoaded();
    }
    public void parseCourseHtml(String html) {
        String className;
        String rootUrl; //reference to the main url of the specific class page
        Course course;

        Document classDoc = Jsoup.parse(html);

        Element classInfo = classDoc.select("a").first();
        className = classInfo.attr("title");
        rootUrl = classInfo.attr("href");


        course = new Course(className, rootUrl);
        courses.add(course);
    }

    /**
     * Retrieves href information from the Scholar mainpage, giving the html lines
     * for all course pages within a semester
     *
     * @return links to course pages underneath Spring 2013
     * @throws IOException
     */
    private static String[] retrieveCourseHtmls(Document mainPage, String semester)
        throws IOException
    {
        System.out.println("executing semester html retrieval");
        Elements elements = mainPage.getElementsByClass("termContainer");
        ArrayList<String> links = new ArrayList<String>();

        /* Scholar's HTML doesn't use a clear hierarchy in this case, so we
         * can't use Jsoup's built in HTML parser to retrieve html lines */
        String html = elements.html();
        StringReader reader = new StringReader(html);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        Boolean correctParent = false;
        while ((line = bufferedReader.readLine()) != null)
        {
            if (line.contains("<h4>" + semester + "</h4>"))
            {
                correctParent = true;
                continue;
            }
            if (line.contains("</ul>"))
            {
                correctParent = false;
            }
            if (correctParent)
            {
                if (!line.contains("<ul>"))
                {
                    links.add(line);
                }
            }
        }
        return links.toArray(new String[links.size()]);
    }


    /**
     * Finds and loads a courses assignment Htmls into the given course object
     */
    public void retrieveAssignmentPages(Course course) {
        if (course.getMainUrl() == null) {
            System.out.println(course + " has no main page HTML");
            return;
        }
        loadCourseMainPage(course.getMainUrl());

    }

    /**
     *
     */
    private void loadCourseMainPage(String url) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String html = getHtml();
                Document courseMain = Jsoup.parse(html);
                System.out.println(courseMain);
                Element assignmentHtml = courseMain.select("a[class*=assignment]").first();
                String assignmentUrl = assignmentHtml.attr("href");
                System.out.println("success: " + assignmentUrl);

            }
        });
        webView.loadUrl(url);
    }
    /**
     * There are actually two assignment pages, one is the full page body
     * and the second is a portlet window on the page loaded through AJAX.
     * This finds the portlet window's HTML (and once we have this, we don't
     * need to deal with AJAX anymore, so we can stop connecting to scholar
     * clunkily through the webview (this is a very good thing))
     */
    private void loadAssignmentUrl(String url) {

    }
    private void loadQuizUrl(String url) {

    }

    /**
     * Useful for resetting the webview's onPageFinished method
     */
    private void clearOnPageFinished() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //Empty method body
            }
        });
    }

    // ----------------------------------------------------------
    /**
     * Gets the Html of the web view's current page
     * @return Html of the web view's current page
     */
    public String getHtml() {
        webView.loadUrl(GET_HTML);
        SystemClock.sleep(1500); //executing javascript doesn't happen immediately
        /* could cause errors */ //and there isn't a good way to account for that
                                 //other than sleeping (still bad).
        String pageHtml = jsInstance.html;
        return pageHtml;
    }

    /**
     * Returns the internal list of courses
     */
    public List<Course> getCourses() {
        return courses;
    }






    /**
     * // ---------------------------------------------------------------------
    /**
     *  A Javascript interface used for retrieving HTML from a webview
     */
    public class JavaScriptHtmlParser
    {
        private String html;

        public JavaScriptHtmlParser() {
            this.html = null;
        }

        @JavascriptInterface
        public void showHTML(String html)
        {
            this.html = html;
        }
    }

}