package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.Client;
import ml.iks.md.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StatistiqueService implements Serializable {

    @Autowired
    ClientRepository repository;


}
