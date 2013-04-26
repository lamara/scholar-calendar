package com.example.scholarscraper;

import com.example.scholarscraper.CalendarSetter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.casexample.exceptions.WrongLoginException;
import com.example.scholarscraper.UpdateFragment.PageLoadListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// -------------------------------------------------------------------------
/**
 * A Scholar web scraper used to retrieve courses and course
 * assignments/information from scholar. Spits out a bunch of stuff to logcat so
 * we can easily track the status of the update process. Logging out isn't
 * handled yet so if you try to run the app multiple times without physically
 * pressing the logout button on the webview before closing the app then the web
 * scraper will break horribly.
 *
 * @author Alex Lamar
 * @author Paul Yea
 * @author Brianna Beitzel
 * @version Apr 15, 2013
 */

public class ScholarScraper
{
    /* An android webview is used internally to handle AJAX parsing */
    private WebView              webView;

    private String               username;
    private String               password;
    private String               mainPageHtml;
    private String               assignmentPageHtml;
    private Context              context;
    private Map<String, String>  casCookies;
    private List<Course>         courses;
    private JavaScriptHtmlParser jsInstance;
    private boolean              mainPageLoaded;

    private PageLoadListener     listener;

    private final String         USER_AGENT    =
                                                   "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0";
    private final String         LOGIN_PAGE    =
                                                   "https://auth.vt.edu/login?service=https%3A%2F%2Fscholar.vt.edu%2Fsakai-login-tool%2Fcontainer";
    private final String         MAIN_PAGE     =
                                                   "https://scholar.vt.edu/portal";
    private final String         GET_HTML      =
                                                   "javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');";
    private final String         LOGOUT_PAGE   =
                                                   "https://scholar.vt.edu/portal/logout";
    protected static final int   ACTION_DOWN   = 0;
    protected static final int   KEYCODE_ENTER = 66;


    // ----------------------------------------------------------
    /**
     * Create a new ScholarScraper object.
     *
     * @param username
     *            Username of the scholar account being accessed
     * @param password
     *            Password of the scholar account being accessed
     * @param context
     *            An android activity associated with the scraper
     * @throws IOException
     * @throws WrongLoginException
     */
    public ScholarScraper(
        String username,
        String password,
        Context context,
        WebView webView,
        PageLoadListener listener)
        throws IOException,
        WrongLoginException
    {
        this.username = username;
        this.password = password;
        this.context = context;
        this.webView = webView;
        this.mainPageHtml = null;
        this.mainPageLoaded = false;
        this.listener = listener;
        courses = new ArrayList<Course>();
        this.jsInstance = new JavaScriptHtmlParser();

        initalizeWebView();
        loadMainPage();
    }


    /**
     * Use this constructor to skip the courselist update process. The
     * courselist update is slow and resource heavy, but only needs to be run
     * once per user, use this constructor after the user already has their
     * courselist.
     */
    public ScholarScraper(
        String username,
        String password,
        Context context,
        PageLoadListener listener)
    {
        this.username = username;
        this.password = password;
        this.context = context;
        this.listener = listener;
    }


    /**
     * Logs into the Scholar main page while keeping a reference to its HTML
     * data This method is partly asynchronous, but the update process can't
     * continue until the asnyc portion is fully completed. An event is
     * dispatched after retrieval of the main page's HTML data to the class's
     * listener so the update process can continue.
     */
    private void loadMainPage()
    {
        /* handles logging into the Scholar main page */
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                /*
                 * calls 2nd nested onPageFinished when completed (the one just
                 * above this line), unless the main page is already loaded
                 */
                if (webView.getUrl().equals(MAIN_PAGE))
                {
                    mainPageHtml = getHtml();
                    mainPageLoaded = true;
                    System.out.println(mainPageHtml);
                    clearOnPageFinished();
                    listener.mainPageLoaded(true);
                    return;
                }

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view1, String url1)
                    {
                        System.out.println("retrieving main page html");
                        if (!webView.getUrl().equals(MAIN_PAGE))
                        {
                            System.out
                                .println("main page loaded unsucsesfully");
                            listener.mainPageLoaded(false);
                            return;
                        }
                        System.out.println("main page loaded unsucsesfully");
                        listener.mainPageLoaded(false);
                        mainPageHtml = getHtml(); // getHtml() sleeps for 1500
// ms
                        mainPageLoaded = true;
                        System.out.println(mainPageHtml);
                        clearOnPageFinished();

                        System.out.println("main page loaded successfully");
                        listener.mainPageLoaded(true);
                    }
                });
                webView
                    .loadUrl("javascript:document.getElementById('username').value = '"
                        + username
                        + "';document.getElementById('password').value='"
                        + password
                        + "';"
                        + "document.getElementsByName('submit')[0].click();");
                System.out.println("main page loading");
            }
        });
        webView.loadUrl(LOGIN_PAGE); // async, calls 1st nested onPageFinished
