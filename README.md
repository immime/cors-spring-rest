An example to show how to enable CORS with spring rest api.

This example supports both tomcat and jetty maven plugin.

1. start tomcat:

	mvn clean tomcat7:run

2. start jetty:

	mvn clean jetty:run

Access the following urls for testing CORS (ajax calls localhost):

	http://127.0.0.1:8080/create.html (add a new employee Alice with id 1)
	http://127.0.0.1:8080/get.html (retrieve employee with id 1)
	http://127.0.0.1:8080/update.html (update employee Alice to John)
	http://127.0.0.1:8080/delete.html (delete employee id 1)

This example tries to show that you can enable/disable CORS support on individual methods by setting header
Access-Control-Allow-Origin in each method.

Delete method doesn't return header Access-Control-Allow-Origin with *, so delete.html show alert with errors. 
Chrome browser displays:

	"cannot load http://localhost:8080/rest/employee/1. Origin http://127.0.0.1:8080 is not allowed by Access-Control-Allow-Origin."
	
However, the call of delete is still invoked on the server side.  The reason is that the pre-flight request (OPTIONS) 
allows DELETE method to be called. 
