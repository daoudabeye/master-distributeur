package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.events.AppCallbackManager;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.model.Car;
import ml.iks.md.models.Payment;
import ml.iks.md.models.RoutingTable;
import ml.iks.md.models.data.IncomingMessageType;
import ml.iks.md.service.AirtimeService;
import ml.iks.md.service.CarService;
import ml.iks.md.service.PaymentService;
import ml.iks.md.util.BeanLocator;
import ml.iks.md.util.Utils;
import ml.ikslib.gateway.AbstractGateway;
import ml.ikslib.gateway.Service;
import ml.ikslib.gateway.message.OutboundMessage;
import ml.ikslib.gateway.ussd.USSDRequest;
import ml.ikslib.gateway.ussd.USSDResponse;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class PaymentDataView implements Serializable {
//    private List<Payment> payments;

    private Payment selectedPayment;
    private Payment payement;

    @Inject
    private PaymentService service;

    Long id;

    String searchKey;

    LazyDataModel<Payment> payments;

    Filter<Payment> filter = new Filter<>(new Payment());

    List<Payment> selected; //cars selected in checkbox column

    List<Payment> filteredValue;// datatable filteredValue attribute (column filters)

    private String ussCmdGtw;
    private String ussdCmd;
    private String ussdrsp;

    @PostConstruct
    public void initDataModel() {
        payments = new LazyDataModel<Payment>() {
            @Override
            public List<Payment> load(int first, int pageSize,
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

                List<Payment> list = service.paginate(filter);
                setRowCount((int) service.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Payment getRowData(String key) {
                return service.findById(Long.valueOf(key));
            }
        };
    }

    public void initPayment(){
        payement = new Payment();
    }
    //**Special commande
    public void newCommand(){
        payement.setMessageId(1L);
        BeanLocator.find(AppCallbackManager.class).registerPaymentMessageEvent(null, payement);
    }

    public void executeUssd(){
        try {
            AbstractGateway g = Service.getInstance().getGatewayById(ussCmdGtw);
            if(StringUtils.isEmpty(ussCmdGtw) || g == null){
                throw new BusinessException("Modem Indisponible");
            }

            USSDRequest request = new USSDRequest(ussdCmd);
            USSDResponse resp = Service.getInstance().getGatewayById(ussCmdGtw).send(request);

            if(resp!= null){
                ussdrsp = resp.getRawResponse();
            }
        } catch (Exception e) {
            throw new BusinessException("Echec ussd", e.getLocalizedMessage());
        }
    }

    public void relance(){
        int num = 0;
        for (Payment selected : selected) {
            try {
                boolean success = BeanLocator.find(AirtimeService.class).relancePayment(selected.getId()+"");
                if (success)
                    Utils.addDetailMessage("Relance effectuées avec succès, veuillez lire les messages");
                else
                    throw new Exception("Echec de l'execution!!!");
                num++;
            } catch (Exception e) {
                new BusinessException("Erreur", e.getLocalizedMessage());
            }
        }
        selected.clear();
        Utils.addDetailMessage(num + " Paiement(s) relacés avec suucès!");
    }

    public void clear() {
        filter = new Filter<Payment>(new Payment());
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

    public void search(String searchKey){

    }

    public void delete() {
        int numCars = 0;
        for (Payment selectedCar : selected) {
            numCars++;
            service.remove(selectedCar);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public String getStatusClass(OutboundMessage.SentStatus status){
        if(status.equals(OutboundMessage.SentStatus.Sent))
            return "btn-success";

        if(status.equals(OutboundMessage.SentStatus.Failed))
            return "btn-danger";

        if(status.equals(OutboundMessage.SentStatus.Unsent))
            return "btn-warning";

        else
            return "btn-primary";
    }

    public String getMsgStatusClass(IncomingMessageType type){
        if(type == null)
            return "btn-primary";
        
        if(type.equals(IncomingMessageType.PAYMENT))
            return "btn-warning";

        if(type.equals(IncomingMessageType.COMMISSION))
            return "btn-danger";

        if(type.equals(IncomingMessageType.PAYMENT_ACK))
            return "btn-success";

        else
            return "btn-primary";
    }

    public List<Payment> getSelected() {
        return selected;
    }

    public List<Payment> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Payment> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<Payment> selectedCars) {
        this.selected = selectedCars;
    }

    public void setPayments(LazyDataModel<Payment> payments) {
        this.payments = payments;
    }

    public Filter<Payment> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Payment> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LazyDataModel<Payment> getPayments() {
        return payments;
    }

    public Payment getPayement() {
        return payement;
    }

    public void setService(PaymentService service) {
        this.service = service;
    }

    public String getUssCmdGtw() {
        return ussCmdGtw;
    }

    public void setUssCmdGtw(String ussCmdGtw) {
        this.ussCmdGtw = ussCmdGtw;
    }

    public String getUssdCmd() {
        return ussdCmd;
    }

    public void setUssdCmd(String ussCmd) {
        this.ussdCmd = ussCmd;
    }

    public String getUssdrsp() {
        return ussdrsp;
    }

    public void setUssdrsp(String ussdrsp) {
        this.ussdrsp = ussdrsp;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}
