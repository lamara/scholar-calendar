package com.scholarscraper;

import com.scholarscraper.alarm.AlarmSetter;
import java.io.IOException;
import com.scholarscraper.model.Assignment;
import com.scholarscraper.model.Course;
import com.scholarscraper.model.Quiz;
import com.scholarscraper.model.Task;
import android.content.Context;
import android.os.AsyncTask;
import com.example.casexample.exceptions.WrongLoginException;
import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScholarScraper
    extends AsyncTask<Object, Void, Integer>
{
    private final String    LOGIN       =
                                            "https://auth.vt.edu/login?service=https%3A%2F%2Fscholar.vt.edu%2Fsakai-login-tool%2Fcontainer";

    public static final int SUCCESSFUL  = 10;
    public static final int WRONG_LOGIN = 11;
    public static final int ERROR       = 12;
    public static final int IO_ERROR    = 13;
    public static final int CANCELLED   = 14;

    protected Context                 context;
    protected List<Course>            courses;

    Exception exception = null;


    @Override
    protected void onPreExecute()
    {
        CookieManager cookieManager =
            new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }


    //TODO there isn't a timeout currently, if the user's internet is slow or
    //something causes the update to hang then the task may not finish updating.
    //add a timeout somewhere between 30-60 seconds
    //TODO error handling is weird at the moment, use a system that makes more sense
    @Override
    protected Integer doInBackground(Object... params)
    {
        String username = (String)params[0];
        String password = (String)params[1];
        context = (Context)params[2];
        try
        {
            String mainPage = loginToScholar(username, password);
            System.out.println("Printing main page, logged in successfully: \n" + mainPage);
            courses = retrieveCourses(mainPage, getSemester());
            retrieveTasks(courses);
            if (isCancelled()) {
                return CANCELLED;
            }
        }
        catch (WrongLoginException e)
        {
            System.out.println("login failed");
            exception = e; //exception gets passed through onCancelled
            cancel(true); // calls onCancelled, a cleaner way to exit
            return WRONG_LOGIN;
        }
        catch (IOException e)
        {
            exception = e;
            cancel(true);
            return IO_ERROR;
        }
        catch (ParseException e)
        {
            exception = e;
            cancel(true);
            return ERROR;
        }
        return SUCCESSFUL;
    }


    @Override
    public void onPostExecute(Integer result)
    {
        try {
            saveCourses();
        }
        catch (IOException e) {
            result = IO_ERROR;
            e.printStackTrace();
        }
        if (context != null && context instanceof MainActivity) {
            ((MainActivity) context).onUpdateFinished(courses, result);
        }
    }


    @Override
    public void onCancelled(Integer result)
    {
        if (result == null) {
            result = CANCELLED; //whenever asynctask.cancel() gets called
                                //it might pass a null integer in if called after
                                //the saveCourses step gets executed.
                                //right now this is mainly to safeguard against null
        }
        if (context != null && context instanceof MainActivity)
        {
            ((MainActivity) context).onUpdateCancelled(result, exception);
        }
    }


    protected List<Course> retrieveCourses(String mainPageHtml, String semester)
        throws WrongLoginException,
        IOException, ParseException
    {
        System.out.println("Executing course retrieval");
        Document mainPage = Jsoup.parse(mainPageHtml);
        String[] htmls = retrieveCourseHtmls(mainPage, semester);
        List<Course> courses = new ArrayList<Course>();
        for (String html : htmls)
        {
            courses.add(parseCourseHtml(html));
        }
        return courses;
    }


    /**
     * Retrieves href information from the Scholar mainpage, giving the html
     * lines for all course pages within a semester
     *
     * @return links to course pages underneath the current semester
     * @throws IOException
     */
    private String[] retrieveCourseHtmls(Document mainPage, String semester)
        throws IOException
    {
        System.out.println("executing semester html retrieval");
        Elements elements = mainPage.select("div#otherSitesCategorWrap");
        // System.out.println(elements);
        ArrayList<String> htmlLines = new ArrayList<String>();

        /*
         * Scholar's HTML doesn't use a clear hierarchy in this case, so we
         * can't use Jsoup's built in HTML parser to retrieve html lines
         */
        String html = elements.html();
        System.out.println(html);
        StringReader reader = new StringReader(html);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null)
        {
            //This is where the html scraping gets (really) messy. Most pages have a semester tag
            //that seperates courses that belong to different semesters, however if it is a user's
            //first semester then that identifier doesn't exist. If this is the case then we need
            //different logic to get the user's course information.
            if (line.contains("<h4>" + semester + "</h4>"))
            {
                bufferedReader.readLine(); // skips over one element that we don't want
                while ((line = bufferedReader.readLine()).contains("<li>"))
                {
                    htmlLines.add(line);
                }
                break;
            }
        }
        if (htmlLines.size() == 0) {
            //Do another pass, trying to pick up course info from the nav bar
            String pageHtml = mainPage.html(); //this time we are getting all of the page's html
            bufferedReader = new BufferedReader(new StringReader(pageHtml));
            while ((line = bufferedReader.readLine()) != null)
            {
                if (line.contains("<li class=\"nav-menu\">")) {
                    htmlLines.add(line);
                }
            }
        }
        if (htmlLines.size() == 0) {
            System.out.println("No course links found");
        }
        return htmlLines.toArray(new String[htmlLines.size()]);
    }


    /**
     * Gets course info from a single line of course data from the main page
     * HTML. Returns an error if there was an issue parsing course data.
     * @throws ParseException
     */
    private Course parseCourseHtml(String html) throws ParseException
    {
        String className;
        String rootUrl;
        Document classDoc = Jsoup.parse(html);

        Element classInfo = classDoc.select("a").first();
        className = classInfo.text();
        rootUrl = classInfo.attr("href");
        if (className == null || rootUrl == null) {
            throw new ParseException(
                "Unable to parse course data from the following line of HTML:  "
                    + html,
                0);
        }
        System.out.println(className + " created");
        return new Course(className, rootUrl);
    }


    protected void retrieveTasks(List<Course> courses)
        throws IOException,
        ParseException
    {
        for (Course course : courses)
        {
            System.out.println(course);
            String courseHtml = parseScholarPage(course.getMainUrl());
            Document courseDoc = Jsoup.parse(courseHtml);
            // System.out.println(courseDoc);
            String assignmentUrl;
            Element assignmentHtml =
                courseDoc.select("span[class*=assignment]").first();
            if (assignmentHtml != null)
            {
                assignmentUrl = assignmentHtml.parent().attr("href");
                System.out.println(assignmentUrl);
                retrieveAssignments(course, assignmentUrl);
            }

            String quizUrl;
            Element quizHtml = courseDoc.select("span[class*=samigo]").first();
            if (quizHtml != null)
            {
                quizUrl = quizHtml.parent().attr("href");
                System.out.println(quizUrl);
                retrieveQuizzes(course, quizUrl);
            }
        }
    }


    private void retrieveQuizzes(Course course, String quizUrl)
        throws IOException,
        ParseException
    {
        String quizPage = parseScholarPage(quizUrl);
        Document quizDoc = Jsoup.parse(quizPage);
        Element quizHtml = quizDoc.select("div[class*=title] > a").first();
        // the portlet page holds all of the data that we need for a course's quizzes
        String portletUrl = quizHtml.attr("href");

        System.out.println("connecting to quiz portlet url at " + portletUrl);

        // we save the portlet url so we can connect directly to it with a
        // more lightweight update process instead of going through all
        // of the steps we've had to do up to this point
        course.setQuizPortletUrl(portletUrl);

        getQuizzesFromPortlet(course, portletUrl);
    }

    protected void getQuizzesFromPortlet(Course course, String portletUrl) throws ParseException, IOException {
        String portletPage = parseScholarPage(portletUrl);
        Document portletDoc = Jsoup.parse(portletPage);

        Element quizElement = portletDoc.select("div[class=tier2]").first();
        Elements data = quizElement.select("td");

        String courseName = course.getName();
        // data for each individual quiz comes in element groups of 3
        for (int i = 0; i < data.size(); i = i + 3)
        {
            if (data.size() % 3 != 0)
            {
                System.out.println("uneven data sets");
                throw new ParseException(
                    "Uneven data sets when parsing quiz data from "
                        + course.toString(),
                    i);
            }
            String title = data.get(i).select("a").first().text();
            String dueDate = data.get(i + 2).text();

            Task quiz = new Quiz(title, courseName, dueDate);
            long result = course.addTask(quiz);
            if (result != Course.ADDED && result != Course.NOT_ADDED)
            {
                // Quiz was replaced due to an update to its due date.
                // In this case, course.addTask() returns the replaced quiz's
                // unique ID, which we can use to cancel the old quiz's
                // out of date alarm.
                AlarmSetter.cancelAlarm(context, result);
            }
        }
    }


    private void retrieveAssignments(Course course, String assignmentUrl)
        throws IOException,
        ParseException
    {
        String assignmentPage = parseScholarPage(assignmentUrl);
        Document assignmentDoc = Jsoup.parse(assignmentPage);
        Element assignmentHtml =
            assignmentDoc.select("div[class*=title] > a").first();
        String portletUrl = assignmentHtml.attr("href");

        // we save the portlet url so we can connect directly to it with a
        // more lightweight update process instead of going through all
        // of the steps we've had to do up to this point
        course.setAssignmentPortletUrl(portletUrl);

        getAssignmentsFromPortlet(course, portletUrl);
    }

    protected void getAssignmentsFromPortlet(Course course, String portletUrl) throws IOException, ParseException {
        String portletPage = parseScholarPage(portletUrl);
        Document portletDoc = Jsoup.parse(portletPage);

        Elements titles = portletDoc.select("td[headers=title] > h4 > a");
        Elements dueDates = portletDoc.select("td[headers=dueDate]");
        portletDoc.select("td[headers=status]");

        String courseName = course.getName();
        for (int i = 0; i < titles.size(); i++)
        {
            String title = titles.get(i).text();
            String dueDate = dueDates.get(i).text();
            /* below gives an either a not-submitted or submitted status */
            // String status = statuses.get(i).text();

            Task assignment = new Assignment(title, courseName, dueDate);
            long result = course.addTask(assignment);
            if (result != Course.ADDED && result != Course.NOT_ADDED)
            {
                // Assignment was replaced due to an update to its due date.
                // In this case, course.addTask() returns the replaced assignment's
                // unique ID, which we can use to cancel the old assignment's
                // out of date alarm.
                AlarmSetter.cancelAlarm(context, result);
            }
        }
    }


    /**
     * Can parse any page inside the scholar website on the precondition that
     * loginToScholar() has already been called (for CAS/authentication purposes).
     */
    private String parseScholarPage(String urlString)
        throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        return readFromInputStream(stream);
    }


    protected String loginToScholar(String username, String password)
        throws IOException,
        WrongLoginException
    {
        // gets login page html, needed to extract a field used in post
        String loginPage = parseScholarPage(LOGIN);
        Document loginDoc = Jsoup.parse(loginPage);
        // open up connection for post
        URL url = new URL(LOGIN);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        // if the CAS page ever changes and things start to break then the below
        // html parsing would be a good place to look
        Element lt = loginDoc.select("input[type=hidden]").first();
        if (lt == null)
        {
            // if the user is already logged in then the http will redirect to
            // the main page, where there is no lt element. We can just return the
            // page at this point.
            return loginPage;
        }
        System.out.println("Printing lt element: \n" + lt);

        // fields used for post
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("lt", lt.getElementsByAttribute("value").first().val());
        fields.put("submit", "_submit");
        fields.put("_eventId", "submit");
        fields.put("execution", "e1s1");
        fields.put("username", username);
        fields.put("password", password);

        String postData = buildPostData(fields);

        System.out.println("Printin post data: \n" + postData);
        connection.setRequestMethod("POST");
        connection.setRequestProperty(
            "Content-Type",
            "application/x-www-form-urlencoded");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        // send request
        DataOutputStream output =
            new DataOutputStream(connection.getOutputStream());
        try
        {
            output.writeBytes(postData);
            output.flush();
        }
        finally
        {
            output.close();
        }

        // check if login was successful by parsing response
        String response = readFromInputStream(connection.getInputStream());
        if (!response.contains("\"loggedIn\": true"))
        {
            System.out.println("Not logged in, printing response: \n" + response);
            throw new WrongLoginException();
        }
        return response;
    }


    private String readFromInputStream(InputStream stream)
        throws IOException
    {
        try
        {
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();
            StringBuilder builder = new StringBuilder(line);
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            return builder.toString();
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            stream.close();
        }
    }


    /**
     * Gets the current semester in the format "Spring/Fall 2xxx", i.e.
     * "Spring 2013"
     */
    protected String getSemester()
    {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        month = (month + 6) % 12; // advances months by 6 so 0 is effectively July
        String semester;
        if (month < 6)
        { // from July to December
            semester = "Fall " + c.get(Calendar.YEAR);
            System.out.println(semester);
            return semester;
        }
        else
        { // from January to June
            semester = "Spring " + c.get(Calendar.YEAR);
            return semester;
        }
    }

    /**
     * Creates a url encoded string of post data from a given map of properties
     *
     * @throws UnsupportedEncodingException
     */
    private String buildPostData(Map<String, String> properties)
        throws UnsupportedEncodingException
    {
        StringBuilder strBuilder = new StringBuilder();
        for (Entry<String, String> entry : properties.entrySet())
        {
            String value = entry.getValue();
            if (value == null) {
                value = ""; //TODO sometimes we get a null key value in processing, figure out
                            //why this happens (it happens pretty rarely)
                System.out.println("null key value found when building post data" +
                		           " for key " + entry.getKey());
            }
            strBuilder.append(entry.getKey()).append("=")
                .append(URLEncoder.encode(value, "UTF-8"))
                .append("&");
        }
        return strBuilder.toString();
    }


    /**
     * returns true if courses are successfully saved to internal storage, false
     * if not
     *
     * @throws IOException
     */
    private boolean saveCourses()
        throws IOException
    {
        File file = new File(context.getFilesDir(), "courses");

        file.createNewFile();
        FileOutputStream fout = new FileOutputStream(file);
        ObjectOutputStream objectStream = new ObjectOutputStream(fout);
        try
        {
            if (courses != null)
            {
                objectStream.writeObject(courses);
                System.out.println("courses were saved");
                return true;
            }
            else
            {
                System.out.println("courses were not saved");
                return false;
            }
        }
        finally
        {
            objectStream.close();
        }
    }

}
