package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.Command;
import ml.iks.md.service.CmdService;
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
public class CmdListMB implements Serializable {

    @Inject
    CmdService cmdService;

    Long id;

    LazyDataModel<Command> commands;

    Filter<Command> filter = new Filter<>(new Command());

    List<Command> selected; //cars selected in checkbox column

    List<Command> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        commands = new LazyDataModel<Command>() {
            @Override
            public List<Command> load(int first, int pageSize,
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
                List<Command> list = cmdService.paginate(filter);
                setRowCount((int) cmdService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Command getRowData(String key) {
                return cmdService.findById(Long.valueOf(key));
            }
        };
    }

    public void clear() {
        filter = new Filter<Command>(new Command());
    }

    public List<String> completeModel(String query) {
        List<String> result = new ArrayList<>();
        return result;
    }

    public void findById(Long id) {
        if (id == null) {
            throw new BusinessException("Provide Command ID to load");
        }
        selected.add(cmdService.findById(id));
    }

    public void delete() {
        int numCars = 0;
        for (Command selectedCar : selected) {
            numCars++;
            cmdService.remove(selectedCar);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public List<Command> getSelected() {
        return selected;
    }

    public List<Command> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Command> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<Command> selectedCars) {
        this.selected = selectedCars;
    }

    public LazyDataModel<Command> getCommands() {
        return commands;
    }

    public void setCommands(LazyDataModel<Command> cars) {
        this.commands = cars;
    }

    public Filter<Command> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Command> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
