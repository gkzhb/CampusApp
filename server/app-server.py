# test for app server

from twisted.web import http


def renderHomePage(request):
    grades = 18, 17, 16, 15
    genders = 'male', 'female'
    request.write("""
    <html>
    <head>
      <title>Form Student Test</title
    </head>
    <body>
      <form action='posthandler' method='post'>
        Your name:
        <p>
          <input type='text' name='name'>
        </p>
        Your student ID:
        <p>
          <input type='text' name='stuid'>
        </p>
        Your password:
        <p>
          <input type='password' name='password'>
        </p>
        What's your gender?
        <p>
    """)
    for gender in genders:
        request.write(
            "<input type='radio' name='gender' value='%s'>%s<br />" % (
            gender, gender.capitalize()))
    request.write("""
        </p>
        What's your grade?
        <p>
        """)
    for grade in grades:
        request.write(
            "<input type='radio' name='grade' value='%d'>%d<br />" % (
            grade, grade))
    request.write("""
        </p>
        <input type='submit' value='Submit' />
      </form>
    </body>
    </html>
    """)
    request.finish()


def handlePost(request):
    request.write("""
    <html>
      <head>
        <title>Posted Form Data</title>
      </head>
      <body>
      <h1>Form Data</h1>
    """)

    for key, values in request.args.items():
        request.write("<h2>%s</h2>" % key)
        request.write("<ul>")
        for value in values:
            request.write("<li>%s</li>" % value)
        request.write("</ul>")

    request.write("""
       </body>
    </html>
    """)
    request.finish()


class FunctionHandledRequest(http.Request):
    pageHandlers = {
        '/': renderHomePage,
        '/posthandler': handlePost,
        }

    def process(self):
        self.setHeader('Content-Type', 'text/html')
        if self.pageHandlers.has_key(self.path):
            handler = self.pageHandlers[self.path]
            handler(self)
        else:
            self.setResponseCode(http.NOT_FOUND)
            self.write("<h1>Not Found</h1>Sorry, no such page.")
            self.finish()


class MyHttp(http.HTTPChannel):
    requestFactory = FunctionHandledRequest


class MyHttpFactory(http.HTTPFactory):
    protocol = MyHttp


if __name__ == "__main__":
    from twisted.internet import reactor
    reactor.listenTCP(8000, MyHttpFactory())
    reactor.run()
