package com.example.James.myapplication.backend;

/**
 * Created by James on 2/21/2017.
 */

import com.example.James.myapplication.backend.Data.Exercise;
import com.example.James.myapplication.backend.Data.ExerciseDataStore;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QueryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        ArrayList<Exercise> resultList = ExerciseDataStore.query();

        //start writing html page
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        //SITE HEADER
        out.write("<html>\n" +
                "<head>\n" +
                "\t\t<b>Exercise Entries For Your Device:</b>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n" +
                "</head>\n");
        out.write("<br /><body>\n"+"</b>---------------------------------------------------------" +
                "--------------------------------------------------------------------------------" +
                "-------------------------------------------------------------------------\n<br />");

        //TABLE HEADERS
        out.write("<table border ='1'>" +
                "<tr>" +
                "<th>ID</th>" +
                "<th>input</th>" +
                "<th>activity</th>" +
                "<th>dateTime</th>" +
                "<th>duration</th>" +
                "<th>distance</th>" +
                "<th>avgSpeed</th>" +
                "<th>calories</th>" +
                "<th>climb</th>" +
                "<th>heartRate</th>" +
                "<th>comment</th>" +
                "<th>        </th>" +
                "</tr>"
                );

        //TABLE BODY ROW ENTRIES
        if (resultList != null) {
            for(int i = 0; i<resultList.size(); i++){
                out.write("<tr>" +
                        "<td>" + resultList.get(i).mID + "</td>" +
                        "<td>" + resultList.get(i).mInput + "</td>" +
                        "<td>" + resultList.get(i).mActivity + "</td>" +
                        "<td>" + resultList.get(i).mDateTime + "</td>" +
                        "<td>" + resultList.get(i).mDuration + "</td>" +
                        "<td>" + resultList.get(i).mDistance + "</td>" +
                        "<td>" + resultList.get(i).mAverageSpeed + "</td>" +
                        "<td>" + resultList.get(i).mCalories + "</td>" +
                        "<td>" + resultList.get(i).mClimb+ "</td>" +
                        "<td>" + resultList.get(i).mHeartRate + "</td>" +
                        "<td>" + resultList.get(i).mComment + "</td>" +
                        "<td>" + "<input type=\"button\" onclick=\"location.href='/delete.do?name="
                        +resultList.get(i).mID+"'\" value=\"Delete\">" + "</td>" +
                        "</tr>"
                        );
            }
        }
        out.write("</table>");
        out.write("</body>\n" +
                "</html>");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}