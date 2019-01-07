package com.example.James.myapplication.backend;

/**
 * Created by James on 2/22/2017.
 */

import com.example.James.myapplication.backend.Data.ExerciseDataStore;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//deletes a specific entry from the datastore and notifies the phone app about the deletion
public class DeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        //get id for entry to be deleted
        String name = req.getParameter("name");
        ExerciseDataStore.delete(name);

        //send message to app with the id of the entry to be deleted from history
        MessagingEndpoint message = new MessagingEndpoint();
        message.sendMessage(name);

        //remake the html page
        resp.sendRedirect("/query.do");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}
