package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.GatewayDefinition;
import ml.iks.md.models.Sim;
import ml.iks.md.service.GatewayService;
import ml.iks.md.service.SimService;
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
public class SimListMB implements Serializable {

    @Inject
    SimService service;

    Long id;

    LazyDataModel<Sim> sims;

    Filter<Sim> filter = new Filter<>(new Sim());

    List<Sim> selected; //cars selected in checkbox column

    List<Sim> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        sims = new LazyDataModel<Sim>() {
            @Override
            public List<Sim> load(int first, int pageSize,
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
                List<Sim> list = service.paginate(filter);
                setRowCount((int) service.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Sim getRowData(String key) {
                return service.findById(Long.valueOf(key));
            }
        };
    }

    public void clear() {
        filter = new Filter<Sim>(new Sim());
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
        for (Sim selectedCar : selected) {
            numCars++;
            service.remove(selectedCar);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public List<Sim> getSelected() {
        return selected;
    }

    public List<Sim> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Sim> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<Sim> selectedCars) {
        this.selected = selectedCars;
    }

    public LazyDataModel<Sim> getSims() {
        return sims;
    }

    public void setSims(LazyDataModel<Sim> cars) {
        this.sims = cars;
    }

    public Filter<Sim> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Sim> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
