package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.Command;
import ml.iks.md.models.RoutingTable;
import ml.iks.md.service.CmdService;
import ml.iks.md.service.RouteService;
import ml.iks.md.util.Utils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.simpleframework.http.socket.service.Router;

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
public class RouteListMB implements Serializable {

    @Inject
    RouteService service;

    Long id;

    LazyDataModel<RoutingTable> routingTables;

    Filter<RoutingTable> filter = new Filter<>(new RoutingTable());

    List<RoutingTable> selected; //cars selected in checkbox column

    List<RoutingTable> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        routingTables = new LazyDataModel<RoutingTable>() {
            @Override
            public List<RoutingTable> load(int first, int pageSize,
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
                List<RoutingTable> list = service.paginate(filter);
                setRowCount((int) service.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public RoutingTable getRowData(String key) {
                return service.findById(Long.valueOf(key));
            }
        };
    }

    public void clear() {
        filter = new Filter<RoutingTable>(new RoutingTable());
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
        for (RoutingTable selectedCar : selected) {
            numCars++;
            service.remove(selectedCar);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public List<RoutingTable> getSelected() {
        return selected;
    }

    public List<RoutingTable> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<RoutingTable> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<RoutingTable> selectedCars) {
        this.selected = selectedCars;
    }

    public LazyDataModel<RoutingTable> getRoutingTables() {
        return routingTables;
    }

    public void setRoutingTables(LazyDataModel<RoutingTable> cars) {
        this.routingTables = cars;
    }

    public Filter<RoutingTable> getFilter() {
        return filter;
    }

    public void setFilter(Filter<RoutingTable> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
