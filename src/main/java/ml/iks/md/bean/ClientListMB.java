package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.Client;
import ml.iks.md.models.MobileNumber;
import ml.iks.md.repositories.MobileNumberRepository;
import ml.iks.md.service.ClientService;
import ml.iks.md.service.CmdService;
import ml.iks.md.service.MobileNumberService;
import ml.iks.md.util.Utils;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Created by rmpestano on 12/02/17.
 */
@Named
@ViewScoped
public class ClientListMB implements Serializable {

    @Inject
    ClientService service;
    @Inject
    MobileNumberService mobileNumberService;

    Long id;

    LazyDataModel<Client> clients;

    Filter<Client> filter = new Filter<>(new Client());

    List<Client> selected; //cars selected in checkbox column

    List<Client> filteredValue;// datatable filteredValue attribute (column filters)

    MobileNumber mobileNumber;

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
                List<Client> list = service.paginate(filter, false);
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

    public void findById(Long id) {
        if (id == null) {
            throw new BusinessException("Provide Client ID to load");
        }
        selected.add(service.findById(id));
    }

    public void delete() {
        int numCars = 0;
        for (Client item : selected) {
            numCars++;
            service.deleteClient(item);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public List<MobileNumber> getMobiles(Long id){
        return mobileNumberService.findForClient(id);
    }

    public void initMobileNumber(){
        this.mobileNumber = new MobileNumber();
    }

    public void removeMobileNumber(Long id) throws IOException {
        mobileNumberService.remove(id);
        Utils.addDetailMessage("Mobile removed successfully");
    }

    public void saveMobileNumber() throws IOException {
        String msg;
        Client cl = selected.get(0);
        mobileNumber.setClient(cl);
        mobileNumberService.save(mobileNumber);
        msg = "Mobile Number  " + mobileNumber.getNumber() + " added successfully";
        Utils.addDetailMessage(msg);
    }

    public List<Client> getSelected() {
        return selected;
    }

    public List<Client> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Client> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<Client> selectedCars) {
        this.selected = selectedCars;
    }

    public LazyDataModel<Client> getClients() {
        return clients;
    }

    public void setClients(LazyDataModel<Client> cars) {
        this.clients = cars;
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

    public MobileNumber getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(MobileNumber mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
