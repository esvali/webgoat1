package org.owasp.webgoat;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.owasp.webgoat.lessons.AbstractLesson;
import org.owasp.webgoat.session.Course;
import org.owasp.webgoat.session.WebSession;

/*******************************************************************************
 *
 * 주민등록번호
 * 김개똥: 801212-1063572
 * 홍길동: 780622-0187143
 *
 */

/*******************************************************************************
 *
 *
 * This file is part of WebGoat, an Open Web Application Security Project
 * utility. For details, please see http://www.owasp.org/
 *
 * Copyright (c) 2002 - 2007 Bruce Mayhew
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * Getting Source ==============
 *
 * Source for this application is maintained at code.google.com, a repository
 * for free software projects.
 *
 * For details, please see http://code.google.com/p/webgoat/
 *
 * @author     Bruce Mayhew <a href="http://code.google.com/p/webgoat">WebGoat</a>
 * @created    October 28, 2003
 */
public class LessonSource extends HammerHead
{

    /**
     *  Description of the Field
     */
    public final static String START_SOURCE_SKIP = "START_OMIT_SOURCE";

    public final static String END_SOURCE_SKIP = "END_OMIT_SOURCE";


    /**
     *  Description of the Method
     *
     * @param  request               Description of the Parameter
     * @param  response              Description of the Parameter
     * @exception  IOException       Description of the Exception
     * @exception  ServletException  Description of the Exception
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
    {
  String source = null;

  try
  {
      //System.out.println( "Entering doPost: " );
      //System.out.println( " - request "   + request);
      //System.out.println( " - principle: "   + request.getUserPrincipal() );
      //setCacheHeaders(response, 0);
      WebSession session = (WebSession) request.getSession(true)
        .getAttribute(WebSession.SESSION);
      session.update(request, response, this.getServletName()); // FIXME: Too much in this call.

      // Get the Java source of the lesson.  FIXME: Not needed
      source = getSource(session);

      int scr = session.getCurrentScreen();
      Course course = session.getCourse();
      AbstractLesson lesson = course.getLesson(session, scr,
        AbstractLesson.USER_ROLE);
      lesson.getLessonTracker(session).setViewedSource(true);
  }
  catch (Throwable t)
  {
      t.printStackTrace();
      log("ERROR: " + t);
  }
  finally
  {
      try
      {
    this.writeSource(source, response);
      }
      catch (Throwable thr)
      {
    thr.printStackTrace();
    log(request, "Could not write error screen: "
      + thr.getMessage());
      }
      //System.out.println( "Leaving doPost: " );

  }
    }


    protected WebSession updateSession_DELETEME(HttpServletRequest request,
      HttpServletResponse response, ServletContext context)
    {
  HttpSession hs;
  hs = request.getSession(true);

  //System.out.println( "Entering Session_id: " + hs.getId() );
  // dumpSession( hs );

  // Make a temporary session to avoid the concurreny issue
  // in WebSession
  WebSession session = new WebSession(this, context);

  WebSession realSession = null;
  Object o = hs.getAttribute(WebSession.SESSION);

  if ((o != null) && o instanceof WebSession)
  {
      realSession = (WebSession) o;
  }
  session.setCurrentScreen(realSession.getCurrentScreen());
  session.setCourse(realSession.getCourse());
  session.setRequest(request);

  // to authenticate
  //System.out.println( "Leaving Session_id: " + hs.getId() );
  //dumpSession( hs );
  return (session);
    }


    /**
     *  Description of the Method
     *
     * @param  s  Description of the Parameter
     * @return    Description of the Return Value
     */
    protected String getSource(WebSession s)
    {

  String source = null;
  int scr = s.getCurrentScreen();
  Course course = s.getCourse();

  if (s.isUser() || s.isChallenge())
  {

      AbstractLesson lesson = course.getLesson(s, scr,
        AbstractLesson.USER_ROLE);

      if (lesson != null)
      {
    source = lesson.getSource(s);
      }
  }
  if (source == null)
  {
      return "Source code is not available. Contact webgoat@g2-inc.com";
  }
  return (source.replaceAll("(?s)" + START_SOURCE_SKIP + ".*"
    + END_SOURCE_SKIP, "Code Section Deliberately Omitted"));
    }


    /**
     *  Description of the Method
     *
     * @param  s                Description of the Parameter
     * @param  response         Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    protected void writeSource(String s, HttpServletResponse response)
      throws IOException
    {
  response.setContentType("text/html");

  PrintWriter out = response.getWriter();

  if (s == null)
  {
      s = new String();
  }

  out.print(s);
  out.close();
    }
}
