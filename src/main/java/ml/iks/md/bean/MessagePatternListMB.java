package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.MessagePattern;
import ml.iks.md.models.RoutingTable;
import ml.iks.md.service.MsgPatternService;
import ml.iks.md.service.RouteService;
import ml.iks.md.util.Utils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rmpestano on 12/02/17.
 */
@Named
@ViewScoped
public class MessagePatternListMB implements Serializable {

    @Inject
    MsgPatternService service;

    Long id;

    LazyDataModel<MessagePattern> messagePatterns;

    Filter<MessagePattern> filter = new Filter<>(new MessagePattern());

    List<MessagePattern> selected; //cars selected in checkbox column

    List<MessagePattern> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        messagePatterns = new LazyDataModel<MessagePattern>() {
            @Override
            public List<MessagePattern> load(int first, int pageSize,
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
                List<MessagePattern> list = service.paginate(filter);
                setRowCount((int) service.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public MessagePattern getRowData(String key) {
                return service.findById(Long.valueOf(key));
            }
        };
    }

    public void clear() {
        filter = new Filter<MessagePattern>(new MessagePattern());
    }

    public List<String> completeModel(String query) {
        List<String> result = new ArrayList<>();
        return result;
    }

    public void findById(Long id) {
        if (id == null) {
            throw new BusinessException("Provide Command ID to load");
        }
        selected.add(service.findById(id));
    }

    public void delete() {
        int numCars = 0;
        for (MessagePattern selectedCar : selected) {
            numCars++;
            service.remove(selectedCar);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public List<MessagePattern> getSelected() {
        return selected;
    }

    public List<MessagePattern> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<MessagePattern> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<MessagePattern> selectedCars) {
        this.selected = selectedCars;
    }

    public LazyDataModel<MessagePattern> getMessagePatterns() {
        return messagePatterns;
    }

    public void setMessagePatterns(LazyDataModel<MessagePattern> cars) {
        this.messagePatterns = cars;
    }

    public Filter<MessagePattern> getFilter() {
        return filter;
    }

    public void setFilter(Filter<MessagePattern> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
