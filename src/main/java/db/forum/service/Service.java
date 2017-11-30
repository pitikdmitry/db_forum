package db.forum.service;

import db.forum.model.ServiceModel;
import db.forum.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@org.springframework.stereotype.Service
public class Service {

    private final ServiceRepository serviceRepository;

    @Autowired
    public Service(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public ResponseEntity<?> status() {
        try{
            ServiceModel responseService = serviceRepository.status();
            return new ResponseEntity<>(responseService.getJson().toString(), HttpStatus.OK);
        } catch(Exception ex) {
            //ign
        }
        return null;
    }

    public ResponseEntity<?> clear() {
        try{
            serviceRepository.clear();
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch(Exception ex) {
            //ign
            return new ResponseEntity<>("", HttpStatus.BAD_GATEWAY);
        }
    }
}
