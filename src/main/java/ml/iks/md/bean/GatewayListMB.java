package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.events.AppCallbackManager;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.*;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.service.AirtimeService;
import ml.iks.md.service.GatewayService;
import ml.iks.md.service.MsgPatternService;
import ml.iks.md.util.BeanLocator;
import ml.iks.md.util.Utils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by rmpestano on 12/02/17.
 */
@Named
@ViewScoped
public class GatewayListMB implements Serializable {

    @Inject
    GatewayService service;

    Long id;
    String gatewauyId;
    boolean start;

    LazyDataModel<GatewayDefinition> gatewayDefinitions;

    Filter<GatewayDefinition> filter = new Filter<>(new GatewayDefinition());

    List<GatewayDefinition> selected; //cars selected in checkbox column

    List<GatewayDefinition> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        gatewayDefinitions = new LazyDataModel<GatewayDefinition>() {
            @Override
            public List<GatewayDefinition> load(int first, int pageSize,
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
                List<GatewayDefinition> list = service.paginate(filter);
                setRowCount((int) service.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public GatewayDefinition getRowData(String key) {
                return service.findById(Long.valueOf(key));
            }
        };
    }

    public void clear() {
        filter = new Filter<GatewayDefinition>(new GatewayDefinition());
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

    public void findById(String id) {
        if (id == null) {
            throw new BusinessException("Provide Command ID to load");
        }
        selected.add(service.findById(id));
    }

    public void delete() {
        int numCars = 0;
        for (GatewayDefinition selectedCar : selected) {
            numCars++;
            service.remove(selectedCar);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public void start() {
        int count = 0;
        for (GatewayDefinition selectedItem : selected) {
            BeanLocator.find(GatewayService.class).startGateway(selectedItem);
            count++;
        }
        Utils.addDetailMessage(count + " gateways start successfully!");
    }

    public void stop() {
        int count = 0;
        for (GatewayDefinition selectedItem : selected) {
            BeanLocator.find(GatewayService.class).stopGateway(selectedItem.getGatewayId());
            count++;
        }
        Utils.addDetailMessage(count + " gateways start successfully!");
    }

    public String getStatus(String gatewauyId){
        if(BeanLocator.find(GatewayService.class).getGatewayById(gatewauyId) != null)
            return "Started";
        else
            return "Stoped";
    }

    public void clean(){
        for (GatewayDefinition selectedItem : selected) {
            try {
                BeanLocator.find(GatewayService.class).cleanGtw(selectedItem.getGatewayId());
                Utils.addDetailMessage("Nettoyage effectué !");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void teste(){
        //        AlertMaker.showTrayMessage("Teste", "Tray message content...");
        String txt = "78648004*500#";
        InMessage msg = new InMessage("96347412", "enc7", "Le 7864804 à effectué un paiement", new Date(), new Date(), "modem", Carrier.ORANGE, 1);
        msg.setId(1L);

        //ISAGO TESTE
        BeanLocator.find(AirtimeService.class).updateIsagoNumber("96347412", "9874563214568");

        //PAYMENT TESTE
//        Payment paymentML = new Payment(1L, "modem", "transactionId2", "96347412", 5000.0, 100.0);
//        BeanLocator.find(AppCallbackManager.class).registerPaymentMessageEvent(msg, paymentML);
//        BeanLocator.find(AirtimeService.class).doSmsAction(msg, "78648004", "1000", "");


        //INSCRIPTION TESTE
//        InscriptionOperation ins = new InscriptionOperation("Moussa", "96347412", "78648004");
//        ins.addNumber("96347412", NumProfile.CF);
//
//        BeanLocator.find(AppCallbackManager.class).registerInscriptionEvent(msg, ins);
    }

    public List<GatewayDefinition> getSelected() {
        return selected;
    }

    public List<GatewayDefinition> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<GatewayDefinition> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<GatewayDefinition> selectedCars) {
        this.selected = selectedCars;
    }

    public LazyDataModel<GatewayDefinition> getGatewayDefinitions() {
        return gatewayDefinitions;
    }

    public void setGatewayDefinitions(LazyDataModel<GatewayDefinition> cars) {
        this.gatewayDefinitions = cars;
    }

    public Filter<GatewayDefinition> getFilter() {
        return filter;
    }

    public void setFilter(Filter<GatewayDefinition> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GatewayService getService() {
        return service;
    }

    public void setService(GatewayService service) {
        this.service = service;
    }

    public String getGatewauyId() {
        return gatewauyId;
    }

    public void setGatewauyId(String gatewauyId) {
        this.gatewauyId = gatewauyId;
    }
}
