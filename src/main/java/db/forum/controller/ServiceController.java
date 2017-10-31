package db.forum.controller;

import db.forum.service.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service/")
public class ServiceController {
    private Service service;

    public ServiceController(Service service) { this.service = service; }

    @RequestMapping(value = "/status", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<?> status() {

        return service.status();
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity<?> clear() {

        return service.clear();
    }
}
