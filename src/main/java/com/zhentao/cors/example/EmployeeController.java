package com.zhentao.cors.example;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.zhentao.cors.example.exception.EmployeeExistsException;
import com.zhentao.cors.example.exception.EmployeeNotExistsException;
import com.zhentao.cors.example.exception.IdNotMatchException;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private static ConcurrentHashMap<Long, Employee> employees = new ConcurrentHashMap<Long, Employee>();

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Employee> get(@PathVariable long id) {
        Employee employee = employees.get(id);
        if (employee == null) {
            throw new EmployeeNotExistsException();
        }
        HttpHeaders headers = addAccessControllAllowOrigin();
        ResponseEntity<Employee> entity = new ResponseEntity<Employee>(employee, headers, HttpStatus.OK);
        return entity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    public ResponseEntity<Employee> update(@RequestBody Employee employee, @PathVariable long id) {
        if (employee.getId() != id) {
            throw new IdNotMatchException();
        }
        employees.put(id, employee);
        HttpHeaders headers = addAccessControllAllowOrigin();
        ResponseEntity<Employee> entity = new ResponseEntity<Employee>(headers, HttpStatus.OK);
        return entity;

    }

    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" })
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        if (employees.containsKey(employee.getId())) {
            throw new EmployeeExistsException();
        }
        employees.put(employee.getId(), employee);
        HttpHeaders headers = addAccessControllAllowOrigin();
        // This is required for CORS request to retrieve Location header
        headers.add("Access-Control-Expose-Headers", "Location");
        URI newLocation = ControllerLinkBuilder.linkTo(EmployeeController.class).slash(employee.getId()).toUri();
        headers.setLocation(newLocation);
        ResponseEntity<Employee> entity = new ResponseEntity<Employee>(headers, HttpStatus.CREATED);
        return entity;
    }

    private HttpHeaders addAccessControllAllowOrigin() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        return headers;
    }

    /**
     * CORS isn't enabled for delete method since no Access-Control-Allow-Origin is set in header
     *
     * @param id
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable long id) {
        if (!employees.containsKey(id)) {
            throw new EmployeeNotExistsException();
        }
        employees.remove(id);
    }
}
