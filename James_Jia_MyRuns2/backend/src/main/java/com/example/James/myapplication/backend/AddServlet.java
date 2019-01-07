package com.example.James.myapplication.backend;

/**
 * Created by James on 2/22/2017.
 */

import com.example.James.myapplication.backend.Data.Exercise;
import com.example.James.myapplication.backend.Data.ExerciseDataStore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;

public class AddServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        //get all the relevant string parameters
        String id = req.getParameter("id");
        String input_type = req.getParameter("input");
        String activity_type = req.getParameter("activity");
        String date = req.getParameter("date");
        String duration = req.getParameter("duration");
        String distance = req.getParameter("distance");
        String speed = req.getParameter("avgSpeed");
        String calories = req.getParameter("calories");
        String climb = req.getParameter("climb");
        String heartrate = req.getParameter("heartRate");
        String comment = req.getParameter("comment");

        //wrap parameters in a new exercise model
        Exercise entity = new Exercise(id, input_type, activity_type, date, duration, distance,
                                        speed, calories, climb, heartrate, comment);
        //add to the datastore
        ExerciseDataStore.add(entity);

        //redirect to query to redraw the html page and update with the new entry
        resp.sendRedirect("/query.do");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doPost(req, resp);
    }
}