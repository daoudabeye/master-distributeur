package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.Client;
import ml.iks.md.models.OutMessage;
import ml.iks.md.models.MessagePattern;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.repositories.ClientRepository;
import ml.iks.md.repositories.MessagePatternRepository;
import ml.iks.md.service.GatewayService;
import ml.iks.md.service.OutMessageService;
import ml.iks.md.service.OutMessageService;
import ml.iks.md.util.AppConstants;
import ml.iks.md.util.BeanLocator;
import ml.iks.md.util.Utils;
import ml.ikslib.gateway.Service;
import ml.ikslib.gateway.groups.Group;
import ml.ikslib.gateway.message.MsIsdn;
import ml.ikslib.gateway.message.OutboundMessage;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by rmpestano on 12/02/17.
 */
@Named
@ViewScoped
public class OutMessageListMB implements Serializable {

    @Inject
    OutMessageService service;

    Long id;

    LazyDataModel<OutMessage> messages;

    Filter<OutMessage> filter = new Filter<>(new OutMessage());

    List<OutMessage> selected; //cars selected in checkbox column

    List<OutMessage> filteredValue;// datatable filteredValue attribute (column filters)

    NumProfile profile;
    String numero;
    String text;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    @PostConstruct
    public void initDataModel() {
        messages = new LazyDataModel<OutMessage>() {
            @Override
            public List<OutMessage> load(int first, int pageSize,
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
                List<OutMessage> list = service.paginate(filter);
                setRowCount((int) service.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public OutMessage getRowData(String key) {
                return service.findById(Long.valueOf(key));
            }
        };
    }

    public void clear() {
        filter = new Filter<OutMessage>(new OutMessage());
    }

    public void findById(Long id) {
        if (id == null) {
            throw new BusinessException("Provide Command ID to load");
        }
        selected.add(service.findById(id));
    }

    public void delete() {
        int numCars = 0;
        for (OutMessage selectedItem : selected) {
            numCars++;
            service.remove(selectedItem);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public List<OutMessage> getSelected() {
        return selected;
    }

    public List<OutMessage> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<OutMessage> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<OutMessage> selectedCars) {
        this.selected = selectedCars;
    }

    public LazyDataModel<OutMessage> getMessages() {
        return messages;
    }

    public void setMessages(LazyDataModel<OutMessage> cars) {
        this.messages = cars;
    }

    public Filter<OutMessage> getFilter() {
        return filter;
    }

    public void setFilter(Filter<OutMessage> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NumProfile getProfile() {
        return profile;
    }

    public void setProfile(NumProfile profile) {
        this.profile = profile;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
