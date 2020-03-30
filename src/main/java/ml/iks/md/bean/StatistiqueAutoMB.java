package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.Client;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.service.ClientService;
import ml.iks.md.util.AppUtil;
import ml.iks.md.util.Utils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Created by rmpestano on 12/02/17.
 */
@Named
@ViewScoped
public class StatistiqueAutoMB implements Serializable {

    @Inject
    ClientService service;

    Long id;

    LazyDataModel<Client> clients;

    Filter<Client> filter = new Filter<>(new Client());

    Client selected; //cars selected in checkbox column

    List<Client> filteredValue;// datatable filteredValue attribute (column filters)

    Date from, to;
    NumProfile profile;
    String numero;

    @PostConstruct
    public void initDataModel() {
        clients = new LazyDataModel<Client>() {
            @Override
            public List<Client> load(int first, int pageSize,
                                  String sortField, SortOrder sortOrder,
                                  Map<String, Object> filters) {
                ml.iks.md.infra.model.SortOrder order = null;
                if (sortOrder != null) {
                    order = sortOrder.equals(SortOrder.ASCENDING) ? ml.iks.md.infra.model.SortOrder.ASCENDING
                            : sortOrder.equals(SortOrder.DESCENDING) ? ml.iks.md.infra.model.SortOrder.DESCENDING
                            : ml.iks.md.infra.model.SortOrder.UNSORTED;
                }
                filter.setFirst(first).setPageSize(pageSize)
                        .setSortField(sortField).setSortOrder(order)
                        .setParams(filters);

                List<Client> list = service.paginate(filter, true);
                setRowCount((int) service.count(filter));

                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Client getRowData(String key) {
                return service.findById(Long.valueOf(key));
            }
        };
    }

    public void clear() {
        filter = new Filter<Client>(new Client());
    }

    public List<String> completeModel(String query) {
        List<String> result = new ArrayList<>();
        return result;
    }

    public List<Client> getSbclients(){
        if(selected == null)
            return new ArrayList<>();
        return service.findClientByInscriptor(selected);
    }

    public void findById(Long id) {

    }

    public void delete() {

    }

    public void getSate(){

    }

    public Client getSelected() {
        return selected;
    }

    public List<Client> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Client> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(Client selected) {
        this.selected = selected;
    }

    public LazyDataModel<Client> getClients() {
        return clients;
    }

    public void setClients(LazyDataModel<Client> client) {
        this.clients = client;
    }

    public Filter<Client> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Client> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public NumProfile getProfile() {
        return profile;
    }

    public void setProfile(NumProfile profile) {
        this.profile = profile;
    }
}