// when done
    }


    /**
     * Initializes the webview environment
     */
    private void initalizeWebView()
    {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUserAgentString(USER_AGENT);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webView.addJavascriptInterface(jsInstance, "HTMLOUT");
    }


    // -------------------------------------------------------------------------
    /**
     * Parses the scholar main page for course information. Loads a class list
     * for the specified semester based on data from the main page, and then
     * notifies the update listener to continue the update process.
     *
     * @throws WrongLoginException
     * @throws IOException
     */
    public void retrieveCourses(String semester)
        throws WrongLoginException,
        IOException
    {
        if (mainPageHtml == null)
        {
            throw new NullPointerException(
                "Main page's HTML has not been retrieved yet");
        }
        System.out.println("executing main page parsing/course retrieval");
        Document mainPage = Jsoup.parse(mainPageHtml);
        String[] htmls = retrieveCourseHtmls(mainPage, "Spring 2013");
        if (htmls.length == 0)
        {
            System.out.println("no classes found");
        }
        for (String html : htmls)
        {
            parseCourseHtml(html);
        }
        webView.clearCache(true);
        listener.coursesLoaded();
    }


    /**
     * Retrieves href information from the Scholar mainpage, giving the html
     * lines for all course pages within a semester
     *
     * @return links to course pages underneath Spring 2013
     * @throws IOException
     */
    private static String[] retrieveCourseHtmls(
        Document mainPage,
        String semester)
        throws IOException
    {
        System.out.println("executing semester html retrieval");
        Elements elements = mainPage.getElementsByClass("termContainer");
        ArrayList<String> links = new ArrayList<String>();

        /*
         * Scholar's HTML doesn't use a clear hierarchy in this case, so we
         * can't use Jsoup's built in HTML parser to retrieve html lines
         */
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
     * Gets course info from a single line of Html retrieved from the
     * retrieveCourseHtmls() method.
     */
    private void parseCourseHtml(String html)
    {
        String className;
        String rootUrl;
        Course course;

        Document classDoc = Jsoup.parse(html);

        Element classInfo = classDoc.select("a").first();
        className = classInfo.attr("title");
        rootUrl = classInfo.attr("href");

        course = new Course(className, rootUrl);
        courses.add(course);
    }


    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    /**
     * Finds and loads a courses assignment and quiz urls into the given course
     * object. The line of methods that this calls will execute a number of url
     * loads by the webview, which can take a while. This method should only be
     * called by the listener class in the UpdateFragment, as the listener
     * handles chaining these method calls so every course in the courselist
     * gets their assignment and quiz urls loaded.
     */
    public void retrieveAssignmentPages(Course course)
    {
        if (course.getMainUrl() == null)
        {
            System.out.println(course + " has no main page HTML");
            return;
        }
        loadCourseMainPage(course);
    }


    /**
     * Handles parsing of the course's main page, retrieves both the main
     * assignment and the main quiz urls, if present, and then executes the
     * method used to retrieve the assignment url's portlet url (the portlet url
     * holds the data we want).
     */
    private void loadCourseMainPage(final Course course)
    {
        String url = course.getMainUrl();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url2)
            {
                String html = getHtml();
                Document courseMain = Jsoup.parse(html);

                /*
                 * assignment or quiz pages often don't exist for a course, we
                 * indicate that with a null pointer
                 */

                String assignmentUrl;
                Element assignmentHtml =
                    courseMain.select("a[class*=assignment]").first();
                assignmentUrl =
                    (assignmentHtml != null)
                        ? assignmentHtml.attr("href")
                        : null;

                String quizUrl;
                Element quizHtml =
                    courseMain.select("a[class*=samigo]").first();
                quizUrl = (quizHtml != null) ? quizHtml.attr("href") : null;

                clearOnPageFinished();
                retrieveAssignmentUrls(assignmentUrl, quizUrl, course);
            }
        });
        webView.loadUrl(url);
    }


    /**
     * There are actually two assignment and two quiz pages, one is the full
     * page body and the second is a portlet window on the page, which is loaded
     * through AJAX. This finds the portlet window's HTML, which is the only
     * window that holds the data that we need (and once we have this, we don't
     * need to deal with AJAX anymore, so we can stop connecting to scholar
     * clunkily through the webview).
     */
    private void retrieveAssignmentUrls(
        final String assignmentUrl,
        final String quizUrl,
        final Course course)
    {

        if (assignmentUrl == null)
        {
            course.setAssignmentUrl(null);
            loadQuizUrl(quizUrl, course);
            return;
        }
        /*
         * retrieves the assignment's portlet url from the assignment main page,
         * and then executes the method to retrieve the quiz's portlet url from
         * the quiz main page
         */
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                String html = getHtml();
                Document assignmentPage = Jsoup.parse(html);
                Element assignmentHtml =
                    assignmentPage.select("div[class*=title] > a").first();
                String portletUrl = assignmentHtml.attr("href");

                System.out.println("assignment page:");
                System.out.println("success: " + portletUrl);

                course.setAssignmentUrl(portletUrl);

                clearOnPageFinished();
                loadQuizUrl(quizUrl, course);
            }
        });
        webView.loadUrl(assignmentUrl);
    }


    /**
     * Retrieves the quiz's portlet url and loads it into its corresponding
     * course object. Once finished, the method signifies the listener to move
     * on to the next course in the courselist.
     */
    private void loadQuizUrl(final String quizUrl, final Course course)
    {
        if (quizUrl == null)
        {
            course.setQuizUrl(null);
            System.out.println(course + " loaded");
            listener.retrieveCourseLinks();
            return;
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                String html = getHtml();
                System.out.println("quiz page:");
                Document quizPage = Jsoup.parse(html);
                Element quizHtml =
                    quizPage.select("div[class*=title] > a").first();
                String portletUrl = quizHtml.attr("href");

                System.out.println("quiz page:");
                System.out.println("success: " + portletUrl);

                course.setQuizUrl(portletUrl);
                System.out.println(course + " loaded");

                clearOnPageFinished();
                listener.retrieveCourseLinks();

            }
        });
        webView.loadUrl(quizUrl);
    }


    // -------------------------------------------------------------------------

    /**
     * Checks if the login info given is valid.
     *
     * @return True if valid, false if invalid
     */
    public boolean checkLoginInfo()
    {
        /*
         * checks using a cas login instance, relatively inexpensive and is much
         * simpler then messing with the web view
         */
        Cas cas;
        try
        {
            cas = new Cas(username.toCharArray(), password.toCharArray());
            cas.closeSession();
        }
        catch (WrongLoginException e)
        {
            return false;
        }
        return true;
    }


    /**
     * Useful for resetting the webview's onPageFinished method
     */
    private void clearOnPageFinished()
    {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                // Empty method body
            }
        });
    }


    /**
     * Logs out of scholar page. Method is asynchronous, so do not call it and
     * then immediately call another operation on the webview or else bad things
     * may happen.
     */
    public void logout()
    {
        clearOnPageFinished();
        webView.loadUrl(LOGOUT_PAGE);
    }


    /**
     * Gets the Html of the web view's current page. Will hang the thread for
     * around 1.5 seconds.
     *
     * @return Html of the web view's current page
     */
    public String getHtml()
    {
        webView.loadUrl(GET_HTML);
        SystemClock.sleep(1500); // executing javascript doesn't happen
        /* could cause errors */ // immediately and there isn't a good way to
                                 // account for that other than sleeping (still bad).
        String pageHtml = jsInstance.html;
        return pageHtml;
    }


    /**
     * Returns the internal list of courses
     */
    public List<Course> getCourses()
    {
        return courses;
    }


    // -------------------------------------------------------------------------
    /**
     * Operates on a list of courses. A course's assignments are loaded into the
     * course's internal assignment list. This task can be run independent of a
     * webview on the condition that a course's assignment and quiz portlet Urls
     * have already been found (which is where the webview portion of the
     * scholar scraper comes in)
     *
     * @author Alex Lamar
     * @version Apr 15, 2013
     */
    public static class AssignmentRetriever
        extends AsyncTask<Object, Void, Boolean>
    {

        private Map<String, String> casCookies;
        private PageLoadListener    listener;
        private Context             context;
        private CalendarSetter      calendarSetter;


        @Override
        protected void onPreExecute()
        {
            System.out.println("starting assignment retrieval");
            casCookies = null;
        }


        @Override
        protected Boolean doInBackground(Object... params)
        {
            List<Course> courses = (ArrayList<Course>)params[0];
            String username =                (String) params[1];
            String password =                (String) params[2];
            listener =             (PageLoadListener) params[3];
            context =                       (Context) params[4];
            calendarSetter = CalendarSetter.getInstance(context, "alawi");

            if (courses == null)
            {
                return false;
            }
            try
            {
                Cas cas = new Cas(username.toCharArray(), password.toCharArray());
                casCookies = cas.getCookies();
                System.out.println("CAS loaded");
                for (Course course : courses)
                {
                    if (course.getAssignmentUrl() != null)
                    {
                        parseAssignmentPage(course);
                    }
                    if (course.getQuizUrl() != null)
                    {
                        parseQuizPage(course);
                    }
                }
                cas.closeSession();
            }
            catch (WrongLoginException e)
            {
                System.out.println("Invalid username/password");
                e.printStackTrace();
                return false;
            }
            catch (ParseException e)
            {
                System.out.println("Could not parse assign/quiz urls");
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result)
        {
            if (result)
            {
                System.out.println("Update finished");
                listener.updateFinished();
            }
            else
            {
                System.out.println("Update failed");
            }
        }


        // ----------------------------------------------------------
        /**
         * Place a description of your method here.
         *
         * @param course
         */
        public void parseAssignmentPage(Course course) throws ParseException
        {
            System.out.println("parsing assignments" + course.toString());
            String assignmentUrl = course.getAssignmentUrl();
            Connection connection = Jsoup.connect(assignmentUrl);
            loadCookies(connection, casCookies);
            try
            {
                Document assignmentPage = connection.execute().parse();
                System.out.println(course.toString() + " document retrieved");

                Elements titles =
                    assignmentPage.select("td[headers=title] > h4 > a");
                Elements openDates =
                    assignmentPage.select("td[headers=openDate]");
                Elements dueDates =
                    assignmentPage.select("td[headers=dueDate]");
                Elements statuses = assignmentPage.select("td[headers=status]");

                System.out.println("Title size: " + titles.size());
                System.out.println("Open Date size: " + openDates.size());
                System.out.println("Due Date size: " + dueDates.size());
                System.out.println("Status size: " + statuses.size());

                try
                {
                    String courseName = course.getName();
                    for (int i = 0; i < titles.size(); i++)
                    {
                        String title = titles.get(i).text();
                        //openDates.get(i).text();
                        String dueDate = dueDates.get(i).text();
                        /* below gives an either a not-submitted or submitted status */
                        //String status = statuses.get(i).text();

                        Task assignment =
                            new Assignment(title, courseName, dueDate);
                        if (course.addTask(assignment))
                        {
                            calendarSetter.addEvent(assignment);
                            // assignment was added successfully, now do
                            // operations on the assignment
                            // to set notifications, add it to the calendar,
                            // etc..
                        }
                    }
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    System.out.println("error retrieving assignment data");
                    e.printStackTrace();
                }
            }
            catch (IOException e)
            {
                System.out.println("assignment page IO exception");
                e.printStackTrace();
            }
        }


        // ----------------------------------------------------------
        /**
         * Place a description of your method here.
         *
         * @param course
         * @throws ParseException
         */
        public void parseQuizPage(Course course) throws ParseException
        {
            System.out.println("parsing quizes for " + course.toString());
            String quizUrl = course.getQuizUrl();
            Connection connection = Jsoup.connect(quizUrl);
            loadCookies(connection, casCookies);
            Document quizPage;
            try
            {
                quizPage = connection.execute().parse();

                System.out.println("quiz document retrieved");

                Element quizElement =
                    quizPage.select("div[class=tier2]").first();
                Elements data = quizElement.select("td");

                System.out.println("Data size: " + data.size());

                /* data for each individual quiz comes in element groups of 3 */
                for (int i = 0; i < data.size(); i = i + 3)
                {
                    if (data.size() % 3 != 0)
                    {
                        System.out.println("uneven data sets");
                        break;
                    }
                    String title = data.get(i).select("a").first().text();
                    String courseName = course.getName();
                    String dueDate = data.get(i + 2).text();

                    Task quiz = new Quiz(title, courseName, dueDate);
                    if (course.addTask(quiz)) {
                        calendarSetter.addEvent(quiz);
                        // quiz was added successfully, now do operations on the
                        // quiz to set notifications, add it to the calendar, etc..
                    }
                }
            }
            catch (IOException e)
            {
                System.out.println("could not parse quiz page");
                e.printStackTrace();
            }
        }


        /**
         * Loads all cookies from a given cookie hashmap into a Jsoup connection
         * (generally used with loading CAS cookies into a Scholar connection)
         * Must be called before executing the connection in order to log in
         * successfully
         *
         * @param connection   A Jsoup connection
         * @param cookies      The cookie hashmap that will be loaded
         */
        private static void loadCookies(Connection connection, Map<String, String> cookies) {
            for (Entry<String, String> cookie : cookies.entrySet()) {
                connection.cookie(cookie.getKey(), cookie.getValue());
            }
        }

    }


    /**
     * // ---------------------------------------------------------------------
     * /** A Javascript interface used for retrieving HTML from a webview
     */
    public class JavaScriptHtmlParser
    {
        private String html;


        public JavaScriptHtmlParser()
        {
            this.html = null;
        }


        @JavascriptInterface
        public void showHTML(String html)
        {
            this.html = html;
        }
    }

}
