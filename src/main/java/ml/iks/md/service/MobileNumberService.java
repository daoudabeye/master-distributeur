package ml.iks.md.service;

import ml.iks.md.models.MobileNumber;
import ml.iks.md.repositories.MobileNumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class MobileNumberService {

    @Autowired
    MobileNumberRepository repository;


    public void save(MobileNumber mobileNumber) {
        repository.save(mobileNumber);
    }

    public void remove(Long id) {
        repository.deleteById(id);
    }

    public List<MobileNumber> findForClient(Long id) {
        return repository.findByClientId(id);
    }
}
