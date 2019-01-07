package com.example.James.myapplication.backend;

/**
 * Created by James on 2/22/2017.
 */

import com.example.James.myapplication.backend.Data.ExerciseDataStore;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

//clear out the datastore
public class DeleteAllServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        ExerciseDataStore.deleteAll();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doPost(req, resp);
    }
}
