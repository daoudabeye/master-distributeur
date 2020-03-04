package ml.iks.md.bean;

import com.github.adminfaces.template.exception.BusinessException;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.Client;
import ml.iks.md.models.InMessage;
import ml.iks.md.models.MessagePattern;
import ml.iks.md.models.Sim;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.repositories.ClientRepository;
import ml.iks.md.repositories.MessagePatternRepository;
import ml.iks.md.service.GatewayService;
import ml.iks.md.service.InMessageService;
import ml.iks.md.service.SimService;
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
public class InMessageListMB implements Serializable {

    @Inject
    InMessageService service;

    Long id;

    LazyDataModel<InMessage> messages;

    Filter<InMessage> filter = new Filter<>(new InMessage());

    List<InMessage> selected; //cars selected in checkbox column

    List<InMessage> filteredValue;// datatable filteredValue attribute (column filters)

    NumProfile profile;
    String numero;
    String text;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    @PostConstruct
    public void initDataModel() {
        messages = new LazyDataModel<InMessage>() {
            @Override
            public List<InMessage> load(int first, int pageSize,
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
                List<InMessage> list = service.paginate(filter);
                setRowCount((int) service.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public InMessage getRowData(String key) {
                return service.findById(Long.valueOf(key));
            }
        };
    }

    public void clear() {
        filter = new Filter<InMessage>(new InMessage());
    }

    public void findById(Long id) {
        if (id == null) {
            throw new BusinessException("Provide Command ID to load");
        }
        selected.add(service.findById(id));
    }

    public void delete() {
        int numCars = 0;
        for (InMessage selectedItem : selected) {
            numCars++;
            service.remove(selectedItem);
        }
        selected.clear();
        Utils.addDetailMessage(numCars + " cars deleted successfully!");
    }

    public List<InMessage> getSelected() {
        return selected;
    }

    public List<InMessage> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<InMessage> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelected(List<InMessage> selectedCars) {
        this.selected = selectedCars;
    }

    public LazyDataModel<InMessage> getMessages() {
        return messages;
    }

    public void setMessages(LazyDataModel<InMessage> cars) {
        this.messages = cars;
    }

    public Filter<InMessage> getFilter() {
        return filter;
    }

    public void setFilter(Filter<InMessage> filter) {
        this.filter = filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void execute() {
        List<MessagePattern> patterns = BeanLocator.find(MessagePatternRepository.class).findAll();
        for (InMessage inMessage : selected) {
            for (MessagePattern pattern : patterns) {
                boolean triggered = pattern.findAndTrigger(inMessage);
                if(triggered && !pattern.isForwarding())
                    break;
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Executed", "Using Remote Command."));
    }

    public void sendSMS(){
        if(StringUtils.isEmpty(numero))
            return;
        try {
            OutboundMessage out = new OutboundMessage(numero, text);
            out.setRequestDeliveryReport(true);

            Service.getInstance().send(out);
            BeanLocator.find(GatewayService.class).saveMessage(out);
        } catch (Exception e){
            new BusinessException(e);
        }
    }

    public void sendToGroup() {
        if(profile == null)
            return;

        try {
            OutboundMessage out = new OutboundMessage(profile.name(), text);
            out.setRequestDeliveryReport(true);

            Group groupe = addGroup();

            Group existing = Service.getInstance().getGroupManager().getGroup(profile.name());
            if(existing != null)
                Service.getInstance().removeGroup(existing);

            Service.getInstance().addGroup(groupe);
            LinkedList<OutboundMessage> msg =  Service.getInstance().distributeToGroup(out);

            for (OutboundMessage outboundMessage : msg) {
                executor.submit(() -> {
                    Service.getInstance().send(outboundMessage);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    private Group addGroup() {
        List<Client> client = BeanLocator.find(ClientRepository.class).findByProfile(profile);
        List<String> number = new ArrayList<String>();

        Group groupe = new Group(profile.name(), "");
        for (Client client1 : client) {
            if(AppConstants.getOperator(client1.getNumero()) == Carrier.ORANGE)
                groupe.addAddress(new MsIsdn(client1.getNumero(), MsIsdn.Type.National));
        }
        return groupe;
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
